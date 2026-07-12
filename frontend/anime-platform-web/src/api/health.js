import request from './request'
export const healthServices = [
  ['用户服务', '/api/auth/ping'], ['作品服务', '/api/works/ping'], ['角色服务', '/api/characters/ping'],
  ['人物服务', '/api/persons/ping'], ['评分服务', '/api/ratings/ping'], ['评论服务', '/api/comments/ping'],
  ['收藏服务', '/api/favorites/ping'], ['通知服务', '/api/notifications/ping'], ['聊天服务', '/api/chats/ping']
]
export const pingService = (path) => request.get(path)
