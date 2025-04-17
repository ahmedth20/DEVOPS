# Stage 1: Build Angular App
#FROM node:18-alpine AS build-stage
#WORKDIR /app
#COPY package*.json ./
#RUN npm install
#COPY . .
#RUN npm run build --configuration=production

# Stage 2: Serve with Nginx
#FROM nginx:alpine
#COPY --from=build-stage /app/dist/frontend /usr/share/nginx/html
#COPY nginx.conf /etc/nginx/conf.d/default.conf
#EXPOSE 89

FROM node:18-alpine

WORKDIR /project

RUN npm install -g @angular/cli@16

COPY package.json package-lock.json ./
RUN npm i

COPY . .

EXPOSE 4200
CMD ["npm", "start"]