import { svelte } from '@sveltejs/vite-plugin-svelte'
import { defineConfig } from 'vite'
import { VitePWA } from 'vite-plugin-pwa'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    svelte(),
    VitePWA({
      // Nouvelle version installée et activée sans attendre ; la page se recharge seule (voir lib/updates.ts).
      registerType: 'autoUpdate',
      injectRegister: false,
      // Icônes (favicon, PWA, Apple) générées à partir d'un seul logo.
      pwaAssets: { image: 'public/logo.svg', preset: 'minimal-2023' },
      manifest: {
        name: 'Life Manager',
        short_name: 'Life Manager',
        description: 'Gérer le quotidien du foyer',
        lang: 'fr',
        theme_color: '#2f6f5e',
        background_color: '#f4efe6',
        display: 'standalone',
      },
      workbox: {
        // Les appels d'API ne passent jamais par le cache hors-ligne.
        navigateFallbackDenylist: [/^\/api\//],
        // Les copies des anciennes versions sont supprimées dès qu'une nouvelle prend la main.
        cleanupOutdatedCaches: true,
        clientsClaim: true,
        skipWaiting: true,
      },
    }),
  ],
  server: {
    port: 5173,
    strictPort: true,
    // En dev, l'API Micronaut tourne à côté sur le port 8080.
    proxy: { '/api': 'http://localhost:8080' },
  },
})
