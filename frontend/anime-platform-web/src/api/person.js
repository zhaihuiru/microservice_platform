import request from './request'
export const personApi = {
  page: (params) => request.get('/api/persons', { params }),
  search: (params) => request.get('/api/persons/search', { params }),
  get: (id) => request.get(`/api/persons/${id}`),
  detail: (id) => request.get(`/api/persons/${id}/detail`),
  works: (id) => request.get(`/api/persons/${id}/works`),
  create: (data) => request.post('/api/persons', data),
  update: (id, data) => request.put(`/api/persons/${id}`, data),
  remove: (id) => request.delete(`/api/persons/${id}`),
  byWork: (workId) => request.get(`/api/person-works/work/${workId}`),
  bindWork: (data) => request.post('/api/person-works', data),
  unbindWork: (params) => request.delete('/api/person-works', { params }),
  uploadAvatar: (file) => { const data = new FormData(); data.append('file', file); return request.post('/person/file/upload', data) }
}
