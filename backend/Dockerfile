FROM ubuntu:latest AS build
RUN apt-get update
RUN apt-get install -y software-properties-common
RUN add-apt-repository ppa:openjdk-r/ppa
RUN apt-get update
RUN apt-get install -y openjdk-23-jdk maven
COPY . .
RUN mvn clean package

FROM openjdk:23-jdk-oracle
EXPOSE 8080
COPY --from=build /target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]