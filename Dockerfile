FROM amazoncorretto:17
COPY target/*.jar checkout-service-0.0.1.jar
EXPOSE 8091
ENTRYPOINT ["java", "-jar", "checkout-service-0.0.1.jar"]
