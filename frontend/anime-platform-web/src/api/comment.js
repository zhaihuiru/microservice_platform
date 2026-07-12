import request from './request'
export const commentApi = {
  byWork: (workId) => request.get(`/api/comments/work/${workId}`),
  create: (workId, content) => request.post('/api/comments', { workId, content }),
  reply: (parentId, workId, content) => request.post(`/api/comments/${parentId}/reply`, { workId, content }),
  like: (id) => request.put(`/api/comments/${id}/like`),
  update: (id, content) => request.put(`/api/comments/${id}`, { content }),
  remove: (id) => request.delete(`/api/comments/${id}`),
  pending: () => request.get('/api/comments/admin/pending'),
  setStatus: (id, status) => request.put(`/api/comments/admin/${id}/status`, { status }),
  sticky: (id, sticky) => request.put(`/api/comments/admin/${id}/sticky`, null, { params: { sticky } }),
  essence: (id, essence) => request.put(`/api/comments/admin/${id}/essence`, null, { params: { essence } }),
  adminRemove: (id) => request.delete(`/api/comments/admin/${id}`)
}
