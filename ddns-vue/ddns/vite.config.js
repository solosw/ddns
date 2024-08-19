import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    host: "localhost",
    cors: true,
    port: 3333,
    open: false, //自动打开
    proxy: {
      // 这里的ccc可乱写, 是拼接在url后面的地址 如果接口中没有统一的后缀可自定义
      // 如果有统一后缀, 如api, 直接写api即可, 也不用rewrite了
      "^/api": {
        target: "http://localhost:9090", // 真实接口地址, 后端给的基地址
        changeOrigin: true, // 允许跨域
        secure: false,  //关键参数，不懂的自己去查
        rewrite: (path) => path.replace(/^\/api/, '/api')
      },
    },
  },
})
