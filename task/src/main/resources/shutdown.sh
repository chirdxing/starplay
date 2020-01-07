#!/bin/sh
if [ -s pid ] ; then
    kill `cat pid`
    rm -f pid
fi
