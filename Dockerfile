#FROM openjdk:17-jdk-slim
#RUN mkdir /opt/apt
#COPY target/employees-0.0.1-SNAPSHOT.jar /opt/apt/employees.jar
#CMD ["java", "-jar", "/opt/apt/employees.jar"]

FROM openjdk:17-jdk-slim as builder
WORKDIR application
COPY target/employees-0.0.1-SNAPSHOT.jar employees.jar
RUN java -Djarmode=tools -jar employees.jar extract  --layers --launcher


FROM openjdk:17-jdk-slim
WORKDIR application
ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh .
RUN chmod +x ./wait-for-it.sh
COPY --from=builder application/employees/dependencies/ ./
COPY --from=builder application/employees/spring-boot-loader/ ./
COPY --from=builder application/employees/snapshot-dependencies/ ./
COPY --from=builder application/employees/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]