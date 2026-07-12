import request from './request'
export const favoriteApi = {
  createFolder: (data) => request.post('/api/favorites/folders', data),
  folders: () => request.get('/api/favorites/folders/me'),
  publicFolders: (userId) => request.get(`/api/favorites/folders/user/${userId}`),
  removeFolder: (folderId) => request.delete(`/api/favorites/folders/${folderId}`),
  add: (folderId, workId) => request.post('/api/favorites', { folderId, workId }),
  remove: (folderId, workId) => request.delete('/api/favorites', { data: { folderId, workId } }),
  items: (folderId) => request.get(`/api/favorites/folder/${folderId}/items`),
  adminRemoveFolder: (folderId) => request.delete(`/api/favorites/admin/folders/${folderId}`)
}
