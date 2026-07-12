import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],

  server: {
    host: '0.0.0.0',
    port: 5173,

    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },

      // 上传接口必须写得具体，避免与前端 /works 路由冲突
      '/work/file': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },

      '/character/file': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },

      '/person/file': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },

      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },

      '/ws': {
        target: 'ws://localhost:8080',
        ws: true,
        changeOrigin: true
      }
    }
  }
})