# Stage 1: Build Angular App
FROM node:18-alpine AS build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build --configuration=production

# Stage 2: Serve with Nginx
FROM nginx:alpine
COPY --from=build-stage /app/dist/frontend /usr/share/nginx/html
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf
