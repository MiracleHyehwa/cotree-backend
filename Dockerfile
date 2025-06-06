FROM openjdk:21

RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && echo Asia/Seoul > /etc/timezone

ARG JAR_FILE=build/libs/Cotree-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} ourClass.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=dev", "-Duser.timezone=Asia/Seoul", "/ourClass.jar"]
