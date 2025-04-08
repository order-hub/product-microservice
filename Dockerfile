# Build stage
FROM gradle:8.6.0-jdk21-alpine AS build
WORKDIR /app

COPY build.gradle .
COPY gradlew .
COPY gradle/ gradle/
COPY src/ src/

RUN ./gradlew clean bootJar --no-daemon

# Run stage
FROM eclipse-temurin:21.0.2_13-jre-jammy
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.config.import=optional:file:/.env", "-jar", "app.jar"]