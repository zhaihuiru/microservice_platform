import request from './request'
export const characterApi = {
  page: (params) => request.get('/api/characters', { params }),
  search: (params) => request.get('/api/characters/search', { params }),
  get: (id) => request.get(`/api/characters/${id}`),
  detail: (id) => request.get(`/api/characters/${id}/detail`),
  works: (id) => request.get(`/api/characters/${id}/works`),
  create: (data) => request.post('/api/characters', data),
  update: (id, data) => request.put(`/api/characters/${id}`, data),
  remove: (id) => request.delete(`/api/characters/${id}`),
  relations: (characterId) => request.get(`/api/character-relations/character/${characterId}`),
  createRelation: (data) => request.post('/api/character-relations', data),
  updateRelation: (id, data) => request.put(`/api/character-relations/${id}`, data),
  removeRelation: (id) => request.delete(`/api/character-relations/${id}`),
  byWork: (workId) => request.get(`/api/work-character-actors/work/${workId}`),
  bindActor: (data) => request.post('/api/work-character-actors', data),
  unbindActor: (params) => request.delete('/api/work-character-actors', { params }),
  uploadAvatar: (file) => { const data = new FormData(); data.append('file', file); return request.post('/character/file/upload', data) }
}
