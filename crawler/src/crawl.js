import { fetchRss, fetchHtml } from './fetchers.js';
import { startRun, finishRun, touchSource, upsertArticle } from './repo.js';

export async function crawlSource(src) {
	const runId = await startRun(src.id);
	try {
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
		await finishRun(runId, 'SUCCESS', items.length, ins, upd);
		console.log(`${src.name}: 수집 ${items.length} / 신규 ${ins} / 갱신 ${upd}`);
	} catch (e) {
		await finishRun(runId, 'FAILED', 0, 0, 0, e.message);
		console.error(`${src.name}: 실패 ${e.message}`);
	} finally {
		await touchSource(src.id);
	}
}