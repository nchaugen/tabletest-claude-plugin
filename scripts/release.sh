#!/usr/bin/env bash
set -euo pipefail

VERSION=${1:-}
if [[ -z "$VERSION" ]]; then
    echo "Usage: $0 <version>  (e.g. $0 1.3.0)" >&2
    exit 1
fi

TAG="v$VERSION"

if ! grep -q "\"version\": \"$VERSION\"" .claude-plugin/plugin.json; then
    echo "Error: version $VERSION not found in .claude-plugin/plugin.json" >&2
    exit 1
fi

if ! grep -q "^## \[$VERSION\]" CHANGELOG.md; then
    echo "Error: no entry for [$VERSION] in CHANGELOG.md" >&2
    exit 1
fi

git tag -a "$TAG" -m "$TAG"
git push origin "$TAG"
echo "Pushed $TAG — GitHub Actions will create the release."
