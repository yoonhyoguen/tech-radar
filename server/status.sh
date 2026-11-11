#!/usr/bin/env bash
cd "$(dirname "$0")/.."

for name in crawler web; do
    pidfile="run/$name.pid"
    if [ -f "$pidfile" ] && kill -0 "$(cat "$pidfile")" 2> /dev/null; then
        echo "$name: 실행 중 (pid $(cat "$pidfile"))"
    else
        echo "$name: 멈춤"
    fi
done

echo "--- 수집기 로그"
tail -n 8 logs/crawler.log 2> /dev/null
echo "--- 웹 로그"
tail -n 5 logs/web.log 2> /dev/null
