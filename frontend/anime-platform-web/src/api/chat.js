import request from './request'
export const chatApi = {
  mine: () => request.get('/api/chats/conversations/my'),
  createPrivate: (peerUserId) => request.post('/api/chats/conversations/private', { peerUserId }),
  createGroup: (title, memberIds) => request.post('/api/chats/conversations/group', { title, memberIds }),
  createWork: (workId, title) => request.post('/api/chats/conversations/work', { workId, title }),
  addMembers: (id, userIds) => request.post(`/api/chats/conversations/${id}/members`, { userIds }),
  join: (id) => request.post(`/api/chats/conversations/${id}/join`),
  leave: (id) => request.post(`/api/chats/conversations/${id}/leave`),
  messages: (id, params) => request.get(`/api/chats/conversations/${id}/messages`, { params }),
  send: (id, content) => request.post(`/api/chats/conversations/${id}/messages`, {
    clientMessageId: crypto.randomUUID?.() || `${Date.now()}`,
    messageType: 'TEXT', content, mediaUrl: null, replyToMessageId: null, mentionUserIds: []
  }),
  read: (id) => request.put(`/api/chats/conversations/${id}/read`),
  removeMessage: (id) => request.delete(`/api/chats/messages/${id}`),
  adminConversations: (params) => request.get('/api/chats/admin/conversations', { params }),
  adminMessages: (id, params) => request.get(`/api/chats/admin/conversations/${id}/messages`, { params }),
  adminRemoveMessage: (id, reason) => request.delete(`/api/chats/admin/messages/${id}`, { data: { reason } }),
  mute: (conversationId, userId, reason) => request.put(`/api/chats/admin/conversations/${conversationId}/mute/${userId}`, { reason }),
  unmute: (conversationId, userId, reason) => request.put(`/api/chats/admin/conversations/${conversationId}/unmute/${userId}`, { reason }),
  close: (conversationId, reason) => request.put(`/api/chats/admin/conversations/${conversationId}/close`, { reason }),
  disband: (conversationId, reason) => request.put(`/api/chats/admin/conversations/${conversationId}/disband`, { reason }),
  logs: (params) => request.get('/api/chats/admin/logs', { params })
}
