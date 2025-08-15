FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu
WORKDIR /app
ENV TZ=Asia/Shanghai
COPY target/*.jar /app/app.jar
EXPOSE 9200
ENTRYPOINT ["java","-jar","/app/app.jar"]
