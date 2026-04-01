#!/bin/bash
set -Eeuo pipefail
export AWS_ENDPOINT_URL=http://localhost:4566

echo "Creating SNS topics..."
awslocal sns create-topic \
  --name product-events.fifo \
  --attributes FifoTopic=true,ContentBasedDeduplication=false
