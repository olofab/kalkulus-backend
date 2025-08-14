FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy everything and build the project
COPY . /app
RUN chmod +x ./gradlew
RUN ./gradlew build

# Railway doesn't need wait-for-it as it handles service dependencies
# Railway also automatically provides DATABASE_URL when PostgreSQL is connected
CMD ["java", "-jar", "-Dspring.profiles.active=railway", "build/libs/kalkulus-backend-0.0.1-SNAPSHOT.jar"]
