FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy everything and build the project
COPY . /app
RUN chmod +x ./gradlew
RUN ./gradlew build

# Expose the port that Railway expects
EXPOSE $PORT

# Add startup delay to allow PostgreSQL service to be ready, then start with Railway's PORT
CMD ["/bin/bash", "-c", "sleep 30 && java -Dserver.port=$PORT -jar build/libs/kalkulus-backend-0.0.1-SNAPSHOT.jar"]
