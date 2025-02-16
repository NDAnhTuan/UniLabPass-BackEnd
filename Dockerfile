FROM openjdk:17.0.1-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} UniLabPass-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/UniLabPass-0.0.1-SNAPSHOT.jar"]
