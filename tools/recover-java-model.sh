#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "[recover] Refreshing Maven metadata and plugins"
mvn -f parse/pom.xml -U -DskipTests dependency:resolve dependency:resolve-plugins

echo "[recover] Cleaning and regenerating ANTLR and Java sources"
mvn -f parse/pom.xml -DskipTests clean generate-sources

echo "[recover] Compiling main and test sources"
mvn -f parse/pom.xml -DskipTests test-compile

echo "[recover] Java model recovery complete"