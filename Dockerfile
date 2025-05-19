# Build stage
FROM gradle:8.6.0-jdk21 AS build
WORKDIR /app

COPY build.gradle .
COPY gradlew .
COPY gradle/ gradle/
COPY src/ src/

RUN ./gradlew clean bootJar --no-daemon

# Run stage
FROM eclipse-temurin:21.0.2_13-jre-jammy
WORKDIR /app

# 애플리케이션 JAR 복사
COPY --from=build /app/build/libs/*.jar app.jar

# .env 파일 복사 (루트 경로로)
COPY .env /.env

EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.config.import=optional:file:/.env", "-jar", "app.jar"]
