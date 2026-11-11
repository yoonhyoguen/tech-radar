#!/usr/bin/env bash
# 최신 코드 받기 + 웹 빌드 + 수집기·웹 백그라운드 실행
set -euo pipefail
cd "$(dirname "$0")/.."
ROOT=$(pwd)
mkdir -p "$ROOT/logs" "$ROOT/run"

if [ ! -f "$ROOT/crawler/.env" ] || [ ! -f "$HOME/.tech-radar-web.env" ]; then
    echo "먼저 bash server/setup.sh 를 실행하세요."
    exit 1
fi

git pull --ff-only
bash "$ROOT/server/stop.sh"

(cd "$ROOT/crawler" && npm ci --omit=dev --no-audit --no-fund --silent)

if command -v javac > /dev/null; then
    echo "웹 빌드 중 (처음에는 몇 분 걸립니다)..."
    (cd "$ROOT/web" && bash gradlew bootJar --no-daemon -q)
fi
JAR="$ROOT/web/build/libs/web.jar"

cd "$ROOT/crawler"
nohup node src/index.js >> "$ROOT/logs/crawler.log" 2>&1 &
echo $! > "$ROOT/run/crawler.pid"
echo "수집기 실행 (pid $(cat "$ROOT/run/crawler.pid"))"

if [ -f "$JAR" ]; then
    set -a
    . "$HOME/.tech-radar-web.env"
    set +a
    nohup java -jar "$JAR" >> "$ROOT/logs/web.log" 2>&1 &
    echo $! > "$ROOT/run/web.pid"
    echo "웹 실행 (pid $(cat "$ROOT/run/web.pid"), 포트 $PORT)"
else
    echo "web.jar 가 없습니다. 서버에 javac(JDK)가 없으면 강사님께 확인하세요."
fi
