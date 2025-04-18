FROM openjdk:17-jdk-slim
WORKDIR /app
ADD target/*.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]
