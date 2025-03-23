
FROM openjdk:17-jdk-slim

LABEL authors="yosry"

ENTRYPOINT ["top", "-b"]

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]