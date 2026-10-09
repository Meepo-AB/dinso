#!/bin/sh
# Build and start a Dinso customer application.
# Usage: ./start-customer.sh --<svenskebanken|pensionsbolaget|finbanken> [Spring Boot arguments]

set -eu

if [ "$#" -lt 1 ]; then
  echo "Usage: $0 --<svenskebanken|pensionsbolaget|finbanken> [Spring Boot arguments]" >&2
  exit 64
fi

customer_flag=$1
shift

case "$customer_flag" in
  --svenskebanken) customer=svenskebanken; api_port=8081 ;;
  --pensionsbolaget) customer=pensionsbolaget; api_port=8082 ;;
  --finbanken) customer=finbanken; api_port=8083 ;;
  *)
    echo "Unknown customer flag: $customer_flag" >&2
    echo "Supported flags: --svenskebanken, --pensionsbolaget, --finbanken" >&2
    exit 64
    ;;
esac

if ! command -v java >/dev/null 2>&1; then
  echo "Java is required but was not found on PATH." >&2
  exit 69
fi

script_dir=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$script_dir"

./gradlew ":customers:$customer:bootJar" --no-daemon

jar_dir="customers/$customer/build/libs"
jar=''
for candidate in "$jar_dir"/*.jar; do
  [ -f "$candidate" ] || continue
  case "$candidate" in
    *-plain.jar) ;;
    *) jar=$candidate ;;
  esac
done

if [ -z "$jar" ]; then
  echo "Could not find the executable JAR in $jar_dir." >&2
  exit 1
fi

exec java -jar "$jar" "$@" --server.port="$api_port"
