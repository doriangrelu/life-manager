// Mises à jour de l'application installée (PWA).
// Le service worker garde une copie du JS et du CSS : sans ce module, une nouvelle version n'apparaîtrait
// qu'après deux rechargements. Ici, la présence d'une nouvelle version est vérifiée à l'ouverture, au retour
// sur l'application et toutes les heures ; dès qu'elle est installée, la page se recharge d'elle-même.
import { registerSW } from 'virtual:pwa-register'

const HOURLY_MS = 60 * 60 * 1000

export function keepUpToDate(): void {
  registerSW({
    immediate: true,
    onRegisteredSW(_url, registration) {
      if (!registration) {
        return
      }
      const check = () => {
        if (navigator.onLine) {
          registration.update().catch(() => undefined)
        }
      }
      setInterval(check, HOURLY_MS)
      document.addEventListener('visibilitychange', () => document.visibilityState === 'visible' && check())
    },
  })
}
