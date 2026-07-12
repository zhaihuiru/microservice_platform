import request from './request'
export const ratingApi = {
  submit: (workId, score) => request.post('/api/ratings', { workId, score }),
  average: (workId) => request.get(`/api/ratings/avg/${workId}`),
  mine: (workId) => request.get(`/api/ratings/work/${workId}`, { silent: true }),
  removeMine: (workId) => request.delete(`/api/ratings/${workId}`),
  vote: (topicId, targetId) => request.post('/api/votes', { topicId, targetId }),
  voteResult: (topicId) => request.get(`/api/votes/topic/${topicId}/result`),
  adminRemove: (id) => request.delete(`/api/ratings/admin/${id}`)
}
