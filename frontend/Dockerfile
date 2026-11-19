# Image de production de la PWA : fichiers statiques servis par nginx (utilisateur non root, port 8080).
# L'adresse de Keycloak est figée au build :
#   docker build --build-arg VITE_OIDC_AUTHORITY=https://iabilis.fr/realms/life-manager -t life-manager-web frontend/

FROM oven/bun:1.3.14 AS build
WORKDIR /build
COPY package.json bun.lock ./
RUN bun install --frozen-lockfile
COPY . .
ARG VITE_OIDC_AUTHORITY
ARG VITE_OIDC_CLIENT_ID=life-manager-pwa
# Version affichée dans le pied de page (tag Git, fourni par release.yml).
ARG VITE_APP_VERSION=dev
RUN test -n "$VITE_OIDC_AUTHORITY" || (echo "VITE_OIDC_AUTHORITY est obligatoire" && exit 1)
RUN bun run build

FROM nginxinc/nginx-unprivileged:1.28-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /build/dist /usr/share/nginx/html
EXPOSE 8080
