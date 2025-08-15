# FROM eclipse-temurin:17-jre
FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu
WORKDIR /app
ENV TZ=Asia/Shanghai
COPY target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
