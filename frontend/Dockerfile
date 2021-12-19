################################################################################
## Builder
################################################################################
FROM maven:3.8.4-openjdk-17-slim as builder

RUN mkdir -p /workspace

WORKDIR /workspace

COPY pom.xml /workspace

COPY src /workspace/src

RUN mvn -f pom.xml clean package



################################################################################
## Runtime
################################################################################
FROM openjdk:17-alpine as runtime

COPY --from=builder /workspace/target/*.jar app.jar

EXPOSE 9000

ENTRYPOINT ["java","-jar","/app.jar"]