import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// In dev, forward API calls to the two backend services so the browser sees one origin.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api/auth': 'http://localhost:8081',
      '/api/users': 'http://localhost:8082',
    },
  },
});
