FROM amazoncorretto:17-alpine
EXPOSE 8080
ENV LOG4J_FORMAT_MSG_NO_LOOKUPS=true
ARG JAR_FILE=build/libs/quiz-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
CMD ["java","-Dserver.port=8080","-jar","app.jar"]

