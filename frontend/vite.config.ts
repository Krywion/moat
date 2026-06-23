import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    proxy: {
      '/auth/login': 'http://localhost:8080',
      '/auth/register': 'http://localhost:8080',
      '/auth/logout': 'http://localhost:8080',
      '/auth/me': 'http://localhost:8080',
      '/companies': 'http://localhost:8080',
    },
  },
})
