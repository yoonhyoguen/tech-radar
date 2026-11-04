import { pool } from './db.js';
import { fetchRss } from './fetchers.js';
import { getEnabledSources, upsertArticle } from './repo.js';

const sources = await getEnabledSources();

for (const src of sources) {
	const items = await fetchRss(src.url);
	let ins = 0;
	let upd = 0;
	for (const it of items) {
		const r = await upsertArticle(src.id, it);
		if (r.inserted) ins++;
		else if (r.changed) upd++;
	}
	console.log(`${src.name}: 수집 ${items.length} / 신규 ${ins} / 갱신 ${upd}`);
}

await pool.end();