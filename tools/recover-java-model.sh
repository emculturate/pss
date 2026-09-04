#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "[recover] Running clean package (tests skipped) to regenerate ANTLR and rebuild project artifacts"
mvn -f parse/pom.xml -DskipTests clean package

echo "[recover] Java model recovery complete"