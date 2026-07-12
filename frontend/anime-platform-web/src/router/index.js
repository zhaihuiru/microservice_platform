import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authStore } from '../stores/auth'

const routes = [
  {
    path: '/', component: () => import('../layouts/MainLayout.vue'),
    children: [
      { path: '', name: 'home', component: () => import('../views/Home.vue') },
      { path: 'works', name: 'works', component: () => import('../views/work/WorkList.vue') },
      { path: 'works/:id', name: 'work-detail', component: () => import('../views/work/WorkDetail.vue') },
      { path: 'characters', name: 'characters', component: () => import('../views/character/CharacterList.vue') },
      { path: 'characters/:id', name: 'character-detail', component: () => import('../views/character/CharacterDetail.vue') },
      { path: 'persons', name: 'persons', component: () => import('../views/person/PersonList.vue') },
      { path: 'persons/:id', name: 'person-detail', component: () => import('../views/person/PersonDetail.vue') },
      { path: 'favorites', component: () => import('../views/user/Favorites.vue'), meta: { requiresAuth: true } },
      { path: 'notifications', component: () => import('../views/notification/NotificationList.vue'), meta: { requiresAuth: true } },
      { path: 'chat', component: () => import('../views/chat/ChatHome.vue'), meta: { requiresAuth: true } },
      { path: 'profile', component: () => import('../views/user/Profile.vue'), meta: { requiresAuth: true } }
    ]
  },
  { path: '/login', component: () => import('../views/auth/Login.vue') },
  { path: '/register', component: () => import('../views/auth/Register.vue') },
  {
    path: '/admin', component: () => import('../layouts/AdminLayout.vue'), meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', component: () => import('../views/admin/AdminDashboard.vue') },
      { path: 'users', component: () => import('../views/admin/UserManage.vue') },
      { path: 'content', component: () => import('../views/admin/ContentManage.vue') },
      { path: 'relations', component: () => import('../views/admin/RelationManage.vue') },
      { path: 'comments', component: () => import('../views/admin/CommentAudit.vue') },
      { path: 'notifications', component: () => import('../views/admin/NotificationManage.vue') },
      { path: 'chats', component: () => import('../views/admin/ChatManage.vue') },
      { path: 'health', component: () => import('../views/dev/ServiceHealth.vue') }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({ history: createWebHistory(), routes, scrollBehavior: () => ({ top: 0 }) })
router.beforeEach((to) => {
  if (to.meta.requiresAuth && !authStore.isLoggedIn.value) { ElMessage.warning('请先登录'); return { path: '/login', query: { redirect: to.fullPath } } }
  if (to.meta.requiresAdmin && !authStore.isAdmin.value) { ElMessage.error('仅管理员可以访问'); return '/' }
})
export default router
