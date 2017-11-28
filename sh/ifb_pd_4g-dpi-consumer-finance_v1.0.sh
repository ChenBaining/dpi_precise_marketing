#!/bin/sh
baseDirForScriptSelf=$(cd "$(dirname "$0")"; pwd)

queueName=$1
app=`grep "appJar" ${baseDirForScriptSelf}/config.json | awk -F":" '{print $2}' |sed  -e 's/"//g' -e 's/,/ /g'`
config="${baseDirForScriptSelf}/config.json"
logConfig="file://${baseDirForScriptSelf}/log4j.properties"
url_config="${baseDirForScriptSelf}/UrlConfig.json"
jarpath="${baseDirForScriptSelf}/commons-codec-1.9.jar,${baseDirForScriptSelf}/commons-logging-1.2.jar,${baseDirForScriptSelf}/gson-2.8.1.jar"

numExecutors=`grep "numExecutors" ${baseDirForScriptSelf}/config.json | awk -F":" '{print $2}' |sed  -e 's/"//g' -e 's/,/ /g'`
executorMemory=`grep "executorMemory" ${baseDirForScriptSelf}/config.json | awk -F":" '{print $2}' |sed -e 's/"//g' -e 's/,/ /g' `
executorCores=`grep "executorCores" ${baseDirForScriptSelf}/config.json | awk -F":" '{print $2}' |sed  -e 's/"//g' -e 's/,/ /g'`
driverMemory=`grep "driverMemory" ${baseDirForScriptSelf}/config.json | awk -F":" '{print $2}' |sed -e 's/"//g' -e 's/,/ /g' `
storageFraction=`grep "storageFraction" ${baseDirForScriptSelf}/config.json | awk -F":" '{print $2}' |sed -e 's/"//g' -e 's/,/ /g' `
memoryFraction=`grep "memoryFraction" ${baseDirForScriptSelf}/config.json | awk -F":" '{print $2}' |sed -e 's/"//g' -e 's/,/ /g' `
mainClass=`grep "mainClass" ${baseDirForScriptSelf}/config.json | awk -F":" '{print $2}' |sed -e 's/"//g' -e 's/,/ /g' `

spark-submit \
  --class ${mainClass}\
  --master  yarn \
  --driver-memory ${driverMemory} \
  --deploy-mode client \
  --num-executors ${numExecutors} \
  --executor-memory ${executorMemory} \
  --executor-cores ${executorCores} \
  --queue $queueName \
  --conf spark.memory.fraction=${memoryFraction} \
  --conf spark.memory.storageFraction=${storageFraction} \
  --jars $jarpath \
  ${baseDirForScriptSelf}/${app} $config $url_config 2>&1 | grep -v "scheduler" |grep -v "storage"
