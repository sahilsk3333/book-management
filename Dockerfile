# Stage 1: Build the application
FROM gradle:7.6.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN ./gradlew build

# Stage 2: Run the application
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/book-management-0.0.1-SNAPSHOT.jar /app/book-management.jar
CMD ["java", "-jar", "/app/book-management.jar"]
