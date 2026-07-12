<template>
  <div class="auth-page">
    <div class="auth-visual register-visual">
      <router-link to="/" class="brand light">
        <span class="brand-mark">A</span>
        <span>Anime Atlas</span>
      </router-link>
      <div>
        <p class="hero-kicker">JOIN THE COMMUNITY</p>
        <h1>创建属于你的动漫档案。</h1>
        <p>收藏作品、留下评分与评论，和其他用户一起交流。</p>
      </div>
    </div>

    <el-card class="auth-card" shadow="never">
      <h2>注册账户</h2>

      <el-form
        :model="form"
        label-position="top"
        @submit.prevent="submit"
      >
        <el-form-item label="用户名">
          <el-input v-model.trim="form.username" size="large" />
        </el-form-item>

        <el-form-item label="昵称">
          <el-input v-model.trim="form.nickname" size="large" />
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input v-model.trim="form.email" size="large" />
        </el-form-item>

        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            size="large"
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
          注册
        </el-button>
      </el-form>

      <p class="auth-switch">
        已有账户？
        <router-link to="/login">返回登录</router-link>
      </p>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '../../api/auth'

const router = useRouter()
const loading = ref(false)

const form = reactive({
  username: '',
  nickname: '',
  email: '',
  password: ''
})

async function submit() {
  if (loading.value) return

  if (!form.username || !form.password || !form.email) {
    ElMessage.warning('请完整填写用户名、邮箱和密码')
    return
  }

  loading.value = true

  try {
    await authApi.register({
      username: form.username,
      nickname: form.nickname,
      email: form.email,
      password: form.password
    })

    ElMessage.success('注册成功，请登录')
    await router.push('/login')
  } catch (error) {
    // 错误消息由 request.js 统一显示；这里负责接住异常，避免未处理 Promise。
    console.error('注册失败：', error)
  } finally {
    loading.value = false
  }
}
</script>
