#!/usr/bin/env bash
# 서버 최초 1회: 접속 정보 저장 + 테이블 생성 + 수집기 패키지 설치
set -euo pipefail
cd "$(dirname "$0")/.."
ROOT=$(pwd)

read -r -p "DB 이름 (예: radar04): " DB_NAME
read -r -p "DB 사용자 (예: student04): " DB_USER
read -r -p "웹 포트 (예: 8004): " PORT
read -r -s -p "DB 비밀번호: " DB_PASSWORD
echo

ENCODED=$(node -e 'process.stdout.write(encodeURIComponent(process.argv[1]))' "$DB_PASSWORD")

umask 077
printf 'DATABASE_URL=postgres://%s:%s@localhost:5432/%s\n' "$DB_USER" "$ENCODED" "$DB_NAME" > "$ROOT/crawler/.env"
{
    printf 'DB_URL=%q\n' "jdbc:postgresql://localhost:5432/$DB_NAME"
    printf 'DB_USER=%q\n' "$DB_USER"
    printf 'DB_PASSWORD=%q\n' "$DB_PASSWORD"
    printf 'PORT=%q\n' "$PORT"
} > "$HOME/.tech-radar-web.env"
echo "접속 정보 저장: crawler/.env, ~/.tech-radar-web.env"

export PGPASSWORD="$DB_PASSWORD"
psql -h localhost -U "$DB_USER" -d "$DB_NAME" -v ON_ERROR_STOP=1 -q -f "$ROOT/crawler/schema.sql"
psql -h localhost -U "$DB_USER" -d "$DB_NAME" -v ON_ERROR_STOP=1 -q -f "$ROOT/crawler/seed.sql"
psql -h localhost -U "$DB_USER" -d "$DB_NAME" -c '\dt'

(cd "$ROOT/crawler" && npm ci --omit=dev --no-audit --no-fund)
echo "준비 완료. 이제 bash server/start.sh 를 실행하세요."
