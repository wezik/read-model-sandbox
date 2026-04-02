#!/bin/bash
set -e

echo "Building application..."
./gradlew bootJar -q

echo "Starting postgres and localstack..."
docker compose up -d postgres localstack

echo "Waiting for postgres..."
until docker compose exec -T postgres pg_isready -U sandbox > /dev/null 2>&1; do
  sleep 1
done

echo "Starting instance 1 on port 8081..."
java -jar build/libs/assortment-app.jar --server.port=8081 > /tmp/app1.log 2>&1 &
PID1=$!

echo "Starting instance 2 on port 8082..."
java -jar build/libs/assortment-app.jar --server.port=8082 > /tmp/app2.log 2>&1 &
PID2=$!

echo "Starting instance 3 on port 8083..."
java -jar build/libs/assortment-app.jar --server.port=8083 > /tmp/app3.log 2>&1 &
PID3=$!

echo "Starting instance 4 on port 8084..."
java -jar build/libs/assortment-app.jar --server.port=8084 > /tmp/app4.log 2>&1 &
PID4=$!

echo "Waiting for apps to start..."
sleep 5

echo "Starting nginx load balancer..."
docker compose up -d nginx

echo ""
echo "=========================================="
echo "Cluster running!"
echo "=========================================="
echo "Load balancer: http://localhost:8080"
echo "Instance 1:    http://localhost:8081 (PID: $PID1)"
echo "Instance 2:    http://localhost:8082 (PID: $PID2)"
echo "Instance 3:    http://localhost:8083 (PID: $PID3)"
echo "Instance 4:    http://localhost:8084 (PID: $PID4)"
echo ""
echo "Logs: tail -f /tmp/app1.log /tmp/app2.log /tmp/app3.log /tmp/app4.log"
echo "Stop: ./stop-cluster.sh"
echo "=========================================="

# Wait for both processes
wait
