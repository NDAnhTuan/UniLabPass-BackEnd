FROM eclipse-temurin:17-jre-alpine
RUN mvn clean package
COPY ./target/UniLabPass-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]