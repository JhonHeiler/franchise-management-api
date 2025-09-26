#!/usr/bin/env bash
set -euo pipefail
PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_ROOT"
PORT="${SERVER_PORT:-8080}"
if lsof -iTCP:"$PORT" -sTCP:LISTEN -Pn >/dev/null 2>&1; then
  echo "[run-inmem] Puerto $PORT en uso. Exporta SERVER_PORT a otro valor o libera el puerto." >&2
  exit 1
fi
echo "[run-inmem] Construyendo jar (sin verificación de cobertura)..."
./gradlew -x jacocoTestCoverageVerification -x jacocoTestReport bootJar >/dev/null
JAR="$(ls build/libs/*SNAPSHOT.jar | head -n1)"
echo "[run-inmem] Iniciando en http://localhost:$PORT perfil=inmem"
exec java ${JAVA_OPTS:-} -jar "$JAR" --spring.profiles.active=inmem --server.port="$PORT" 
