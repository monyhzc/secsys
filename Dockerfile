# syntax=docker/dockerfile:1

##
## Build the Vue client
##
FROM node:16-alpine AS client-build
WORKDIR /app/client
COPY client/package*.json ./
RUN npm ci && chmod +x node_modules/.bin/*
COPY client .
RUN npm run build:prod

##
## Build the Spring Boot server
##
FROM maven:3.9.6-eclipse-temurin-8 AS server-build
WORKDIR /app/server
RUN mkdir -p /root/.m2
COPY docker/maven/settings.xml /root/.m2/settings.xml
COPY server/pom.xml .
COPY server/base-common/pom.xml base-common/
COPY server/base-web/pom.xml base-web/
RUN mvn -pl base-web -am dependency:go-offline
COPY server .
RUN mvn -pl base-web -am clean package -DskipTests

##
## Final runtime image with nginx + Java
##
FROM eclipse-temurin:8-jre-jammy
ENV TZ=Asia/Shanghai \
    SPRING_PROFILES_ACTIVE=test
RUN sed -i 's|archive.ubuntu.com|mirrors.aliyun.com|g' /etc/apt/sources.list \
 && sed -i 's|security.ubuntu.com|mirrors.aliyun.com|g' /etc/apt/sources.list \
 && apt-get update \
 && apt-get install -y --no-install-recommends nginx ca-certificates curl bash \
 && rm -f /etc/nginx/sites-enabled/default /etc/nginx/conf.d/default.conf \
 && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=server-build /app/server/base-web/target/*.jar /app/app.jar
COPY --from=client-build /app/client/dist /usr/share/nginx/html
COPY docker/nginx/default.conf /etc/nginx/conf.d/default.conf
COPY docker/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
EXPOSE 80 8099
CMD ["/entrypoint.sh"]
