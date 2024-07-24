#!/bin/bash

#===========================================================================================
# Java Environment Setting
#===========================================================================================
error_exit ()
{
    echo "ERROR: $1 !!"
    exit 1
}

find_java_home()
{
    case "`uname`" in
        Darwin)
            JAVA_HOME=$(/usr/libexec/java_home)
        ;;
        *)
            JAVA_HOME=$(dirname $(dirname $(readlink -f $(which javac))))
        ;;
    esac
}

choose_gc_options()
{
    # Example of JAVA_MAJOR_VERSION value : '1', '9', '10', '11', ...
    # '1' means releases befor Java 9
    JAVA_MAJOR_VERSION=$("$JAVA" -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{print $1}')
    if [ -z "$JAVA_MAJOR_VERSION" ] || [ "$JAVA_MAJOR_VERSION" -lt "9" ] ; then
      JAVA_OPT="${JAVA_OPT} -server -Xms512m -Xm512m -Xmn256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m"
      JAVA_OPT="${JAVA_OPT} -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:CMSInitiatingOccupancyFraction=70 -XX:+CMSParallelRemarkEnabled -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+CMSClassUnloadingEnabled -XX:SurvivorRatio=8 -XX:-UseParNewGC"
    else
      JAVA_OPT="${JAVA_OPT} -server -Xms512m -Xmx512m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m"
      JAVA_OPT="${JAVA_OPT} -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercent=25 -XX:InitiatingHeapOccupancyPercent=30 -XX:SoftRefLRUPolicyMSPerMB=0"
    fi
}

find_java_home

[ ! -e "$JAVA_HOME/bin/java" ] && JAVA_HOME=$HOME/jdk/java
[ ! -e "$JAVA_HOME/bin/java" ] && JAVA_HOME=/usr/java
[ ! -e "$JAVA_HOME/bin/java" ] && error_exit "Please set the JAVA_HOME variable in your environment, We need java(x64)!"

export JAVA_HOME
export JAVA="$JAVA_HOME/bin/java"
export BASE_DIR=$(dirname $0)
export CLASSPATH=.:${BASE_DIR}/conf:${BASE_DIR}/lib/*:${CLASSPATH}
echo ${CLASSPATH}

#===========================================================================================
# JVM Configuration
#===========================================================================================

choose_gc_options

# Dynamically calculate parameters, for reference.
# Set for `JAVA_OPT`.
JAVA_OPT="${JAVA_OPT} -XX:-OmitStackTraceInFastThrow"
JAVA_OPT="${JAVA_OPT} -XX:-UseLargePages"
JAVA_OPT="${JAVA_OPT} ${JAVA_OPT_EXT}"
JAVA_OPT="${JAVA_OPT} -cp ${CLASSPATH}"
JAVA_OPT="${JAVA_OPT} -Dserver.port=${DSERVER_PORT:-8858} -Dcsp.sentinel.dashboard.server=${DSERVER_ADDR:-localhost:8858}"
JAVA_OPT="${JAVA_OPT} -Dcsp.sentinel.api.port=${API_PORT:-8719}"
JAVA_OPT="${JAVA_OPT} -Dsentinel.dashboard.auth.username=${SENTINEL_USERNAME:-sentinel} -Dsentinel.dashboard.auth.password=${SENTINEL_PASSWORD:-sentinel}"
JAVA_OPT="${JAVA_OPT} -Dproject.name=sentinel-dashboard"

$JAVA ${JAVA_OPT} -jar sentinel-dashboard.jar