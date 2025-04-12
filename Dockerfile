FROM openjdk:17
EXPOSE 8083
ADD target/DEVOPS-main-0.0.1.jar DEVOPS-main-0.0.1.jar
ENTRYPOINT ["java", "-jar", "DEVOPS-main-0.0.1.jar"]
