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