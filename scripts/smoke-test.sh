#!/usr/bin/env bash
set -euo pipefail
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
mkdir -p "$TMP/Home" "$TMP/Dev" "$TMP/Test"
printf '<root/>\n' > "$TMP/Home/data.xml"
printf 'jar content\n' > "$TMP/Home/test.jar"
touch -d '2024-04-10 12:15:00' "$TMP/Home/test.jar"
(cd "$TMP" && java -cp "$PROJECT_ROOT/target/classes" Main --once)
test -f "$TMP/Dev/data.xml"
test -f "$TMP/Dev/test.jar"
test ! -e "$TMP/Home/data.xml"
test ! -e "$TMP/Home/test.jar"
printf 'original' > "$TMP/Dev/conflict.xml"
printf 'keep me' > "$TMP/Home/conflict.xml"
(cd "$TMP" && java -cp "$PROJECT_ROOT/target/classes" Main --once)
test "$(cat "$TMP/Dev/conflict.xml")" = original
test "$(cat "$TMP/Home/conflict.xml")" = 'keep me'
printf 'untouched' > "$TMP/Home/other.txt"
(cd "$TMP" && java -cp "$PROJECT_ROOT/target/classes" Main --once)
test -f "$TMP/Home/other.txt"
echo 'smoke test: PASS'
