FROM node:25-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
ARG CUSTOMER
ARG VITE_USE_API=true
ENV VITE_USE_API=$VITE_USE_API
RUN npm run build -- --mode $CUSTOMER

FROM nginx:1.29-alpine
COPY nginx.default.conf.template /etc/nginx/templates/default.conf.template
ARG CUSTOMER
COPY --from=build /app/dist/${CUSTOMER}/ /usr/share/nginx/html/
EXPOSE 80
