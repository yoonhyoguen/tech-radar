import Parser from 'rss-parser';

const UA = 'YNC-TechRadar-Student/1.0 (edu)';
const parser = new Parser({
	timeout: 10000,
	headers: { 'User-Agent': UA },
});

function uniqueUrl(it) {
	if (!it.guid || it.guid.startsWith('http')) return it.link;
	return `${it.link}#${it.guid}`;
}

export async function fetchRss(url) {
	const feed = await parser.parseURL(url);
	return feed.items.map(it => ({
		url: uniqueUrl(it),
		title: it.title?.trim(),
		summary: it.contentSnippet?.slice(0, 300) || null,
		publishedAt: it.isoDate ? new Date(it.isoDate) : null,
	}));
}