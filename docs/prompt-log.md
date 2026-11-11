# Prompt Log

## C1 RSS 수집
- 요청: 디지털청 RSS에서 제목, 링크, 요약, 날짜를 가져오는 fetchRss 함수
- 문제: 39건 중 고유 링크가 23개뿐이었다. 입찰 공고 15건이 같은 /procurement 주소를 썼다.
- 수정: guid가 모두 달라서 링크 뒤에 #guid를 붙여 고유하게 만들었다.

## C3 UPSERT 저장
- 요청: url 기준으로 신규, 갱신, 기존을 구분하는 upsertArticle
- 문제: content_hash만 비교하면 DB에서 제목만 바꿨을 때 갱신이 아니라 기존으로 판정됐다.
- 수정: 갱신 조건에 title 비교를 추가했다.

## C2 HTML 수집 + 총무성 RSS
- 문제 1: 총무성 RSS가 Shift_JIS라서 제목 20건이 깨진 채 저장됐다.
- 수정 1: XML 첫 줄의 encoding을 읽어 TextDecoder로 변환한 뒤 파싱했다.
- 문제 2: once.js가 모든 소스를 fetchRss로 읽어 HTML 페이지에서 Unexpected close tag 오류가 났다.
- 수정 2: source.type이 RSS인지 HTML인지에 따라 fetchRss, fetchHtml을 나눠 호출했다.