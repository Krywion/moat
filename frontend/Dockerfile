# --- etap budowania ---
FROM node:24-alpine AS build
WORKDIR /app
# najpierw manifesty — lepsze cache'owanie warstwy z zależnościami
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

# --- etap uruchomieniowy ---
FROM nginx:1.27-alpine
# wget (z busybox) wystarcza do health-checka kontenera w docker-compose
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
