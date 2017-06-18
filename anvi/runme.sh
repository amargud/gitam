#!/bin/bash
source ~/sourceme

#hadoop dfs -rmr /user/frames/

rm /home/amar/test/*.class

FCP="$HADOOP_HOME/share/hadoop/common/hadoop-common-2.6.0.jar:$HADOOP_HOME/share/hadoop/common/lib/hadoop-annotations-2.6.0.jar:$HADOOP_HOME/share/hadoop/common/hadoop-common-2.6.0.jar:$HADOOP_HOME/share/hadoop/common/lib/hadoop-annotations-2.6.0.jar:$HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.6.0.jar:/home/amar/frames_jars/xmlpull-1.1.3.1.jar:/home/amar/frames_jars/xstream-1.4.3.jar:/home/amar/frames_jars/xstream-1.4.7.jar:/home/amar/frames_jars/xuggler-0.16.jar:/home/amar/frames_jars/xuggle-video-1.3.1.jar:/home/amar/frames_jars/xuggle-video-1.4-SNAPSHOT.jar:/home/amar/frames_jars/xuggle-xuggler-5.4.jar:/home/amar/frames_jars/xz-1.4.jar:/home/amar/frames_jars/core-1.4-SNAPSHOT.jar:/home/amar/frames_jars/core-audio-1.4-SNAPSHOT.jar:/home/amar/frames_jars/core-image-1.4-SNAPSHOT.jar:/home/amar/frames_jars/core-math-1.4-SNAPSHOT.jar:/home/amar/frames_jars/core-video-1.4-SNAPSHOT.jar:/home/amar/frames_jars/core-video-capture-1.4-SNAPSHOT.jar:/home/amar/frames_jars/processing-core-1.4-SNAPSHOT.jar:/home/amar/frames_jars/gstreamer-video-1.4-SNAPSHOT.jar:/home/amar/frames_jars/core-video-1.3.1.jar:/home/amar/frames_jars/core-video-1.4-SNAPSHOT.jar:/home/amar/frames_jars/core-video-capture-1.3.1.jar:/home/amar/frames_jars/core-video-capture-1.4-SNAPSHOT.jar:/home/amar/frames_jars/gstreamer-video-1.4-SNAPSHOT.jar:/home/amar/frames_jars/video.jar:/home/amar/frames_jars/video-processing-1.4-SNAPSHOT.jar:/home/amar/frames_jars/xuggle-video-1.3.1.jar:/home/amar/frames_jars/xuggle-video-1.4-SNAPSHOT.jar:/home/amar/frames_jars/apache-logging-log4j.jar:/home/amar/frames_jars/log4j-1.2.12.jar:/home/amar/frames_jars/log4j-1.2.14.jar:/home/amar/frames_jars/log4j-over-slf4j-1.8.0-alpha2.jar:/home/amar/frames_jars/log4j-over-slf4j-1.8.0-alpha2.jar:/home/amar/frames_jars/slf4j-api-1.7.22.jar:/home/amar/frames_jars/slf4j-api-1.8.0-alpha2.jar:/home/amar/frames_jars/apache-logging-log4j.jar:/home/amar/frames_jars/demos-1.4-SNAPSHOT.jar:."

HCP="$HADOOP_HOME/share/hadoop/common/hadoop-common-2.6.0.jar:$HADOOP_HOME/share/hadoop/common/lib/hadoop-annotations-2.6.0.jar:$HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.6.0.jar:/home/amar/hadoop-2.6.0/etc/hadoop:/home/amar/hadoop-2.6.0/share/hadoop/common/lib/*:/home/amar/hadoop-2.6.0/share/hadoop/common/*:/home/amar/hadoop-2.6.0/share/hadoop/hdfs:/home/amar/hadoop-2.6.0/share/hadoop/hdfs/lib/*:/home/amar/hadoop-2.6.0/share/hadoop/hdfs/*:/home/amar/hadoop-2.6.0/share/hadoop/yarn/lib/*:/home/amar/hadoop-2.6.0/share/hadoop/yarn/*:/home/amar/hadoop-2.6.0/share/hadoop/mapreduce/lib/*:/home/amar/hadoop-2.6.0/share/hadoop/mapreduce/*:/home/amar/hadoop-2.6.0/contrib/capacity-scheduler/*.jar"

FCP=$FCP:$HCP


## COMPILATION 
$JAVA_HOME/bin/javac  -classpath $FCP  VideoThumbnailsExample.java HdfsWriter.java

#EXECUTION
$JAVA_HOME/bin/java -classpath $FCP  VideoThumbnailsExample
