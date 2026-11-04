import cron from 'node-cron';
import { crawlSource } from './crawl.js';
import { getDueSources } from './repo.js';

const sleep = ms => new Promise(r => setTimeout(r, ms));
let running = false;

cron.schedule(process.env.CRON ?? '* * * * *', async () => {
	if (running) return console.warn('[skip] 이전 수집 진행 중');
	running = true;
	try {
		for (const src of await getDueSources()) {
			await crawlSource(src);
			await sleep(1000);
		}
	} finally {
		running = false;
	}
});

console.log('수집기 시작');