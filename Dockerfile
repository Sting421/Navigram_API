# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src/ /app/src/
RUN mvn package -DskipTests

# Run stage
FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Install curl and netcat for connection checking
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl ca-certificates netcat-openbsd && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Create a script for database connection check
RUN echo '#!/bin/bash \n\
echo "Starting application..." \n\
exec java $JAVA_OPTS -jar app.jar \n\
' > /app/start.sh && chmod +x /app/start.sh

# Environment variables for the application
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

# Set the default Java options
ENV JAVA_OPTS="-XX:+UseContainerSupport -Xmx512m -Xms256m -Djava.security.egd=file:/dev/./urandom"

# Start the application
CMD ["/app/start.sh"]
