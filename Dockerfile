#FROM eclipse-temurin:17-jre-alpine
#RUN mvn clean package
#COPY ./target/UniLabPass-0.0.1-SNAPSHOT.jar app.jar
#ENTRYPOINT ["java", "-jar", "app.jar"]
# Sử dụng image có JDK và Maven để build
FROM maven:3.9.3-eclipse-temurin-17-alpine AS build

# Đặt thư mục làm việc trong container
WORKDIR /app

# Copy toàn bộ source code vào container
COPY . .

# Chạy lệnh Maven để build và tạo file JAR
RUN mvn clean package -DskipTests

# Sử dụng image nhẹ hơn chỉ có JRE để chạy ứng dụng
FROM eclipse-temurin:17-jre-alpine

# Copy file JAR từ giai đoạn build sang giai đoạn chạy
COPY --from=build /app/target/UniLabPass-0.0.1-SNAPSHOT.jar app.jar

# Khai báo entrypoint để chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]