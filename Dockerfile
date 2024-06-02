FROM openjdk:17
EXPOSE 8082
ADD target/springapi-image.jar springapi-image.jar
ENTRYPOINT ["java", "-jar", "/springapi-image.jar"]