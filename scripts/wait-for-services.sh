#!/usr/bin/env bash
# Espera a que postgres, backend y frontend estén listos antes de lanzar tests.
set -euo pipefail

COMPOSE_FILE="${COMPOSE_FILE:-docker-compose.test.yml}"
MAX_ATTEMPTS="${MAX_ATTEMPTS:-60}"
SLEEP_SECONDS="${SLEEP_SECONDS:-5}"

echo "Esperando servicios (compose: ${COMPOSE_FILE})..."

wait_for_backend() {
  local attempt=1
  while [ "$attempt" -le "$MAX_ATTEMPTS" ]; do
    if curl -sf "http://localhost:8080/actuator/health" >/dev/null 2>&1; then
      echo "Backend listo en :8080 (intento ${attempt})"
      return 0
    fi
    echo "  backend no listo (${attempt}/${MAX_ATTEMPTS})..."
    sleep "$SLEEP_SECONDS"
    attempt=$((attempt + 1))
  done
  echo "ERROR: backend no respondió a tiempo"
  docker compose -f "$COMPOSE_FILE" logs backend
  return 1
}

wait_for_frontend() {
  local attempt=1
  while [ "$attempt" -le "$MAX_ATTEMPTS" ]; do
    if curl -sf "http://localhost/" >/dev/null 2>&1; then
      echo "Frontend listo en :80 (intento ${attempt})"
      return 0
    fi
    echo "  frontend no listo (${attempt}/${MAX_ATTEMPTS})..."
    sleep "$SLEEP_SECONDS"
    attempt=$((attempt + 1))
  done
  echo "ERROR: frontend no respondió a tiempo"
  docker compose -f "$COMPOSE_FILE" logs frontend
  return 1
}

docker compose -f "$COMPOSE_FILE" ps
wait_for_backend
wait_for_frontend
echo "Todos los servicios están listos para ejecutar tests."
