FROM gradle:8.14-jdk21 AS builder
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:21
WORKDIR /app
COPY --from=builder /app/build/libs/*-all.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]