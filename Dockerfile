FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy everything and build the project
COPY . /app
RUN chmod +x ./gradlew
RUN ./gradlew build

# Add startup delay to allow PostgreSQL service to be ready
CMD ["/bin/bash", "-c", "sleep 10 && java -jar -Dspring.profiles.active=railway build/libs/kalkulus-backend-0.0.1-SNAPSHOT.jar"]
