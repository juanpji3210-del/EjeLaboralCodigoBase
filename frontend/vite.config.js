import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// AD-6: en desarrollo, /api se proxy al backend Spring Boot (8080) → sin CORS en dev
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
