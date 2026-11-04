import { createHash } from 'node:crypto';
import { pool } from './db.js';

function contentHash(it) {
	return createHash('sha256')
		.update(`${it.title}|${it.summary ?? ''}`)
		.digest('hex');
}

export async function getEnabledSources() {
	const { rows } = await pool.query('SELECT * FROM source WHERE enabled');
	return rows;
}

export async function upsertArticle(sourceId, it) {
	const { rows } = await pool.query(
		`INSERT INTO article (source_id, url, title, summary, published_at, content_hash)
     VALUES ($1, $2, $3, $4, $5, $6)
     ON CONFLICT (url) DO UPDATE SET
       title        = EXCLUDED.title,
       summary      = EXCLUDED.summary,
       content_hash = EXCLUDED.content_hash,
       last_seen_at = now(),
       updated_at   = CASE
         WHEN article.content_hash IS DISTINCT FROM EXCLUDED.content_hash
           OR article.title IS DISTINCT FROM EXCLUDED.title
         THEN now() ELSE article.updated_at END
     RETURNING (xmax = 0) AS inserted, (updated_at = now()) AS changed`,
		[sourceId, it.url, it.title, it.summary, it.publishedAt, contentHash(it)],
	);
	return rows[0];
}

export async function getDueSources() {
	const { rows } = await pool.query(
		`SELECT * FROM source
     WHERE enabled
       AND (last_crawled_at IS NULL
         OR last_crawled_at < now() - make_interval(mins => interval_min))`,
	);
	return rows;
}

export async function startRun(sourceId) {
	const { rows } = await pool.query(
		`INSERT INTO crawl_run (source_id, status) VALUES ($1, 'RUNNING') RETURNING id`,
		[sourceId],
	);
	return rows[0].id;
}

export async function finishRun(id, status, fetched, inserted, updated, error = null) {
	await pool.query(
		`UPDATE crawl_run
     SET finished_at = now(), status = $2, fetched_count = $3,
         inserted_count = $4, updated_count = $5, error_message = $6
     WHERE id = $1`,
		[id, status, fetched, inserted, updated, error],
	);
}

export async function touchSource(id) {
	await pool.query('UPDATE source SET last_crawled_at = now() WHERE id = $1', [id]);
}