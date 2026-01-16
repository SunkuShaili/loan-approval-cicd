FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built JAR (renamed during CI to avoid version issues)
COPY target/app.jar app.jar

EXPOSE 8080

# JVM tuning for EC2 (prevents OOM on small instances)
ENTRYPOINT ["java","-Xms256m","-Xmx512m","-jar","app.jar"]
