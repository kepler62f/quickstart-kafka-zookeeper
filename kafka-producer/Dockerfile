########build stage########
FROM maven:3.8.1-jdk-11 as maven_build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN --mount=type=cache,target=/root/.m2 mvn clean package  -Dmaven.test.skip

# https://nieldw.medium.com/caching-maven-dependencies-in-a-docker-build-dca6ca7ad612
#RUN mvn dependency:go-offline
#RUN mvn package

########run stage########
FROM openjdk:11-jdk
WORKDIR /app

COPY --from=maven_build /app/target/*.jar /app/

#run the app
ENV JAVA_OPTS ""
CMD [ "bash", "-c", "java ${JAVA_OPTS} -jar *.jar -v"]