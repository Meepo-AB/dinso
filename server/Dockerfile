FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
COPY gradlew gradlew
COPY gradle gradle
COPY settings.gradle build.gradle ./
COPY service service
COPY database database
COPY api api
COPY springconfig springconfig
COPY customers customers
ARG CUSTOMER
RUN chmod +x gradlew
RUN ./gradlew :customers:${CUSTOMER}:bootJar --no-daemon

FROM eclipse-temurin:25-jre
WORKDIR /app
ARG CUSTOMER
COPY --from=build /workspace/customers/${CUSTOMER}/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
