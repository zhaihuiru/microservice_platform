import request from './request'
export const notificationApi = {
  mine: (params) => request.get('/api/notifications/my', { params }),
  unreadCount: () => request.get('/api/notifications/my/unread-count'),
  read: (id) => request.put(`/api/notifications/${id}/read`),
  readAll: () => request.put('/api/notifications/my/read-all'),
  remove: (id) => request.delete(`/api/notifications/${id}`),
  adminSend: (receiverId, data) => request.post(`/api/notifications/admin/users/${receiverId}`, { ...data, receiverId }),
  broadcast: (data) => request.post('/api/notifications/admin/broadcast', data),
  adminRemoveMessage: (id) => request.delete(`/api/notifications/admin/messages/${id}`),
  adminRemoveUserMessage: (id) => request.delete(`/api/notifications/admin/user-messages/${id}`)
}
