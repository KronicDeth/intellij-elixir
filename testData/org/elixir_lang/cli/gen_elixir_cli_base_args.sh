#!/bin/bash

# This generates new test data for tests/org/elixir_lang/cli/CliArgumentsDataTest.kt
# It should be run on a Posix system with mise installed. It works on Ubuntu, on other OSes YMMV.
#
# It will iterate over all of the available versions of elixir since 1.13.0, skipping anything that looks like a beta, and
# capturing the dry run output for the rest. The output then overwrites the testData/org/elixir_lang/cli/elixir_cli_base_args.txt
# file which is read by the test. After regenerating, don't forget to commit the change.
set -euo pipefail

if [[ -n "${DEBUG:-}" ]]; then
  set -x
fi

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd -P)"
OUTPUT_FILE="${SCRIPT_DIR}/elixir_cli_base_args.txt"
HOME_PATH_MARKER='{elixirHomePath}'

VERBOSE=0
if [[ ${1-} = --verbose ]]; then
  VERBOSE=1
fi

verbose_echo() {
  if [[ "$VERBOSE" -eq 1 ]]; then
    echo "$@" >&2
  fi
}

# Credit to https://stackoverflow.com/a/60279429
faketty() {
  # Create a temporary file for storing the status code
  tmp=$(mktemp)

  # Ensure it worked or fail with status 99
  [ "$tmp" ] || return 99

  # Produce a script that runs the command provided to faketty as
  # arguments and stores the status code in the temporary file
  cmd="$(printf '%q ' "$@")"'; echo $? > '$tmp

  # Run the script through /bin/sh with fake tty
  if [ "$(uname)" = "Darwin" ]; then
    # MacOS
    script -Fq /dev/null /bin/sh -c "$cmd"
  else
    script -qfc "/bin/sh -c $(printf "%q " "$cmd")" /dev/null
  fi

  # Ensure that the status code was written to the temporary file or
  # fail with status 99
  [ -s "$tmp" ] || return 99

  # Collect the status code from the temporary file
  err=$(<"$tmp")

  # Remove the temporary file
  rm "$tmp"

  # Return the status code
  return "$err"
}

verbose_echo "Starting Elixir dry-run checks..."

readarray -t ELIXIR_VERSIONS_ARRAY < <(mise ls-remote elixir | grep -vE 'master|main|^0|^1\.[0-9]\.|^1\.1[0-2]|^$|rc' | sort -V)

verbose_echo "--- Versions to process ---"
for version_to_process in "${ELIXIR_VERSIONS_ARRAY[@]}"; do
  verbose_echo "$version_to_process"
done
verbose_echo "---------------------------"
verbose_echo ""

TEMP_DIR="$(mktemp -d)"
# Truncate the output file.
: >"${OUTPUT_FILE}"
EXECS_TO_TEST=(mix elixir "elixir -r required_path" iex elixirc)
for EXEC_TO_TEST in "${EXECS_TO_TEST[@]}"; do
  PREVIOUS_VERSION=""
  PREVIOUS_VERSIONS_NORMALIZED_OUTPUT=""
  MAX_INDEX=$((${#ELIXIR_VERSIONS_ARRAY[@]} - 1))
  LATEST_VERSION="${ELIXIR_VERSIONS_ARRAY[$MAX_INDEX]}"
  for VERSION in "${ELIXIR_VERSIONS_ARRAY[@]}"; do
    verbose_echo "Processing Elixir version: $VERSION"
    echo -n "." 1>&2

    # Check if the version is installed using exact match,
    if ! mise ls-remote elixir | grep -qE "^\s*$VERSION$"; then
      verbose_echo "  Version $VERSION is not installed. Installing..."
      # Attempt installation. If it fails, `set -e` will cause the script to exit.
      mise install elixir@"$VERSION"
      verbose_echo "  Installation of $VERSION complete."
    else
      verbose_echo "  Version $VERSION is already installed."
    fi

    verbose_echo "  Running command for $VERSION: ELIXIR_CLI_DRY_RUN=1 $EXEC_TO_TEST"

    {
      # Run the command, only take the first line and smash it through realpath to get rid of the relative paths.
      if [[ "$VERBOSE" -eq 1 ]]; then
        set -x
      fi
      # shellcheck disable=SC2086
      OUTPUT=$(ELIXIR_CLI_DRY_RUN=1 faketty mise exec "elixir@$VERSION" -- $EXEC_TO_TEST -- 2>/dev/null | sed -e 's/[\r\n]//g')
      if [[ "$VERBOSE" -eq 1 ]]; then
        set +x
      fi
      if [[ -z $OUTPUT ]]; then
        echo "ERROR: No output from mise exec \"elixir@$VERSION\" -- \"$EXEC_TO_TEST\""
        exit 1
      fi

      MISE_INSTALL_PATH="$(mise where "elixir@$VERSION")"
      NORMALIZED_OUTPUT="${OUTPUT//${MISE_INSTALL_PATH}/${HOME_PATH_MARKER}}"
      NORMALIZED_OUTPUT="${NORMALIZED_OUTPUT% --}"
      echo "${NORMALIZED_OUTPUT}"
    } >"${TEMP_DIR}/$EXEC_TO_TEST@$VERSION.txt" &
  done
done
wait

MAX_INDEX=$((${#ELIXIR_VERSIONS_ARRAY[@]} - 1))
LATEST_VERSION="${ELIXIR_VERSIONS_ARRAY[$MAX_INDEX]}"
for EXEC_TO_TEST in "${EXECS_TO_TEST[@]}"; do
  PREVIOUS_VERSION=""
  PREVIOUS_VERSIONS_NORMALIZED_OUTPUT=""
  find "${TEMP_DIR}" -name "${EXEC_TO_TEST}@*.txt" | sort -V | while read -r FILE; do
    verbose_echo "Processing ${FILE}"
    NORMALIZED_OUTPUT=$(<"$FILE")
    VERSION=$(basename -s .txt "$FILE")
    VERSION=${VERSION#"$EXEC_TO_TEST"@}
    # This will produce over 600 lines of test data without some filtering, so we try to reduce duplicates
    # while keeping enough variety to catch issues if they arise.
    # If the command line is different from the previous version's command line, add a test case
    # OR if the current version is the same as the previous version with an extension (i.e. 1.13.1 was previous, current is 1.13.1-otp-24), add a test case
    # OR if this is the latest version mise has available.
    if [[ $NORMALIZED_OUTPUT != "$PREVIOUS_VERSIONS_NORMALIZED_OUTPUT" || $VERSION = "$PREVIOUS_VERSION"-* || $VERSION = "$LATEST_VERSION" ]]; then
      echo "$EXEC_TO_TEST|$VERSION|$NORMALIZED_OUTPUT" >>"${OUTPUT_FILE}"
      PREVIOUS_VERSIONS_NORMALIZED_OUTPUT="$NORMALIZED_OUTPUT"
      PREVIOUS_VERSION="$VERSION"

      verbose_echo "  --- Captured (and Normalized) output for $VERSION ---"
      verbose_echo "  $NORMALIZED_OUTPUT"
      verbose_echo "  ------------------------------------"
      verbose_echo ""
    else
      verbose_echo "  --- Discarded output for $VERSION as it was a duplicate of the previous version ---"
      verbose_echo "  $NORMALIZED_OUTPUT"
      verbose_echo "  ------------------------------------"
      verbose_echo ""
    fi
  done
done
verbose_echo "Script finished."
