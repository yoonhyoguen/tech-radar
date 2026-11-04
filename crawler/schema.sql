CREATE TABLE source (
                        id              BIGSERIAL PRIMARY KEY,
                        name            VARCHAR(100) NOT NULL,
                        type            VARCHAR(10)  NOT NULL CHECK (type IN ('RSS', 'HTML')),
                        url             TEXT         NOT NULL UNIQUE,
                        list_selector   TEXT,
                        enabled         BOOLEAN      NOT NULL DEFAULT TRUE,
                        interval_min    INT          NOT NULL DEFAULT 60 CHECK (interval_min >= 10),
                        last_crawled_at TIMESTAMPTZ
);

CREATE TABLE article (
                         id            BIGSERIAL PRIMARY KEY,
                         source_id     BIGINT      NOT NULL REFERENCES source (id),
                         url           TEXT        NOT NULL UNIQUE,
                         title         TEXT        NOT NULL,
                         summary       TEXT,
                         published_at  TIMESTAMPTZ,
                         content_hash  VARCHAR(64) NOT NULL,
                         first_seen_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                         last_seen_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
                         updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE crawl_run (
                           id             BIGSERIAL PRIMARY KEY,
                           source_id      BIGINT      NOT NULL REFERENCES source (id),
                           started_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
                           finished_at    TIMESTAMPTZ,
                           status         VARCHAR(10) NOT NULL CHECK (status IN ('RUNNING', 'SUCCESS', 'FAILED')),
                           fetched_count  INT         NOT NULL DEFAULT 0,
                           inserted_count INT         NOT NULL DEFAULT 0,
                           updated_count  INT         NOT NULL DEFAULT 0,
                           error_message  TEXT
);

CREATE TABLE tracked_keyword (
                                 id         BIGSERIAL PRIMARY KEY,
                                 word       VARCHAR(100) NOT NULL UNIQUE,
                                 created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);