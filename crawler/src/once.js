import { pool } from './db.js';
import { crawlSource } from './crawl.js';
import { getEnabledSources } from './repo.js';

const sleep = ms => new Promise(r => setTimeout(r, ms));
const sources = await getEnabledSources();

for (const src of sources) {
	await crawlSource(src);
	await sleep(1000);
}

await pool.end();