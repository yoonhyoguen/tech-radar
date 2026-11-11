# YNC Tech Radar

일본 공공기관의 IT·디지털 정책 소식을 주기적으로 수집하고, 쌓인 데이터를 대시보드로 보여 주는 프로젝트입니다.

- `crawler/` (Node.js): RSS·HTML을 수집해 PostgreSQL에 저장하고, 소스별 주기에 맞춰 스스로 다시 수집합니다.
- `web/` (Spring Boot): 쌓인 데이터를 읽어 대시보드, 기사 목록, 실행 이력, 소스 관리 화면을 제공합니다.
- 두 프로그램은 직접 통신하지 않고 PostgreSQL 테이블만 공유합니다. 테이블 생성은 `crawler/schema.sql`이 맡고, Spring은 `ddl-auto=validate`로 읽기 위주로만 사용합니다.

## 기술 스택

| 구분 | 사용 기술 |
|---|---|
| 수집기 | Node.js 20+, ES Module, rss-parser, cheerio, node-cron, pg(Pool), dotenv |
| DB | PostgreSQL 16 |
| 화면 | Spring Boot 4.1, Java 17, Spring Data JPA, Thymeleaf, SB Admin 2, Chart.js 2.9 |

PDF 기준은 Spring Boot 3이지만, Spring Initializr가 더 이상 3.x를 제공하지 않아 수업 예제와 같은 4.1을 사용했습니다. 학교 서버의 Java가 17이라 Java 17로 빌드합니다.

## 실행 순서 (로컬)

1. DB 실행

   ```bash
   docker run -d --name radar-db -e POSTGRES_USER=radar -e POSTGRES_PASSWORD=<비밀번호> -e POSTGRES_DB=radar -p 5432:5432 postgres:16
   ```

2. 테이블과 초기 데이터 적용 (`crawler` 폴더)

   ```bash
   docker cp schema.sql radar-db:/schema.sql
   docker exec radar-db psql -U radar -d radar -f /schema.sql
   docker cp seed.sql radar-db:/seed.sql
   docker exec radar-db psql -U radar -d radar -f /seed.sql
   ```

3. 수집기 (`crawler` 폴더, `.env.example`을 복사해 `.env` 작성)

   ```bash
   npm install
   npm run once   # 1회 수집 (테스트용)
   npm start      # 1분 tick 주기 실행
   ```

4. 웹 (`web` 폴더)

   ```bash
   DB_PASSWORD=<비밀번호> ./gradlew bootRun
   ```

   `http://localhost:8080` 에서 확인합니다. `DB_URL`, `DB_USER`, `PORT` 환경변수로 접속 대상과 포트를 바꿀 수 있습니다.

## 서버 배포

```bash
git clone https://github.com/yoonhyoguen/tech-radar.git
cd tech-radar
bash server/setup.sh    # 최초 1회: DB 이름·계정·포트·비밀번호 입력, 테이블 생성
bash server/start.sh    # 최신 코드 받기 + 웹 빌드 + 수집기·웹 실행
bash server/status.sh   # 실행 상태와 최근 로그
bash server/stop.sh     # 중지
```

비밀번호는 서버의 `crawler/.env`와 `~/.tech-radar-web.env`에만 저장되고 Git에는 올라가지 않습니다.

## 수집 소스

| 소스 | 종류 | 주소 | 주기 | 비고 |
|---|---|---|---|---|
| デジタル庁 (디지털청) 새 소식 | RSS | https://www.digital.go.jp/rss/news.xml | 60분 | 같은 링크를 쓰는 글이 있어 `guid`로 구분 |
| 総務省 (총무성) 새 소식 | RSS | https://www.soumu.go.jp/news.rdf | 60분 | Shift_JIS 인코딩 |
| 個人情報保護委員会 (개인정보보호위원회) 보도자료 | HTML | https://www.ppc.go.jp/news/press/2026 | 60분 | 선택자 `ul.news-list .news-text a` |

세 곳 모두 공공데이터 이용규약(PDL1.0)으로 출처 표시 조건의 재이용을 허용하는 일본 정부기관입니다. 뉴스 매체 중에는 RSS 발췌·전재를 제한하는 곳(ITmedia 등)이나 약관을 확인할 수 없는 곳이 있어 제외했습니다.

## 크롤링 윤리 체크리스트

- [x] 1. robots.txt 확인: 세 사이트 모두 수집 경로가 Disallow 되어 있지 않음
- [x] 2. 이용약관 확인: PDL1.0, 크롤링 금지 조항 없음
- [x] 3. 요청 간격 1초 이상(`sleep(1000)`), 소스 주기 10분 이상(DB `CHECK (interval_min >= 10)`, 화면에서도 검증)
- [x] 4. User-Agent에 학생 프로젝트임을 표시: `YNC-TechRadar-Student/1.0 (edu)`
- [x] 5. 로그인이 필요한 페이지는 수집하지 않음
- [x] 6. 개인정보(이름·연락처·이메일)는 수집하지 않음
- [x] 7. 본문 전체가 아닌 제목·링크·요약(300자 이내)만 저장
- [x] 8. 화면에 원문 링크와 출처를 함께 표시 (기사 목록의 출처 칸, 모든 화면 하단의 출처 문구)

