import Parser from 'rss-parser';
import * as cheerio from 'cheerio';

const UA = 'YNC-TechRadar-Student/1.0 (edu)';
const parser = new Parser();

async function get(url) {
	const res = await fetch(url, {
		headers: { 'User-Agent': UA },
		signal: AbortSignal.timeout(10000),
	});
	if (!res.ok) throw new Error(`HTTP ${res.status}`);
	return res.arrayBuffer();
}

function decodeXml(buf) {
	const head = new TextDecoder('ascii').decode(buf.slice(0, 200));
	const encoding = head.match(/encoding=["']([^"']+)["']/)?.[1] ?? 'utf-8';
	return new TextDecoder(encoding).decode(buf);
}

function uniqueUrl(it) {
	if (!it.guid || it.guid.startsWith('http')) return it.link;
	return `${it.link}#${it.guid}`;
}

export async function fetchRss(url) {
	const feed = await parser.parseString(decodeXml(await get(url)));
	return feed.items.map(it => ({
		url: uniqueUrl(it),
		title: it.title?.trim(),
		summary: it.contentSnippet?.slice(0, 300) || null,
		publishedAt: it.isoDate ? new Date(it.isoDate) : null,
	}));
}

export async function fetchHtml(url, selector) {
	const buf = await get(url);
	const $ = cheerio.load(new TextDecoder('utf-8').decode(buf));
	return $(selector).map((_, el) => ({
		url: new URL($(el).attr('href'), url).href,
		title: $(el).text().trim(),
		summary: null,
		publishedAt: null,
	})).get();
}