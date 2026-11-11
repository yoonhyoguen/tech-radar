#!/usr/bin/env bash
cd "$(dirname "$0")/.."

for name in crawler web; do
    pidfile="run/$name.pid"
    if [ -f "$pidfile" ] && kill -0 "$(cat "$pidfile")" 2> /dev/null; then
        kill "$(cat "$pidfile")"
        echo "$name 중지"
    fi
    rm -f "$pidfile"
done
