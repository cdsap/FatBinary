#!/usr/bin/env bash
# Publish FatBinary to mavenLocal, build the e2e CLI, and smoke-test it.
# Optional: pass one or more JAVA_HOME paths to exercise a local Java matrix.
#
#   ./scripts/test-cli-matrix.sh
#   ./scripts/test-cli-matrix.sh "$JAVA_HOME" /path/to/jdk-21 /path/to/jdk-25

set -euo pipefail

root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root"

echo "==> Publishing plugin to mavenLocal"
./gradlew publishToMavenLocal

echo "==> Building e2e fat binary"
(
  cd e2e/consumer
  ../../gradlew fatBinary
)

run_binary() {
  local label="$1"
  echo "==> Running ./fatbinary-e2e ($label)"
  (
    cd e2e/consumer
    java -version
    output="$(./fatbinary-e2e)"
    echo "$output"
    echo "$output" | grep -q "FatBinary e2e OK"
    echo "$output" | grep -q "java.version="
  )
}

if [[ "$#" -eq 0 ]]; then
  run_binary "current JAVA_HOME/PATH"
  exit 0
fi

for java_home in "$@"; do
  if [[ ! -x "$java_home/bin/java" ]]; then
    echo "error: no executable java at $java_home/bin/java" >&2
    exit 1
  fi
  export JAVA_HOME="$java_home"
  export PATH="$JAVA_HOME/bin:$PATH"
  run_binary "$JAVA_HOME"
done

echo "OK — CLI matrix passed for $# Java home(s)"
