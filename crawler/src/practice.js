import * as cheerio from 'cheerio';

const url = 'https://books.toscrape.com/';
const res = await fetch(url, {
	headers: { 'User-Agent': 'YNC-TechRadar-Student/1.0 (edu)' },
	signal: AbortSignal.timeout(10000),
});
if (!res.ok) throw new Error(`HTTP ${res.status}`);

const $ = cheerio.load(await res.text());
const titles = $('article.product_pod h3 a')
	.map((_, el) => $(el).attr('title'))
	.get();

console.log(titles.length);
titles.forEach(title => console.log(title));