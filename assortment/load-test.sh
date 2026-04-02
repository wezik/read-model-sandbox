#!/bin/bash

REQUESTS=${1:-1000}
CONCURRENCY=${2:-50}
URL=${3:-http://localhost:8080/products}

PAYLOAD='{"category":"T_SHIRT","styles":[{"color":"black","articles":[{"size":"small"},{"size":"medium"},{"size":"large"}]},{"color":"white","articles":[{"size":"small"},{"size":"medium"},{"size":"large"}]}]}'

echo "Sending $REQUESTS requests with concurrency $CONCURRENCY to $URL"
echo "Started at $(date)"

seq $REQUESTS | xargs -P$CONCURRENCY -I{} \
  curl -s -X POST "$URL" \
  -H "Content-Type: application/json" \
  -d "$PAYLOAD" \
  -o /dev/null -w ""

echo ""
echo "Finished at $(date)"
