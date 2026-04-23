#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
MODULE_DIR="$ROOT_DIR/parse"
SITE_DIR="$MODULE_DIR/target/site"
CURRENT_JACOCO_DIR="$SITE_DIR/jacoco"
CURRENT_JACOCO_EXEC="$MODULE_DIR/target/jacoco.exec"
GIT_HISTORY_DIR="$MODULE_DIR/documents/coverage/history"
MANIFEST_FILE="$GIT_HISTORY_DIR/manifest.log"

mkdir -p "$SITE_DIR"
mkdir -p "$GIT_HISTORY_DIR"

timestamp="$(date +"%Y%m%d_%H%M%S")"

snapshot_dir=""
if [[ -d "$CURRENT_JACOCO_DIR" || -f "$CURRENT_JACOCO_EXEC" ]]; then
  snapshot_dir="$SITE_DIR/previous_jacoco_${timestamp}"
  mkdir -p "$snapshot_dir"

  if [[ -d "$CURRENT_JACOCO_DIR" ]]; then
    mv "$CURRENT_JACOCO_DIR" "$snapshot_dir/jacoco_site"
  fi

  if [[ -f "$CURRENT_JACOCO_EXEC" ]]; then
    mv "$CURRENT_JACOCO_EXEC" "$snapshot_dir/jacoco.exec"
  fi

  echo "[jacoco-helper] Previous coverage moved to: $snapshot_dir"
else
  echo "[jacoco-helper] No existing JaCoCo artifacts found to archive."
fi

if [[ $# -gt 0 ]]; then
  echo "[jacoco-helper] Running: mvn -f parse/pom.xml $*"
  mvn -f "$MODULE_DIR/pom.xml" "$@"
else
  echo "[jacoco-helper] Running: mvn -f parse/pom.xml verify"
  mvn -f "$MODULE_DIR/pom.xml" verify
fi

new_xml="$CURRENT_JACOCO_DIR/jacoco.xml"
if [[ -f "$new_xml" ]]; then
  echo "[jacoco-helper] New report: $new_xml"
else
  echo "[jacoco-helper] WARNING: Expected report not found at $new_xml"
fi

if [[ -n "$snapshot_dir" && -f "$snapshot_dir/jacoco_site/jacoco.xml" ]]; then
  echo "[jacoco-helper] Previous report: $snapshot_dir/jacoco_site/jacoco.xml"
fi

# Persist previous/new jacoco.xml in a git-visible folder so snapshots survive across sessions.
previous_xml=""
if [[ -n "$snapshot_dir" && -f "$snapshot_dir/jacoco_site/jacoco.xml" ]]; then
  previous_xml="$snapshot_dir/jacoco_site/jacoco.xml"
  cp "$previous_xml" "$GIT_HISTORY_DIR/jacoco_previous_${timestamp}.xml"
  cp "$previous_xml" "$GIT_HISTORY_DIR/jacoco_previous_latest.xml"
  echo "[jacoco-helper] Git-visible previous snapshot: $GIT_HISTORY_DIR/jacoco_previous_${timestamp}.xml"
fi

if [[ -f "$new_xml" ]]; then
  cp "$new_xml" "$GIT_HISTORY_DIR/jacoco_new_${timestamp}.xml"
  cp "$new_xml" "$GIT_HISTORY_DIR/jacoco_new_latest.xml"
  echo "[jacoco-helper] Git-visible new snapshot: $GIT_HISTORY_DIR/jacoco_new_${timestamp}.xml"
fi

{
  echo "timestamp=$timestamp"
  echo "maven_args=${*:-verify}"
  if [[ -n "$previous_xml" ]]; then
    echo "previous_target_xml=$previous_xml"
    echo "previous_git_xml=$GIT_HISTORY_DIR/jacoco_previous_${timestamp}.xml"
  else
    echo "previous_target_xml=NONE"
    echo "previous_git_xml=NONE"
  fi
  if [[ -f "$new_xml" ]]; then
    echo "new_target_xml=$new_xml"
    echo "new_git_xml=$GIT_HISTORY_DIR/jacoco_new_${timestamp}.xml"
  else
    echo "new_target_xml=MISSING"
    echo "new_git_xml=MISSING"
  fi
  echo "---"
} >> "$MANIFEST_FILE"

echo "[jacoco-helper] Manifest appended: $MANIFEST_FILE"
