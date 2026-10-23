FROM node:22-alpine AS web-build
WORKDIR /web
COPY apps/web/package.json apps/web/package-lock.json ./
RUN npm ci
COPY apps/web .
RUN npm run build

FROM caddy:2-alpine
COPY ops/Caddyfile /etc/caddy/Caddyfile
COPY --from=web-build /web/dist/snapsends3-web/browser /srv
