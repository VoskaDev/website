const api = {
  async get(url) { const response = await fetch(url); if (!response.ok) throw new Error('İçerik alınamadı'); return response.json(); },
  async post(url, body) { const response = await fetch(url,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(body)}); if (!response.ok) throw new Error('İstek gönderilemedi'); }
};

const escapeHtml = value => String(value ?? '').replace(/[&<>'"]/g, char => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[char]));

async function loadProjects(){
  const root=document.querySelector('#project-list');
  try{
    const projects=await api.get('/api/projects/featured');
    if(!projects.length){root.innerHTML='<p class="loading">Henüz yayınlanmış proje bulunmuyor.</p>';return;}
    root.innerHTML=projects.map(project=>{
      const cover=(project.images||[]).find(image=>image.cover)||(project.images||[])[0];
      const visual=cover?`<img src="${escapeHtml(cover.imageUrl)}" alt="${escapeHtml(cover.altText||project.title)}">`:`<div class="project-placeholder">${escapeHtml(project.title.slice(0,3).toUpperCase())}</div>`;
      const links=[project.liveUrl?`<a class="text-link" href="${escapeHtml(project.liveUrl)}" target="_blank" rel="noreferrer">LIVE DEMO ↗</a>`:'',project.githubUrl?`<a class="text-link" href="${escapeHtml(project.githubUrl)}" target="_blank" rel="noreferrer">GITHUB ↗</a>`:''].join('');
      return `<article class="project-card"><div class="project-visual">${visual}</div><div class="project-content"><p class="eyebrow">${project.featured?'FEATURED PROJECT':'PROJECT'}</p><h3>${escapeHtml(project.title)}</h3><p>${escapeHtml(project.summary)}</p><div class="project-meta"><div><span>Yayın tarihi</span><span>${new Date(project.createdAt).getFullYear()}</span></div><div><span>Durum</span><span>Yayında</span></div></div><div class="project-links">${links}</div></div></article>`;
    }).join('');
  }catch(error){root.innerHTML=`<p class="loading">${escapeHtml(error.message)}</p>`;}
}

async function loadProfile(){
  try{
    const profile=await api.get('/api/profile');
    document.querySelectorAll('[data-social]').forEach(link=>{const url=profile[link.dataset.social];if(url)link.href=url;});
    if(profile.contactEmail){const email=document.querySelector('#contact-email');email.textContent=profile.contactEmail;email.href=`mailto:${profile.contactEmail}`;}
  }catch(error){console.warn(error.message);}
}

document.querySelector('.menu-toggle').addEventListener('click',event=>{const nav=document.querySelector('#main-nav');const open=nav.classList.toggle('open');event.currentTarget.setAttribute('aria-expanded',String(open));});
document.querySelectorAll('.main-nav a').forEach(link=>link.addEventListener('click',()=>document.querySelector('#main-nav').classList.remove('open')));
document.querySelector('#contact-form').addEventListener('submit',async event=>{event.preventDefault();const form=event.currentTarget;const status=document.querySelector('#form-status');status.textContent='Gönderiliyor…';try{await api.post('/api/contact',Object.fromEntries(new FormData(form)));form.reset();status.textContent='Mesajınız alındı.';}catch(error){status.textContent=error.message;}});

loadProjects();loadProfile();
