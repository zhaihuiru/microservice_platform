import { computed, reactive } from 'vue'
import { authApi } from '../api/auth'
import { userApi } from '../api/user'

const savedUser = localStorage.getItem('user')
const state = reactive({
  token: localStorage.getItem('token') || '',
  user: savedUser ? JSON.parse(savedUser) : null
})
const isLoggedIn = computed(() => Boolean(state.token))
const isAdmin = computed(() => state.user?.roles?.includes('ADMIN') || state.user?.role === 'ADMIN')

function persist(data) {
  state.token = data.token || state.token
  state.user = { ...(state.user || {}), ...data }
  if (state.token) localStorage.setItem('token', state.token)
  localStorage.setItem('user', JSON.stringify(state.user))
}

async function login(form) { const data = await authApi.login(form); persist(data); return data }
async function loadProfile() { const data = await userApi.me(); persist(data); return data }
function logout() { state.token = ''; state.user = null; localStorage.removeItem('token'); localStorage.removeItem('user') }

export const authStore = { state, isLoggedIn, isAdmin, login, loadProfile, logout, persist }
