#!/bin/bash
cd `dirname $0`
export LD_LIBRARY_PATH=.
java -Djava.library.path=. -jar 12executable.jar > logFile.txt
