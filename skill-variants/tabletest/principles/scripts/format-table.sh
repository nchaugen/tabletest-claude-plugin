#!/usr/bin/env bash
#
# Formats TableTest tables in Java/Kotlin files.
# Wraps the tabletest-formatter CLI (https://github.com/nchaugen/tabletest-formatter).
#
# Usage: format-table.sh [--check] <file-or-directory>...
#
# The formatter aligns pipes, pads values, and normalises spacing in @TableTest
# text blocks.  It reads .editorconfig for indent settings.
#
# Exit codes:
#   0 - Success (or no formatter found — prints warning)
#   1 - Formatting changes needed (--check mode) or formatter error

set -euo pipefail

ARTIFACT="org.tabletest:tabletest-formatter-cli:1.0.1:jar:shaded"
GROUP_PATH="org/tabletest"
ARTIFACT_ID="tabletest-formatter-cli"
VERSION="1.0.1"

find_formatter_jar() {
    # 1. Check Maven local repository (most common)
    local m2_jar="$HOME/.m2/repository/$GROUP_PATH/$ARTIFACT_ID/$VERSION/$ARTIFACT_ID-$VERSION-shaded.jar"
    if [[ -f "$m2_jar" ]]; then
        echo "$m2_jar"
        return 0
    fi

    # 2. Check non-shaded JAR in Maven local repository
    local m2_plain="$HOME/.m2/repository/$GROUP_PATH/$ARTIFACT_ID/$VERSION/$ARTIFACT_ID-$VERSION.jar"
    if [[ -f "$m2_plain" ]]; then
        echo "$m2_plain"
        return 0
    fi

    return 1
}

download_formatter() {
    if command -v mvn &>/dev/null; then
        mvn -q dependency:get \
            -Dartifact="$ARTIFACT" \
            -Dtransitive=false 2>/dev/null && return 0
    fi

    if command -v curl &>/dev/null; then
        local target_dir="$HOME/.m2/repository/$GROUP_PATH/$ARTIFACT_ID/$VERSION"
        local target_jar="$target_dir/$ARTIFACT_ID-$VERSION-shaded.jar"
        local url="https://repo1.maven.org/maven2/$GROUP_PATH/$ARTIFACT_ID/$VERSION/$ARTIFACT_ID-$VERSION-shaded.jar"
        mkdir -p "$target_dir"
        curl -fsSL -o "$target_jar" "$url" 2>/dev/null && return 0
    fi

    return 1
}

main() {
    if [[ $# -eq 0 ]]; then
        echo "Usage: format-table.sh [--check] <file-or-directory>..." >&2
        exit 1
    fi

    local jar
    jar=$(find_formatter_jar) || true

    if [[ -z "$jar" ]]; then
        if download_formatter; then
            jar=$(find_formatter_jar) || true
        fi
    fi

    if [[ -z "$jar" ]]; then
        echo "WARNING: tabletest-formatter not found. Install with:" >&2
        echo "  mvn dependency:get -Dartifact=$ARTIFACT -Dtransitive=false" >&2
        exit 0
    fi

    java -jar "$jar" "$@"
}

main "$@"
