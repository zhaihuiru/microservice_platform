import request from './request'
export const authApi = {
  login: (data) => request.post('/api/auth/login', data),
  register: (data) => request.post('/api/auth/register', data),
  check: () => request.get('/api/auth/check')
}
