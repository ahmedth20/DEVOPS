FROM openjdk:17-jdk-slim
ENTRYPOINT ["top", "-b"]
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8089
CMD ["java", "-jar", "app.jar"]