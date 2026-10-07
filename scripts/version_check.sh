#!/bin/bash
set -euo pipefail

cd "$(dirname "$0")/.."

echo "========================="
echo "LOADED ENV VARIABLES:"
echo "PR_VERSION: $PR_VERSION"
echo "MAIN_VERSION: $MAIN_VERSION"
echo "BASE_BRANCH: $BASE_BRANCH"
echo "========================="

semver_gt() {
  IFS='.' read -r major1 minor1 patch1 <<< "$1"
  IFS='.' read -r major2 minor2 patch2 <<< "$2"

  # Compare major version
  if [ "$major1" -gt "$major2" ]; then
    return 0
  elif [ "$major1" -lt "$major2" ]; then
    return 1
  fi

  # Compare minor version
  if [ "$minor1" -gt "$minor2" ]; then
    return 0
  elif [ "$minor1" -lt "$minor2" ]; then
    return 1
  fi

  # Compare patch version
  if [ "$patch1" -gt "$patch2" ]; then
    return 0
  else
    return 1
  fi
}

# Version Checking Logic
if [ "$BASE_BRANCH" = "main" ]; then
  echo "'$BRANCH_TYPE' branch into main => applying '$BRANCH_TYPE' rules"
  if [[ "$PR_VERSION" == "$MAIN_VERSION" && ( "$BRANCH_TYPE" == "feature" || "$BRANCH_TYPE" == "dependabot" ) ]]; then
    echo "OK: PR Version ($PR_VERSION) is identical with the current Main version ($MAIN_VERSION)."
    exit 0
  elif semver_gt "$PR_VERSION" "$MAIN_VERSION" && [ "$BRANCH_TYPE" = "release" ]; then
    echo "OK: PR Version ($PR_VERSION) is higher than Main version ($MAIN_VERSION)."
    exit 0
  else
    echo "FAIL: PR Version ($PR_VERSION) is not higher than the current Main version ($MAIN_VERSION)."
    exit 1
  fi

else
  echo "Skipping version check: Base branch is '$BASE_BRANCH'. No version enforcement required."
  exit 0
fi