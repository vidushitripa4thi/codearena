import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        configure: (proxy) => {
          proxy.on('error', (_err, _req, res) => {
            // @ts-ignore
            if (!res.headersSent && typeof res.writeHead === 'function') {
              // @ts-ignore
              res.writeHead(503, { 'Content-Type': 'application/json' });
              res.end(
                JSON.stringify({
                  title: 'Service Unavailable',
                  status: 503,
                  detail:
                    'Cannot connect to the CodeArena backend on port 8080. Please ensure Spring Boot is running with DB_PASSWORD and JWT_SECRET.',
                })
              );
            }
          });
        },
      },
    },
  },
  // @ts-ignore
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/test/setup.ts',
    css: false,
  },
});

