import request from './request'
export const userApi = {
  me: () => request.get('/api/users/me'),
  updateMe: (data) => request.put('/api/users/me', data),
  changePassword: (data) => request.put('/api/users/me/password', data),
  getById: (id) => request.get(`/api/users/${id}`),
  status: (id) => request.get(`/api/users/${id}/status`),
  batch: (ids) => request.get('/api/users/batch', { params: { ids: ids.join(',') } }),
  adminPage: (params) => request.get('/api/admin/users', { params }),
  adminDetail: (id) => request.get(`/api/admin/users/${id}`),
  ban: (id, reason) => request.put(`/api/admin/users/${id}/ban`, { reason }),
  unban: (id) => request.put(`/api/admin/users/${id}/unban`),
  resetPassword: (id, newPassword) => request.put(`/api/admin/users/${id}/reset-password`, { newPassword }),
  assignRoles: (id, roles) => request.put(`/api/admin/users/${id}/roles`, { roles }),
  logs: (id) => request.get(`/api/admin/users/${id}/logs`)
}
