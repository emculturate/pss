#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

resolve_jdk21_home() {
	local candidate
	for candidate in \
		"/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" \
		"/usr/local/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"; do
		if [[ -x "$candidate/bin/java" ]] && "$candidate/bin/java" -version 2>&1 | grep -q 'version "21'; then
			echo "$candidate"
			return 0
		fi
	done
	if command -v brew >/dev/null 2>&1; then
		local prefix
		prefix="$(brew --prefix openjdk@21 2>/dev/null)" || true
		candidate="$prefix/libexec/openjdk.jdk/Contents/Home"
		if [[ -n "$prefix" && -x "$candidate/bin/java" ]] \
			&& "$candidate/bin/java" -version 2>&1 | grep -q 'version "21'; then
			echo "$candidate"
			return 0
		fi
	fi
	return 1
}

if ! JAVA_HOME="$(resolve_jdk21_home)"; then
	echo "[recover] ERROR: JDK 21 is required (pss-parse targets release 21)." >&2
	echo "[recover] Install: brew install openjdk@21" >&2
	exit 1
fi
export JAVA_HOME
export PATH="$JAVA_HOME/bin:$PATH"

echo "[recover] Using JAVA_HOME=$JAVA_HOME"
java -version

echo "[recover] Running clean package (tests skipped) to regenerate ANTLR and rebuild project artifacts"
mvn -f parse/pom.xml -DskipTests clean package

echo "[recover] Java model recovery complete"
