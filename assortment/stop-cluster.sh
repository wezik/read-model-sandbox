#!/bin/bash

echo "Stopping app instances..."
pkill -f "assortment-app.jar" 2>/dev/null || true

echo "Stopping infrastructure..."
docker compose down

# To also delete data: docker compose down -v

echo "Done."
