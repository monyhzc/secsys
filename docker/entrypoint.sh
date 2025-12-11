#!/bin/bash
set -euo pipefail

SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-test}"
JAVA_OPTS="${JAVA_OPTS:-}"

term_handler() {
  local exit_code=${1:-0}
  if [[ -n "${JAVA_PID:-}" ]] && kill -0 "$JAVA_PID" >/dev/null 2>&1; then
    kill -TERM "$JAVA_PID" >/dev/null 2>&1 || true
  fi
  if [[ -n "${NGINX_PID:-}" ]] && kill -0 "$NGINX_PID" >/dev/null 2>&1; then
    kill -TERM "$NGINX_PID" >/dev/null 2>&1 || true
  fi
  wait || true
  exit "$exit_code"
}

trap 'term_handler' SIGINT SIGTERM

java $JAVA_OPTS -jar /app/app.jar --spring.profiles.active="$SPRING_PROFILES_ACTIVE" "$@" &
JAVA_PID=$!

/usr/sbin/nginx -g 'daemon off;' &
NGINX_PID=$!

set +e
wait -n "$JAVA_PID" "$NGINX_PID"
status=$?
term_handler "$status"
