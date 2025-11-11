FROM gradle:8.15-jdk21 AS builder
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle :rating-service:bootJar --no-daemon

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /app/rating-service/build/libs/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]