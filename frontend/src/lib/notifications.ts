// Notifications système de l'appareil (API Notification du navigateur). Rien d'obligatoire :
// sans support ou sans autorisation, les rappels restent visibles dans le bandeau.

export type NotificationState = 'unsupported' | 'default' | 'granted' | 'denied'

export function notificationState(): NotificationState {
  return 'Notification' in window ? Notification.permission : 'unsupported'
}

/** Demande l'autorisation ; doit être appelée depuis un geste de l'utilisateur (clic). */
export async function askPermission(): Promise<NotificationState> {
  return 'Notification' in window ? Notification.requestPermission() : 'unsupported'
}

/**
 * Affiche une notification. Passe par le service worker quand il existe (obligatoire sur mobile),
 * sinon par le constructeur Notification (navigateur de bureau, mode dev).
 */
export async function notify(title: string, body: string, tag: string): Promise<void> {
  const options = { body, tag, icon: '/pwa-192x192.png' }
  const registration = await navigator.serviceWorker?.getRegistration()
  if (registration) {
    await registration.showNotification(title, options)
  } else {
    new Notification(title, options)
  }
}
