<template>
  <div class="auth-page">
    <div class="auth-visual">
      <router-link to="/" class="brand light">
        <span class="brand-mark">A</span>
        <span>Anime Atlas</span>
      </router-link>
      <div>
        <p class="hero-kicker">WELCOME BACK</p>
        <h1>继续探索你喜爱的故事。</h1>
        <p>登录后可以评分、评论、收藏作品，并参与聊天与通知互动。</p>
      </div>
    </div>

    <el-card class="auth-card" shadow="never">
      <h2>登录账户</h2>
      <p class="muted">使用用户名和密码登录</p>

      <el-form
        :model="form"
        label-position="top"
        @submit.prevent="submit"
      >
        <el-form-item label="用户名">
          <el-input
            v-model.trim="form.username"
            size="large"
            autocomplete="username"
          />
        </el-form-item>

        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            size="large"
            type="password"
            show-password
            autocomplete="current-password"
          />
        </el-form-item>

        <el-button
          type="primary"
          native-type="submit"
          size="large"
          class="full-button"
          :loading="loading"
          :disabled="loading"
        >
          登录
        </el-button>
      </el-form>

      <p class="auth-switch">
        还没有账户？
        <router-link to="/register">立即注册</router-link>
      </p>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authStore } from '../../stores/auth'

const router = useRouter()
const route = useRoute()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

async function submit() {
  if (loading.value) return

  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  loading.value = true

  try {
    await authStore.login({
      username: form.username,
      password: form.password
    })

    ElMessage.success('登录成功')
    await router.push(route.query.redirect || '/')
  } catch (error) {
    // 错误消息由 request.js 统一显示；这里负责接住异常。
    console.error('登录失败：', error)
  } finally {
    loading.value = false
  }
}
</script>
