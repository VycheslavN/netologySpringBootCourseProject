FROM adoptopenjdk/openjdk11:alpine-jre

EXPOSE 5500

ADD target/netologySpringBootCourseProject-0.0.1-SNAPSHOT.jar moneyapp.jar

CMD ["java", "-jar" ,"moneyapp.jar"]