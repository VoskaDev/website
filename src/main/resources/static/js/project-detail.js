const escapeHtml = value => String(value ?? '').replace(/[&<>'"]/g, char => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[char]));

async function loadProjectDetail() {
  const root = document.querySelector('#project-detail');
  const slug = new URLSearchParams(location.search).get('slug')
    || decodeURIComponent(location.pathname.split('/').filter(Boolean).pop() || '');
  try {
    const response = await fetch(`/api/projects/${encodeURIComponent(slug)}`);
    if (!response.ok) throw new Error(response.status === 404 ? 'Proje bulunamadı' : 'Proje alınamadı');
    const project = await response.json();
    document.title = `${project.title} · Voska Dev`;
    const images = [...(project.images || [])].sort((a, b) => (Number(b.cover) - Number(a.cover)) || (a.displayOrder - b.displayOrder));
    const links = [
      project.liveUrl ? `<a class="button button-gold" href="${escapeHtml(project.liveUrl)}" target="_blank" rel="noreferrer">CANLI SİTE ↗</a>` : '',
      project.githubUrl ? `<a class="outline-site-button" href="${escapeHtml(project.githubUrl)}" target="_blank" rel="noreferrer">GITHUB ↗</a>` : ''
    ].join('');
    const gallery = images.length
      ? images.map((image, index) => `<figure class="detail-gallery-item"><button type="button" data-full-image="${escapeHtml(image.imageUrl)}" data-full-alt="${escapeHtml(image.altText || project.title)}"><img src="${escapeHtml(image.imageUrl)}" alt="${escapeHtml(image.altText || `${project.title} görsel ${index + 1}`)}"></button><figcaption>${escapeHtml(image.altText || `${index + 1}. görsel`)}</figcaption></figure>`).join('')
      : '<p class="loading">Bu projeye henüz görsel eklenmemiş.</p>';

    root.innerHTML = `<article>
      <section class="project-detail-hero section-shell">
        <a class="detail-back" href="/projects.html">← TÜM PROJELER</a>
        <div class="detail-heading"><div><p class="eyebrow">${project.featured ? 'FEATURED PROJECT' : 'PROJECT'}</p><h1>${escapeHtml(project.title)}</h1></div><p>${escapeHtml(project.summary)}</p></div>
        <div class="detail-meta"><div><span>Yayın tarihi</span><strong>${new Date(project.createdAt).getFullYear()}</strong></div><div><span>Durum</span><strong>Yayında</strong></div><div><span>Galeri</span><strong>${images.length} görsel</strong></div></div>
        <div class="detail-actions">${links}</div>
      </section>
      <section class="project-description section-shell section-border"><div><p class="eyebrow">ABOUT THE PROJECT</p><h2>PROJE<br>AÇIKLAMASI</h2></div><div class="description-text">${escapeHtml(project.description || project.summary).replace(/\r?\n/g, '<br>')}</div></section>
      <section class="project-gallery section-shell section-border"><div class="gallery-heading"><div><p class="eyebrow">PROJECT GALLERY</p><h2>GALERİ</h2></div><span>${images.length} GÖRSEL</span></div><div class="detail-gallery-grid">${gallery}</div></section>
    </article>`;
  } catch (error) {
    root.innerHTML = `<section class="section-shell"><p class="eyebrow">404</p><h1 class="error-heading">${escapeHtml(error.message)}</h1><a class="text-link" href="/projects.html">PROJELERE DÖN</a></section>`;
  }
}

document.querySelector('#project-detail').addEventListener('click', event => {
  const button = event.target.closest('[data-full-image]');
  if (!button) return;
  const viewer = document.createElement('dialog');
  viewer.className = 'image-viewer';
  viewer.innerHTML = `<button type="button" aria-label="Kapat">×</button><img src="${escapeHtml(button.dataset.fullImage)}" alt="${escapeHtml(button.dataset.fullAlt)}">`;
  document.body.appendChild(viewer);
  viewer.querySelector('button').addEventListener('click', () => viewer.close());
  viewer.addEventListener('click', e => { if (e.target === viewer) viewer.close(); });
  viewer.addEventListener('close', () => viewer.remove());
  viewer.showModal();
});

document.querySelector('.menu-toggle').addEventListener('click', event => {
  const nav = document.querySelector('#main-nav');
  event.currentTarget.setAttribute('aria-expanded', String(nav.classList.toggle('open')));
});

loadProjectDetail();
