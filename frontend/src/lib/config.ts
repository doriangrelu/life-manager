// Réglages de l'application, surchargeables par variables d'environnement Vite (voir .env.example).
export const config = {
  oidcAuthority: import.meta.env.VITE_OIDC_AUTHORITY ?? 'http://localhost:8180/realms/life-manager',
  oidcClientId: import.meta.env.VITE_OIDC_CLIENT_ID ?? 'life-manager-pwa',
}
