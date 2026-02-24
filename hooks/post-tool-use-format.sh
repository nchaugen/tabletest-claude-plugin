#!/usr/bin/env bash
#
# PostToolUse hook: auto-format TableTest tables after Write/Edit.
#
# Reads hook input from stdin, checks if the modified file contains @TableTest,
# and runs the formatter if so.  Exits silently if the formatter is not
# installed — formatting is a convenience, not a gate.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PLUGIN_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
FORMAT_SCRIPT="$PLUGIN_ROOT/skills/tabletest/scripts/format-table.sh"

input=$(cat)

file_path=$(echo "$input" | jq -r '.tool_input.file_path // empty')

if [[ -z "$file_path" ]] || [[ ! -f "$file_path" ]]; then
    exit 0
fi

# Only process Java/Kotlin files
case "$file_path" in
    *.java|*.kt) ;;
    *) exit 0 ;;
esac

# Only format if file contains @TableTest
if ! grep -q '@TableTest' "$file_path" 2>/dev/null; then
    exit 0
fi

# Run the formatter — suppress errors to avoid blocking the workflow
"$FORMAT_SCRIPT" "$file_path" 2>/dev/null || true

exit 0
