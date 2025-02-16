FROM eclipse-temurin:17-jre-alpine
COPY ./UniLabPass-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]