#!/bin/sh
cd /var/lib/tomcat7/webapps/eventshop/proc/Debug/
g++ -I/usr/include/opencv -I/usr/include/opencv2 -O0 -g3 -Wall -c -fmessage-length=0 -o src/$1.o ../src/$1.cc -lopencv_core -lopencv_highgui -lopencv_imgproc
g++ -L/usr/lib -L/usr/lib -o $2 src/temporalPatternTemplate.o src/temporalPattern.o src/spatialchar.o src/spatialPattern.o src/op.o src/message.pb.o src/$1.o src/grouping.o src/filter.o src/emage.o src/aggregation.o -lopencv_core -lopencv_highgui -lprotobuf -lprotoc -lopencv_imgproc
