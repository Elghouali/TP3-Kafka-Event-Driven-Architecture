/* Live SSE UI; snapshots are cumulative per product, not individual sales. */
const store = new Map();
const el = (id) => document.getElementById(id);
const money = (value) => Number(value || 0).toLocaleString('en-US',{minimumFractionDigits:2,maximumFractionDigits:2});
const escapeHTML = (v) => String(v).replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
function render(){
  const items=[...store.values()].sort((a,b)=>Number(b.revenue)-Number(a.revenue));
  const revenue=items.reduce((x,v)=>x+Number(v.revenue||0),0);
  el('revenue').textContent=`${money(revenue)} MAD`;
  el('orders').textContent=items.reduce((x,v)=>x+Number(v.orders||0),0).toLocaleString();
  el('units').textContent=items.reduce((x,v)=>x+Number(v.units||0),0).toLocaleString();
  el('products').textContent=items.length;
  const max=Math.max(1,...items.map(v=>Number(v.revenue||0)));
  el('bars').innerHTML=items.length ? items.map(v=>`<div><div class="bar-label"><span>${escapeHTML(v.product)}</span><b>${money(v.revenue)} MAD</b></div><div class="track"><div class="fill" style="width:${(100*Number(v.revenue||0)/max).toFixed(1)}%"></div></div></div>`).join('') : '<p class="empty">Awaiting Kafka events…</p>';
  el('table').innerHTML=items.length ? items.map(v=>`<tr><td>${escapeHTML(v.product)}</td><td>${Number(v.orders).toLocaleString()}</td><td>${Number(v.units).toLocaleString()}</td><td>${money(v.revenue)}</td><td>${new Date(Number(v.lastUpdatedEpochMs)).toLocaleTimeString()}</td></tr>`).join('') : '<tr><td colspan="5" class="empty">No events yet. Start the supplier or POST a sale.</td></tr>';
}
fetch('/api/analytics').then(r=>r.json()).then(items=>{items.forEach(v=>store.set(v.product,v));render();}).catch(()=>{});
const stream=new EventSource('/api/stream');
stream.addEventListener('summary',e=>{try{const v=JSON.parse(e.data);store.set(v.product,v);render();}catch(err){console.warn(err);}});
stream.addEventListener('ready',()=>{el('connection').textContent='Live · Kafka connected';el('connection').classList.add('live');});
stream.onerror=()=>{el('connection').textContent='Reconnecting…';el('connection').classList.remove('live');};
render();
