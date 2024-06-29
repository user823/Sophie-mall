#!/bin/bash

# 检查传入的参数数量
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <jar_file>"
    exit 1
fi

target="sophie-mall-$1/target/sophie-mall-$1-1.0-SNAPSHOT.jar"
# 检查传入的参数是否是一个文件
if [ ! -f ${target} ]; then
    echo "$1 is not a valid file"
    exit 1
fi

/opt/jdk-17.0.2/bin/java -jar ${target}
