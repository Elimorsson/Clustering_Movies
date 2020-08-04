#!/bin/bash
if [ $# -ne 3 ]
  then
    echo "Not enough arguments supplied"
fi
if [ $2 -eq 1 ]
  then
    java -jar ./pivotJar.jar $1 $3
  else
     java -jar ./improveAlgo.jar $1 $3
fi
