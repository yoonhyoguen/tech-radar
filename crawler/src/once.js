import { fetchRss } from './fetchers.js';

const items = await fetchRss('https://www.digital.go.jp/rss/news.xml');

console.log('전체', items.length);
console.log('고유 url', new Set(items.map(it => it.url)).size);
console.log(items.slice(0, 3));