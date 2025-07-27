FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy everything and build the project
COPY . /app
RUN chmod +x ./gradlew
RUN ./gradlew build

# Copy and prepare wait-for-it
COPY wait-for-it.sh wait-for-it.sh
RUN chmod +x wait-for-it.sh

# Use wait-for-it to wait for the database before starting the app
CMD ["./wait-for-it.sh", "postgres:5432", "--", "java", "-jar", "build/libs/kalkulus-backend-0.0.1-SNAPSHOT.jar"]
