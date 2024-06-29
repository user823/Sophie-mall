#!/bin/bash
# 使用脚本前确保path 路径下能找到wrk 和 guplot 画图

root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd -P)"
wrkdir="${root}/_output/wrk"
jobname="sophie-mall"
duration="300s"
threads=$((3 * `grep -c processor /proc/cpuinfo`))
luaScript="${root}/scripts/lua/loadtest.lua"
token=""

source "${root}/scripts/lib/color.sh"

# 设置并发数
sophie::wrk::setup() {
  concurrent="200 500 1000 3000 5000 10000 15000 20000 25000 50000"
  cmd="wrk -t${threads} -d${duration} -T30s --latency -s ${luaScript}"
  if [ -n "${token}" ]; then
    cmd="${cmd} -H \"Authorization: ${token}\""
  fi
}

sophie::wrk::usage() {
  cat << EOF

  Usage: $0 [OPTION] URL [arg1] [arg2] ...
  Performance automation test script. arg1, arg2 ... are extra parameters for http request.

    URL                    HTTP request url, like: http://127.0.0.1:8080/healthz

  OPTIONS:
    -h                     Usage information
    -n                     Performance test task name, default: sophie-mall
    -d                     Directory used to store performance data and gnuplot graphic, default: _output/wrk
    -a                     Token for http request
EOF
}

function sophie::wrk::convert_plot_data() {
  echo "$1" | awk -v datfile="${wrkdir}/${datfile}" ' {
  if ($0 ~ "Running") {
    common_time=$2
  }
if ($0 ~ "connections") {
  connections=$4
  common_threads=$1
}
if ($0 ~ "Latency   ") {
  avg_latency=convertLatency($2)
}
if ($0 ~ "50%") {
  p50=convertLatency($2)
}
if ($0 ~ "75%") {
  p75=convertLatency($2)
}
if ($0 ~ "90%") {
  p90=convertLatency($2)
}
if ($0 ~ "99%") {
  p99=convertLatency($2)
}
if ($0 ~ "Requests/sec") {
  qps=$2
}
if ($0 ~ "all") {
  rate=$7
}
}
END {
rate=sprintf("%.2f", rate*100)
print connections,qps,avg_latency,rate >> datfile
}

function convertLatency(s) {
  if (s ~ "us") {
    sub("us", "", s)
    return s/1000
  }
if (s ~ "ms") {
  sub("ms", "", s)
  return s
}
if (s ~ "s") {
  sub("s", "", s)
  return s * 1000
}
}'
}

function sophie::wrk::prepare()
{
  rm -f ${wrkdir}/${datfile}
}

# Plot according to gunplot data file
function sophie::wrk::plot() {
  gnuplot <<  EOF
set terminal png enhanced #输出格式为png文件
set ylabel 'QPS'
set xlabel 'Concurrent'
set y2label 'Average Latency (ms)'
set key top left vertical noreverse spacing 1.2 box
set tics out nomirror
set border 3 front
set style line 1 linecolor rgb '#00ff00' linewidth 2 linetype 3 pointtype 2
set style line 2 linecolor rgb '#ff0000' linewidth 1 linetype 3 pointtype 2
set style data linespoints

set grid #显示网格
set xtics nomirror rotate #by 90#只需要一个x轴
set mxtics 5
set mytics 5 #可以增加分刻度
set ytics nomirror
set y2tics

set autoscale  y
set autoscale y2

set output "${wrkdir}/${qpsttlb}"  #指定数据文件名称
set title "QPS & TTLB\nRunning: ${duration}\nThreads: ${threads}"
plot "${wrkdir}/${datfile}" using 2:xticlabels(1) w lp pt 7 ps 1 lc rgbcolor "#EE0000" axis x1y1 t "QPS","${wrkdir}/${datfile}" using 3:xticlabels(1) w lp pt 5 ps 1 lc rgbcolor "#0000CD" axis x2y2 t "Avg Latency (ms)"

unset y2tics
unset y2label
set ytics nomirror
set yrange[0:100]
set output "${wrkdir}/${successrate}"  #指定数据文件名称
set title "Success Rate\nRunning: ${duration}\nThreads: ${threads}"
plot "${wrkdir}/${datfile}" using 4:xticlabels(1) w lp pt 7 ps 1 lc rgbcolor "#F62817" t "Success Rate"
EOF
}

# Start API performance testing
sophie::wrk::start_performance_test() {
  sophie::wrk::prepare

  for c in ${concurrent}
  do
    wrkcmd="${cmd} -c ${c} $1 $2"
    echo "Running wrk command: ${wrkcmd}"
    result=`eval ${wrkcmd}`
    echo "${result}"
    sophie::wrk::convert_plot_data "${result}"
  done

  echo -e "\nNow plot according to ${COLOR_MAGENTA}${wrkdir}/${datfile}${COLOR_NORMAL}"
  sophie::wrk::plot &> /dev/null
  echo -e "QPS graphic file is: ${COLOR_MAGENTA}${wrkdir}/${qpsttlb}${COLOR_NORMAL}
Success rate graphic file is: ${COLOR_MAGENTA}${wrkdir}/${successrate}${COLOR_NORMAL}"
}

while getopts "a:hd:n:" opt;do
  case ${opt} in
    d)
      wrkdir=${OPTARG}
      ;;
    n)
      jobname=${OPTARG}
      ;;
    a)
      token="Bearer ${OPTARG}"
      ;;
    ?)
      sophie::wrk::usage
      exit 0
      ;;
  esac
done

# 将已处理的选项参数移除
shift $(($OPTIND-1))

if [ "$#" -lt 1 ];then
  sophie::wrk::usage
  exit 0
fi

url="$1"
shift
args=("$*")

mkdir -p ${wrkdir}

qpsttlb="${jobname}_qps_ttlb.png"
successrate="${jobname}_successrate.png"
datfile="${jobname}.dat"

sophie::wrk::setup
sophie::wrk::start_performance_test "${url}" "${args}"


