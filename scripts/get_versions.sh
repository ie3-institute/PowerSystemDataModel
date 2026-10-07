#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

REPO_URL="$(git config --get remote.origin.url)"
export REPO_URL

if [[ -n "${GITHUB_ENV:-}" ]]; then
  echo "REPO_URL=$REPO_URL" >> "$GITHUB_ENV"
fi

parse_version() {
  local source="$1"
  local props major minor patch

  props="$(tr -d '\r')"
  major="$(sed -n 's/^version\.major=//p' <<< "$props")"
  minor="$(sed -n 's/^version\.minor=//p' <<< "$props")"
  patch="$(sed -n 's/^version\.patch=//p' <<< "$props")"

  if [[ ! "$major.$minor.$patch" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    echo "ERROR: Invalid version in version.properties of $source: '$major.$minor.$patch'" >&2
    exit 1
  fi

  echo "$major.$minor.$patch"
}

write_env() {
  local key="$1"
  local value="$2"

  echo "$key=$value"
  echo "export $key=$value" >> versions.env

  if [[ -n "${GITHUB_ENV:-}" ]]; then
    echo "$key=$value" >> "$GITHUB_ENV"
  fi
}

echo "Fetching current version of PR..."
PR_VERSION="$(parse_version "PR" < version.properties)"
write_env "PR_VERSION" "$PR_VERSION"

get_branch_version() {
  local branch_name="$1"
  local branch_version

  git fetch --quiet origin "+refs/heads/$branch_name:refs/remotes/origin/$branch_name"

  echo "Fetching version from $branch_name branch..."
  branch_version="$(git show "origin/$branch_name:version.properties" | parse_version "$branch_name")"

  write_env "${branch_name^^}_VERSION" "$branch_version"
}

get_branch_version "dev"
get_branch_version "main"

echo "Get Versions: OK!"