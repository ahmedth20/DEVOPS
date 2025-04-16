FROM openjdk:17
EXPOSE 8087
ADD target/kaddem-0.0.1.jar kaddem-0.0.1.jar
ENTRYPOINT ["java","-jar","/kaddem-0.0.1.jar"]
