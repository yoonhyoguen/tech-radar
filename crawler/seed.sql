INSERT INTO source (name, type, url, list_selector, enabled, interval_min)
VALUES
    (
        'デジタル庁',
        'RSS',
        'https://www.digital.go.jp/rss/news.xml',
        NULL,
        TRUE,
        60
    ),
    (
        '総務省',
        'RSS',
        'https://www.soumu.go.jp/news.rdf',
        NULL,
        FALSE,
        60
    ),
    (
        '個人情報保護委員会',
        'HTML',
        'https://www.ppc.go.jp/news/press/2026',
        'ul.news-list .news-text a',
        FALSE,
        60
    )
    ON CONFLICT (url) DO NOTHING;