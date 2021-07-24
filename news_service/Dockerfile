FROM openjdk:14-jdk-buster
ARG JAR_FILE=build/libs/news_service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} news_service.jar
ENTRYPOINT ["java", "-jar", "/news_service.jar"]