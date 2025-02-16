FROM openjdk:17.0.1-jdk-slim
WORKDIR /app
COPY ./target/UniLabPass-0.0.1-SNAPSHOT.jar /app
EXPOSE 8080

CMD  ["java","-jar","/UniLabPass-0.0.1-SNAPSHOT.jar"]
