FROM openjdk:17-jdk-alpine

# Copia el archivo JAR generado de tu proyecto a la imagen
#COPY /root/reserve_spaces/backend_spaces.jar /app/
COPY /backend_spaces.jar /app/

# Define el directorio de trabajo dentro de la imagen
WORKDIR /app

# Expone el puerto en el que tu aplicación Spring Boot escucha
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "backend_spaces.jar"]
