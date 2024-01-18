# Use a base image with JDK 17
FROM openjdk:17-jdk-slim

# Set the working directory in the Docker image
WORKDIR /opt/app

# Copy the pre-built JAR file into the Docker image
COPY ./build/libs/picspace-0.0.1-SNAPSHOT.jar /opt/app/

# Define the entry point that starts the application
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /opt/app/picspace-0.0.1-SNAPSHOT.jar"]