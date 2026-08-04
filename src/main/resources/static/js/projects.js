const escapeHtml = value => String(value ?? '').replace(/[&<>'"]/g, char => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[char]));

async function loadAllProjects() {
  const root = document.querySelector('#all-project-list');
  try {
    const response = await fetch('/api/projects');
    if (!response.ok) throw new Error('Projeler alınamadı');
    const projects = await response.json();
    if (!projects.length) {
      root.innerHTML = '<p class="loading">Henüz yayınlanmış proje bulunmuyor.</p>';
      return;
    }
    root.innerHTML = projects.map(project => {
      const images = [...(project.images || [])].sort((a, b) => (Number(b.cover) - Number(a.cover)) || (a.displayOrder - b.displayOrder));
      const cover = images[0];
      const visual = cover
        ? `<img src="${escapeHtml(cover.imageUrl)}" alt="${escapeHtml(cover.altText || project.title)}">`
        : `<div class="project-placeholder">${escapeHtml(project.title.slice(0, 3).toUpperCase())}</div>`;
      return `<article class="project-index-card">
        <a class="project-index-cover" href="/project.html?slug=${encodeURIComponent(project.slug)}">${visual}</a>
        <div class="project-index-content">
          <p class="eyebrow">${project.featured ? 'FEATURED PROJECT' : 'PROJECT'}</p>
          <h2><a href="/project.html?slug=${encodeURIComponent(project.slug)}">${escapeHtml(project.title)}</a></h2>
          <p>${escapeHtml(project.summary)}</p>
          <div><span>${images.length} görsel</span><a class="text-link" href="/project.html?slug=${encodeURIComponent(project.slug)}">PROJEYİ İNCELE →</a></div>
        </div>
      </article>`;
    }).join('');
  } catch (error) {
    root.innerHTML = `<p class="loading">${escapeHtml(error.message)}</p>`;
  }
}

document.querySelector('.menu-toggle').addEventListener('click', event => {
  const nav = document.querySelector('#main-nav');
  event.currentTarget.setAttribute('aria-expanded', String(nav.classList.toggle('open')));
});

loadAllProjects();