## 갱신 규칙 R1~R8 구현 위치

| 규칙 | 내용 | 구현 위치 |
|---|---|---|
| R1 | 처음 보는 url → INSERT → 신규 | `crawler/src/repo.js` `upsertArticle` (`ON CONFLICT (url)`, `xmax = 0`) |
| R2 | 내용 변경 → `updated_at` 갱신 → 갱신 | 같은 SQL의 `CASE` (해시 또는 제목이 다를 때) |
| R3 | 내용 동일 → `last_seen_at`만 갱신 → 기존 | 같은 SQL, `updated_at`은 이전 값 유지 |
| R4 | `last_crawled_at + interval_min`이 지난 소스만 | `crawler/src/repo.js` `getDueSources` |
| R5 | `enabled = false` 소스 제외 | `getDueSources`, `getEnabledSources`의 `WHERE enabled` |
| R6 | 이전 수집 중이면 이번 tick 건너뜀 | `crawler/src/index.js` `running` 플래그 |
| R7 | 한 소스 실패해도 나머지 계속, FAILED + 에러 기록 | `crawler/src/crawl.js` `try/catch` → `finishRun('FAILED')` |
| R8 | 요청 사이 1초, 타임아웃 10초, 실패해도 `last_crawled_at` 갱신 | `index.js`·`once.js`의 `sleep`, `fetchers.js`의 `AbortSignal.timeout(10000)`, `crawl.js`의 `finally` |

## 기능 요구사항

| ID | 기능 | 위치 |
|---|---|---|
| C1 | RSS 수집 | `crawler/src/fetchers.js` `fetchRss` |
| C2 | HTML 수집 (선택자, 절대 URL) | `fetchers.js` `fetchHtml` |
| C3 | UPSERT 저장 | `repo.js` `upsertArticle` |
| C4 | 1분 tick + 소스별 주기 | `index.js` |
| C5 | 실행 이력 | `repo.js` `startRun` / `finishRun` |
| C6 | 겹침 방지·요청 간격·실패 격리 | `index.js`, `crawl.js` |
| C7 | 1회 실행 | `npm run once` (`once.js`) |
| D1 | 요약 카드 + 수집기 상태 | `DashboardService.summary` (가장 짧은 주기의 2배가 지나면 "수집기 정지 의심") |
| D2 | 기사 목록 검색·소스 필터·서버 페이징 | `ArticleController`, `ArticleRepository.search` |
| D3 | 시간대별 신규 건수 line 차트 | `StatsRepository.hourlyNew` (빈 시간대도 0으로 표시) |
| D4 | 관심 키워드 등록 + TOP bar 차트 | `KeywordController`, `StatsRepository.keywordTop` |
| D5 | 실행 이력 (성공/실패 배지, 에러 메시지) | `RunController`, `runs.html` |
| D6 | 소스 on/off·주기 변경 | `SourceController`, `Source.changeSetting` |
| D7 | 대시보드 60초 자동 새로고침 | `fragments/layout.html`의 `meta refresh` |

## 운영 결과

로컬 기준 (2026-11-04 ~ 2026-11-11): 실행 15회, 성공률 93.3% (실패 1회는 R7 확인용으로 URL을 일부러 고장 낸 것), 수집 기사 129건.

서버 운영 결과는 대시보드의 실행 이력 화면 상단(총 실행 횟수, 성공률)에서 확인해 제출 전에 이 칸을 갱신합니다.

## 사용한 AI 도구와 고친 사례

사용 도구: Claude Code (Anthropic Claude). 요청과 수정 내용은 `docs/prompt-log.md`에 기록했습니다.

AI가 틀려서 고친 사례 Best 3

1. **중복 URL**: 예제 코드대로 RSS의 `link`를 그대로 저장하자 디지털청 39건 중 23건만 남았습니다. 입찰 공고 15건이 같은 `/procurement` 주소를 썼기 때문입니다. 글마다 다른 `guid`를 `#` 뒤에 붙여 해결했습니다.
2. **갱신 판정 누락**: `content_hash`만 비교하면 DB에서 제목만 고쳤을 때 해시가 그대로라 "기존"으로 판정됐습니다. 갱신 조건에 제목 비교를 추가했습니다.
3. **일본어 깨짐**: 총무성 RSS가 Shift_JIS라 제목 20건이 깨진 채 저장됐습니다. XML 선언의 인코딩을 읽어 `TextDecoder`로 변환한 뒤 파싱하도록 바꿨습니다.

## 화면

`docs/screenshots/`에 대시보드와 실행 이력 화면 캡처를 넣습니다.
