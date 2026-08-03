const state={projects:[],filter:'ALL',editing:null};
const dialog=document.querySelector('#project-dialog');
const projectForm=document.querySelector('#project-form');
const escapeHtml=value=>String(value??'').replace(/[&<>'"]/g,char=>({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[char]));

async function request(url,options={}){
  const config={credentials:'same-origin',...options,headers:{...(options.body instanceof FormData?{}:{'Content-Type':'application/json'}),...(options.headers||{})}};
  const response=await fetch(url,config);
  if(response.status===401||response.status===403){location.href='/admin/login.html';throw new Error('Oturum gerekli');}
  if(!response.ok){let message='İşlem başarısız';try{const body=await response.json();message=body.detail||body.message||message;}catch{}throw new Error(message);}
  return response.status===204?null:response.json();
}

function coverOf(project){return (project.images||[]).find(image=>image.cover)||(project.images||[])[0];}
function renderProjects(){
  const visible=state.projects.filter(project=>state.filter==='ALL'||project.status===state.filter);
  document.querySelector('#total-count').textContent=state.projects.length;
  document.querySelector('#published-count').textContent=state.projects.filter(item=>item.status==='PUBLISHED').length;
  document.querySelector('#draft-count').textContent=state.projects.filter(item=>item.status==='DRAFT').length;
  const table=document.querySelector('#project-table');
  if(!visible.length){table.innerHTML='<p class="empty">Bu filtrede proje bulunmuyor.</p>';return;}
  table.innerHTML=visible.map(project=>{const cover=coverOf(project);const image=cover?`<img class="row-cover" src="${escapeHtml(cover.imageUrl)}" alt="">`:`<div class="row-cover row-placeholder">${escapeHtml(project.title.slice(0,2).toUpperCase())}</div>`;return `<article class="project-row">${image}<div class="row-name"><strong>${escapeHtml(project.title)}</strong><span>${escapeHtml(project.slug)}</span></div><span class="status ${project.status==='DRAFT'?'draft':''}">${project.status==='DRAFT'?'TASLAK':'YAYINDA'}</span><span class="row-date">${new Date(project.updatedAt).toLocaleDateString('tr-TR')}</span><div class="row-actions"><button data-edit="${project.id}">DÜZENLE</button><button class="delete" data-delete="${project.id}">SİL</button></div></article>`;}).join('');
}

async function loadProjects(){try{state.projects=await request('/api/admin/projects');renderProjects();}catch(error){document.querySelector('#project-table').innerHTML=`<p class="empty">${escapeHtml(error.message)}</p>`;}}
function resetProjectForm(){projectForm.reset();projectForm.elements.id.value='';projectForm.elements.status.value='DRAFT';state.editing=null;document.querySelector('#dialog-title').textContent='YENİ PROJE';document.querySelector('#image-manager').hidden=true;document.querySelector('#project-status').textContent='';}
function openNewProject(){resetProjectForm();dialog.showModal();}
async function openProject(id){
  const project=await request(`/api/admin/projects/${id}`);state.editing=project;
  for(const key of ['id','title','summary','description','githubUrl','liveUrl','status'])projectForm.elements[key].value=project[key]??'';
  projectForm.elements.featured.checked=project.featured;document.querySelector('#dialog-title').textContent='PROJEYİ DÜZENLE';document.querySelector('#image-manager').hidden=false;renderImages(project.images||[]);dialog.showModal();
}
function projectPayload(){return {title:projectForm.elements.title.value.trim(),summary:projectForm.elements.summary.value.trim(),description:projectForm.elements.description.value.trim(),githubUrl:projectForm.elements.githubUrl.value.trim()||null,liveUrl:projectForm.elements.liveUrl.value.trim()||null,featured:projectForm.elements.featured.checked,status:projectForm.elements.status.value};}
projectForm.addEventListener('submit',async event=>{event.preventDefault();const status=document.querySelector('#project-status');status.textContent='Kaydediliyor…';try{const id=projectForm.elements.id.value;const saved=await request(id?`/api/admin/projects/${id}`:'/api/admin/projects',{method:id?'PUT':'POST',body:JSON.stringify(projectPayload())});state.editing=saved;projectForm.elements.id.value=saved.id;document.querySelector('#image-manager').hidden=false;status.textContent='Kaydedildi.';await loadProjects();}catch(error){status.textContent=error.message;}});

function renderImages(images){const root=document.querySelector('#image-list');root.innerHTML=images.length?images.map(image=>`<div class="image-item"><img src="${escapeHtml(image.imageUrl)}" alt="${escapeHtml(image.altText||'')}"><button type="button" data-image-delete="${image.id}" aria-label="Görseli sil">×</button><small>${image.cover?'KAPAK':'GÖRSEL'} · ${image.displayOrder}</small></div>`).join(''):'<p class="empty">Görsel yok.</p>';}
document.querySelector('#upload-image').addEventListener('click',async()=>{const id=projectForm.elements.id.value;const file=document.querySelector('#image-file').files[0];if(!id||!file)return;const formData=new FormData();formData.append('file',file);formData.append('data',new Blob([JSON.stringify({altText:document.querySelector('#image-alt').value,displayOrder:(state.editing?.images||[]).length,cover:document.querySelector('#image-cover').checked})],{type:'application/json'}));try{await request(`/api/admin/projects/${id}/images`,{method:'POST',body:formData});state.editing=await request(`/api/admin/projects/${id}`);renderImages(state.editing.images||[]);document.querySelector('#image-file').value='';await loadProjects();}catch(error){document.querySelector('#project-status').textContent=error.message;}});
document.querySelector('#image-list').addEventListener('click',async event=>{const button=event.target.closest('[data-image-delete]');if(!button||!state.editing)return;if(!confirm('Bu görsel silinsin mi?'))return;await request(`/api/admin/projects/${state.editing.id}/images/${button.dataset.imageDelete}`,{method:'DELETE'});state.editing=await request(`/api/admin/projects/${state.editing.id}`);renderImages(state.editing.images||[]);await loadProjects();});

document.querySelector('#project-table').addEventListener('click',async event=>{const edit=event.target.closest('[data-edit]');const remove=event.target.closest('[data-delete]');if(edit)await openProject(edit.dataset.edit);if(remove&&confirm('Bu proje kalıcı olarak silinsin mi?')){await request(`/api/admin/projects/${remove.dataset.delete}`,{method:'DELETE'});await loadProjects();}});
document.querySelector('#new-project').addEventListener('click',openNewProject);document.querySelector('.close-dialog').addEventListener('click',()=>dialog.close());dialog.addEventListener('click',event=>{if(event.target===dialog)dialog.close();});
document.querySelectorAll('.filter').forEach(button=>button.addEventListener('click',()=>{document.querySelectorAll('.filter').forEach(item=>item.classList.toggle('active',item===button));state.filter=button.dataset.filter;renderProjects();}));

async function loadProfile(){try{const profile=await request('/api/profile');const form=document.querySelector('#profile-form');Object.keys(profile).forEach(key=>{if(form.elements[key])form.elements[key].value=profile[key]??'';});}catch(error){document.querySelector('#profile-status').textContent=error.message;}}
document.querySelector('#profile-form').addEventListener('submit',async event=>{event.preventDefault();const form=event.currentTarget;const status=document.querySelector('#profile-status');status.textContent='Kaydediliyor…';const body={};for(const key of ['githubUrl','linkedinUrl','discordUrl','websiteUrl','xUrl','instagramUrl','resumeUrl','contactEmail'])body[key]=form.elements[key].value.trim()||null;try{await request('/api/admin/profile',{method:'PUT',body:JSON.stringify(body)});status.textContent='Kaydedildi.';}catch(error){status.textContent=error.message;}});

document.querySelectorAll('.nav-button').forEach(button=>button.addEventListener('click',()=>{document.querySelectorAll('.nav-button').forEach(item=>item.classList.toggle('active',item===button));document.querySelectorAll('.admin-view').forEach(view=>view.classList.toggle('active',view.id===`${button.dataset.view}-view`));document.querySelector('#view-title').textContent=button.textContent;document.querySelector('.admin-sidebar').classList.remove('open');}));
document.querySelector('#mobile-nav').addEventListener('click',()=>document.querySelector('.admin-sidebar').classList.toggle('open'));

loadProjects();loadProfile();
