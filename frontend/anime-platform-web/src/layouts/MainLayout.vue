<template>
  <div class="app-shell">
    <header class="topbar">
      <router-link to="/" class="brand"><span class="brand-mark">A</span><span>Anime Atlas</span></router-link>
      <nav class="main-nav">
        <router-link to="/works">作品</router-link><router-link to="/characters">角色</router-link><router-link to="/persons">人物</router-link>
        <router-link v-if="authStore.isLoggedIn.value" to="/chat">聊天</router-link>
      </nav>
      <div class="nav-actions">
        <router-link v-if="authStore.isLoggedIn.value" to="/notifications"><el-badge :value="unread" :hidden="!unread"><el-button text>通知</el-button></el-badge></router-link>
        <router-link v-if="authStore.isAdmin.value" to="/admin"><el-button>管理后台</el-button></router-link>
        <template v-if="authStore.isLoggedIn.value">
          <el-dropdown><button class="user-chip"><el-avatar :size="30" :src="authStore.state.user?.avatarUrl">{{ initial }}</el-avatar><span>{{ authStore.state.user?.nickname || authStore.state.user?.username }}</span></button>
            <template #dropdown><el-dropdown-menu><el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item><el-dropdown-item @click="$router.push('/favorites')">我的收藏</el-dropdown-item><el-dropdown-item divided @click="logout">退出登录</el-dropdown-item></el-dropdown-menu></template>
          </el-dropdown>
        </template>
        <template v-else><router-link to="/login"><el-button>登录</el-button></router-link><router-link to="/register"><el-button type="primary">注册</el-button></router-link></template>
      </div>
    </header>
    <main class="main-content"><router-view /></main>
    <footer class="footer">Anime Atlas · 微服务动漫作品信息与互动平台</footer>
  </div>
</template>
<script setup>
import { computed, onMounted, ref } from 'vue'; import { useRouter } from 'vue-router'; import { authStore } from '../stores/auth'; import { notificationApi } from '../api/notification'
const router=useRouter(); const unread=ref(0); const initial=computed(()=>(authStore.state.user?.nickname||authStore.state.user?.username||'U').slice(0,1).toUpperCase())
async function loadUnread(){ if(!authStore.isLoggedIn.value) return; try{unread.value=await notificationApi.unreadCount()}catch{unread.value=0} }
function logout(){authStore.logout();router.push('/login')}
onMounted(async()=>{ if(authStore.isLoggedIn.value){ try{await authStore.loadProfile()}catch{}; loadUnread() } })
</script>
