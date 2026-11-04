import { pool } from './db.js';
import { fetchRss, fetchHtml } from './fetchers.js';
import { getEnabledSources, upsertArticle } from './repo.js';

const sleep = ms => new Promise(r => setTimeout(r, ms));
const sources = await getEnabledSources();

for (const src of sources) {
	const items = src.type === 'RSS'
		? await fetchRss(src.url)
		: await fetchHtml(src.url, src.list_selector);
	let ins = 0;
	let upd = 0;
	for (const it of items) {
		const r = await upsertArticle(src.id, it);
		if (r.inserted) ins++;
		else if (r.changed) upd++;
	}
	console.log(`${src.name}: 수집 ${items.length} / 신규 ${ins} / 갱신 ${upd}`);
	await sleep(1000);
}

await pool.end();