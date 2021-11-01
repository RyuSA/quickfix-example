FROM gradle:7.2-jdk11 as builder
COPY ./ /app
WORKDIR /app
CMD ["gradle", "build"]

FROM openjdk:11-jre-slim
COPY --from=builder /app/build/libs/quickfix-0.0.1.jar /app/quickfix-0.0.1.jar
WORKDIR /app
CMD ["java", "-jar", "./quickfix-0.0.1.jar"]
