#!/usr/bin/env bash
# Runs the suite with the settings from .env (copy .env.example to .env first and fill in the password).
# Extra arguments are passed to Maven, e.g. ./run-tests.sh -Dgroups=P1
set -euo pipefail
cd "$(dirname "$0")"

if [ ! -f .env ]; then
  echo "No .env found. Run: cp .env.example .env  - then set EMPLOYEE_PASSWORD in .env" >&2
  exit 1
fi

# Load .env: strips Windows line endings and surrounding quotes; values may reference earlier keys ($EMPLOYEE_USERNAME).
while IFS='=' read -r key value || [ -n "$key" ]; do
  key=$(printf '%s' "$key" | tr -d ' \r')
  case "$key" in ''|\#*) continue ;; esac
  value=$(printf '%s' "$value" | tr -d '\r')
  value=${value#\"}; value=${value%\"}; value=${value#\'}; value=${value%\'}
  case "$value" in \$[A-Za-z_]*) ref=${value#\$}; value=${!ref:-} ;; esac
  export "$key=$value"
done < .env

if [ -z "${EMPLOYEE_PASSWORD:-}" ]; then
  echo "EMPLOYEE_PASSWORD is empty in .env - ask the QA manager for it" >&2
  exit 1
fi

# Role accounts the test data JSON references: default to the employee account when .env does not set them.
for role in EVALUATOR PROJECT_MANAGER ADMIN VERTICAL_HEAD PRACTICE_HEAD DEPARTMENT_HEAD EMPLOYEE_ONLY; do
  user_var="${role}_USERNAME"; pass_var="${role}_PASSWORD"
  [ -n "${!user_var:-}" ] || export "$user_var=$EMPLOYEE_USERNAME"
  [ -n "${!pass_var:-}" ] || export "$pass_var=$EMPLOYEE_PASSWORD"
done

headless=true
[ "${HEADED:-false}" = "true" ] && headless=false

exec mvn -B clean test \
  -Denv="${ENV:-test}" \
  -DbaseUrl="${BASE_URL:-}" \
  -Dbrowser="${BROWSER:-chrome}" \
  -Dheadless="$headless" \
  "$@"
