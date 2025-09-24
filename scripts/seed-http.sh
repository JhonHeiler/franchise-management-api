#!/usr/bin/env bash
set -euo pipefail

# Simple HTTP seeder for Franchise API
# Requires: curl, jq

API_BASE=${API_BASE:-http://localhost:8080}

need() { command -v "$1" >/dev/null 2>&1 || { echo "Error: '$1' is required"; exit 1; }; }
need curl
need jq

echo "Seeding data into ${API_BASE}"

echo "Creating franchise ACME..."
FRANCHISE_ID=$(curl -sS -X POST "${API_BASE}/api/v1/franchises" \
  -H 'Content-Type: application/json' \
  -d '{"name":"ACME"}' | jq -r '.id')
echo "FRANCHISE_ID=${FRANCHISE_ID}"

echo "Creating branches..."
BRANCH1_ID=$(curl -sS -X POST "${API_BASE}/api/v1/franchises/${FRANCHISE_ID}/branches" \
  -H 'Content-Type: application/json' \
  -d '{"name":"Sucursal Centro"}' | jq -r '.id')
BRANCH2_ID=$(curl -sS -X POST "${API_BASE}/api/v1/franchises/${FRANCHISE_ID}/branches" \
  -H 'Content-Type: application/json' \
  -d '{"name":"Sucursal Norte"}' | jq -r '.id')
echo "BRANCH1_ID=${BRANCH1_ID}  BRANCH2_ID=${BRANCH2_ID}"

echo "Adding products to branch1..."
PROD1_ID=$(curl -sS -X POST "${API_BASE}/api/v1/franchises/${FRANCHISE_ID}/branches/${BRANCH1_ID}/products" \
  -H 'Content-Type: application/json' \
  -d '{"name":"Café","stock":50}' | jq -r '.id')
PROD2_ID=$(curl -sS -X POST "${API_BASE}/api/v1/franchises/${FRANCHISE_ID}/branches/${BRANCH1_ID}/products" \
  -H 'Content-Type: application/json' \
  -d '{"name":"Té","stock":30}' | jq -r '.id')
echo "PROD1_ID=${PROD1_ID}  PROD2_ID=${PROD2_ID}"

echo "Updating stock of PROD2 to 75..."
curl -sS -X PATCH "${API_BASE}/api/v1/franchises/${FRANCHISE_ID}/branches/${BRANCH1_ID}/products/${PROD2_ID}/stock" \
  -H 'Content-Type: application/json' -d '{"stock":75}' >/dev/null

echo "Aggregation (max stock per branch):"
curl -sS "${API_BASE}/api/v1/franchises/${FRANCHISE_ID}/branches/max-stock-products" | jq

echo "Done. Franchise=${FRANCHISE_ID} Branch1=${BRANCH1_ID} Branch2=${BRANCH2_ID} Prod1=${PROD1_ID} Prod2=${PROD2_ID}"
