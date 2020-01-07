#!/bin/sh
echo "start task on background"
nohup java -cp "lib/*:conf/" com.weijuju.base.job.main.TaskMain %* &
echo "success started"
echo $! > pid