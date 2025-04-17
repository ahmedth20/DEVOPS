
FROM openjdk:17-jdk-slim


ENTRYPOINT ["top", "-b"]

WORKDIR /app

ADD target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]