const api = {
  async get(url) {
    const response = await fetch(url);
    if (!response.ok) throw new Error('İçerik alınamadı');
    return response.json();
  },
  async post(url, body) {
    const response = await fetch(url, {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(body)
    });
    if (!response.ok) throw new Error('İstek gönderilemedi');
  }
};

const escapeHtml = value => String(value ?? '').replace(
  /[&<>'"]/g,
  char => ({'&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;'}[char])
);

function createProjectGallery(project) {
  const images = [...(project.images || [])]
    .sort((first, second) => (Number(second.cover) - Number(first.cover)) || (first.displayOrder - second.displayOrder));

  if (!images.length) {
    return `<div class="project-media"><div class="project-visual"><div class="project-placeholder">${escapeHtml(project.title.slice(0, 3).toUpperCase())}</div></div></div>`;
  }

  const cover = images[0];
  const thumbnailImages = images.slice(0, 4);
  const thumbnails = thumbnailImages.length > 1
    ? `<div class="project-thumbnails" aria-label="${escapeHtml(project.title)} görsel galerisi">
        ${thumbnailImages.map((image, index) => `
          <button type="button" class="project-thumbnail ${index === 0 ? 'active' : ''}"
                  data-gallery-src="${escapeHtml(image.imageUrl)}"
                  data-gallery-alt="${escapeHtml(image.altText || project.title)}"
                  aria-label="${index + 1}. görseli göster">
            <img src="${escapeHtml(image.imageUrl)}" alt="">
            <span>${index + 1}</span>
          </button>`).join('')}
       </div><p class="gallery-count"><span>1</span> / ${thumbnailImages.length}${images.length > 4 ? ` · <a href="/projects/${encodeURIComponent(project.slug)}">+${images.length - 4} görsel</a>` : ''}</p>`
    : '';

  return `<div class="project-media">
    <div class="project-visual">
      <img class="project-main-image" src="${escapeHtml(cover.imageUrl)}" alt="${escapeHtml(cover.altText || project.title)}">
    </div>
    ${thumbnails}
  </div>`;
}

async function loadProjects() {
  const root = document.querySelector('#project-list');
  try {
    const projects = await api.get('/api/projects/featured');
    if (!projects.length) {
      root.innerHTML = '<p class="loading">Henüz yayınlanmış proje bulunmuyor.</p>';
      return;
    }

    root.innerHTML = projects.map(project => {
      const links = [
        project.liveUrl ? `<a class="text-link" href="${escapeHtml(project.liveUrl)}" target="_blank" rel="noreferrer">LIVE DEMO ↗</a>` : '',
        project.githubUrl ? `<a class="text-link" href="${escapeHtml(project.githubUrl)}" target="_blank" rel="noreferrer">GITHUB ↗</a>` : ''
      ].join('');

      return `<article class="project-card">
        ${createProjectGallery(project)}
        <div class="project-content">
          <p class="eyebrow">${project.featured ? 'FEATURED PROJECT' : 'PROJECT'}</p>
          <h3><a class="project-title-link" href="/projects/${encodeURIComponent(project.slug)}">${escapeHtml(project.title)}</a></h3>
          <p>${escapeHtml(project.summary)}</p>
          <div class="project-meta">
            <div><span>Yayın tarihi</span><span>${new Date(project.createdAt).getFullYear()}</span></div>
            <div><span>Durum</span><span>Yayında</span></div>
          </div>
          <div class="project-links"><a class="text-link" href="/projects/${encodeURIComponent(project.slug)}">DETAYLARI GÖR →</a>${links}</div>
        </div>
      </article>`;
    }).join('');
  } catch (error) {
    root.innerHTML = `<p class="loading">${escapeHtml(error.message)}</p>`;
  }
}

async function loadProfile() {
  try {
    const profile = await api.get('/api/profile');
    document.querySelectorAll('[data-social]').forEach(link => {
      const url = profile[link.dataset.social];
      if (url) link.href = url;
    });
    if (profile.contactEmail) {
      const email = document.querySelector('#contact-email');
      email.textContent = profile.contactEmail;
      email.href = `mailto:${profile.contactEmail}`;
    }
  } catch (error) {
    console.warn(error.message);
  }
}

document.querySelector('#project-list').addEventListener('click', event => {
  const thumbnail = event.target.closest('[data-gallery-src]');
  if (!thumbnail) return;

  const media = thumbnail.closest('.project-media');
  const mainImage = media.querySelector('.project-main-image');
  mainImage.src = thumbnail.dataset.gallerySrc;
  mainImage.alt = thumbnail.dataset.galleryAlt;
  media.querySelectorAll('.project-thumbnail')
    .forEach(button => button.classList.toggle('active', button === thumbnail));
  media.querySelector('.gallery-count span').textContent = thumbnail.querySelector('span').textContent;
});

document.querySelector('.menu-toggle').addEventListener('click', event => {
  const nav = document.querySelector('#main-nav');
  const open = nav.classList.toggle('open');
  event.currentTarget.setAttribute('aria-expanded', String(open));
});

document.querySelectorAll('.main-nav a').forEach(link =>
  link.addEventListener('click', () => document.querySelector('#main-nav').classList.remove('open'))
);

document.querySelector('#contact-form').addEventListener('submit', async event => {
  event.preventDefault();
  const form = event.currentTarget;
  const status = document.querySelector('#form-status');
  status.textContent = 'Gönderiliyor…';
  try {
    await api.post('/api/contact', Object.fromEntries(new FormData(form)));
    form.reset();
    status.textContent = 'Mesajınız gönderildi.';
  } catch (error) {
    status.textContent = error.message;
  }
});

loadProjects();
loadProfile();
