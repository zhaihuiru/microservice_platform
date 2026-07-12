import request from './request'
export const workApi = {
  page: (params) => request.get('/api/works', { params }),
  search: (params) => request.get('/api/works/search', { params }),
  get: (id) => request.get(`/api/works/${id}`),
  detail: (id) => request.get(`/api/works/${id}/detail`),
  create: (data) => request.post('/api/works', data),
  update: (id, data) => request.put(`/api/works/${id}`, data),
  remove: (id) => request.delete(`/api/works/${id}`),
  categories: () => request.get('/api/categories/all'),
  category: (id) => request.get(`/api/categories/${id}`),
  categoryWorks: (id, params) => request.get(`/api/categories/${id}/works`, { params }),
  createCategory: (data) => request.post('/api/categories', data),
  updateCategory: (id, data) => request.put(`/api/categories/${id}`, data),
  removeCategory: (id) => request.delete(`/api/categories/${id}`),
  listRelations: (params) => request.get('/api/work-categories', { params }),
  bindCategory: (data) => request.post('/api/work-categories', data),
  unbindCategory: (workId, categoryId) => request.delete('/api/work-categories', { params: { workId, categoryId } }),
  uploadCover: (file) => {
    const data = new FormData(); data.append('file', file)
    return request.post('/work/file/upload', data)
  }
}
