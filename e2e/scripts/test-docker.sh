#!/usr/bin/env bash
# Ejecuta la suite E2E en Docker (útil en WSL/Ubuntu 26 donde Playwright no soporta el SO nativo).
# Requisito: stack de pruebas levantado en el host (docker compose -f docker-compose.test.yml up -d).
#
# Uso:
#   npm run test:docker
#
# Docker Desktop (sin --network host):
#   USE_HOST_NETWORK=false BASE_URL=http://host.docker.internal \
#     API_BASE_URL=http://host.docker.internal:8080 npm run test:docker
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
E2E_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Debe coincidir con la versión de @playwright/test en package-lock.json
PLAYWRIGHT_IMAGE="${PLAYWRIGHT_IMAGE:-mcr.microsoft.com/playwright:v1.60.0-jammy}"
USE_HOST_NETWORK="${USE_HOST_NETWORK:-true}"
BASE_URL="${BASE_URL:-http://localhost}"
API_BASE_URL="${API_BASE_URL:-http://localhost:8080}"

if ! command -v docker >/dev/null 2>&1; then
  echo "ERROR: docker no está instalado o no está en PATH" >&2
  exit 1
fi

DOCKER_ARGS=(--rm --init --ipc=host)
if [ "${USE_HOST_NETWORK}" = "true" ]; then
  DOCKER_ARGS+=(--network host)
fi

echo "Ejecutando E2E en Docker (${PLAYWRIGHT_IMAGE})"
echo "  USE_HOST_NETWORK=${USE_HOST_NETWORK}"
echo "  BASE_URL=${BASE_URL}"
echo "  API_BASE_URL=${API_BASE_URL}"
echo ""

docker run "${DOCKER_ARGS[@]}" \
  -v "${E2E_DIR}:/work/e2e" \
  -v apuntame-e2e-node_modules:/work/e2e/node_modules \
  -w /work/e2e \
  -e BASE_URL="${BASE_URL}" \
  -e API_BASE_URL="${API_BASE_URL}" \
  -e CI=true \
  "${PLAYWRIGHT_IMAGE}" \
  bash -lc "npm ci && npm run test:ci"

echo ""
echo "Reporte HTML: e2e/playwright-report/index.html"
echo "Abrir en local: cd e2e && npm run test:report"
