FROM amazoncorretto:17
COPY target/*.jar checkout-service-0.0.1.jar
EXPOSE 8091
ENTRYPOINT ["java", "-Dspring.profiles.active=dev","-jar", "checkout-service-0.0.1.jar"]
