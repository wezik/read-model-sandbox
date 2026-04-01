#!/bin/bash
set -Eeuo pipefail
export AWS_ENDPOINT_URL=http://localhost:4566

echo "Creating SNS subscriptions..."
awslocal sns subscribe \
  --topic-arn "arn:aws:sns:us-east-1:000000000000:product-events.fifo" \
  --protocol sqs \
  --notification-endpoint "arn:aws:sqs:us-east-1:000000000000:marketplace-product-events.fifo" \
  --attributes RawMessageDelivery=true
