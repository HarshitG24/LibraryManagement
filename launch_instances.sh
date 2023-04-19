#!/bin/bash

for i in {1..5}
do
  PORT=$((8080+$i))
  DBNAME="database-instance-$i"
  echo "Starting instance $i on port $PORT with database $DBNAME"
  npx kill-port $PORT
  PORT=$PORT DBNAME=$DBNAME java -jar target/distributed-systems-0.0.1-SNAPSHOT.jar --spring.config.location=classpath:/application-template.properties &
  sleep 1
done
