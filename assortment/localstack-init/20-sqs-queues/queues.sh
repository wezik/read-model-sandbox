#!/bin/bash
set -Eeuo pipefail
export AWS_ENDPOINT_URL=http://localhost:4566

echo "Creating SQS queues..."
awslocal sqs create-queue \
  --queue-name marketplace-product-events.fifo \
  --attributes FifoQueue=true,ContentBasedDeduplication=false
