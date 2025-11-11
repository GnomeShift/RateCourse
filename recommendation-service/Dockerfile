FROM gradle:8.14-jdk21 AS builder
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle :recommendation-service:shadowJar --no-daemon

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /app/recommendation-service/build/libs/*-all.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]