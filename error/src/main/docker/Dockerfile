FROM openjdk:8-jdk
RUN useradd -ms /bin/bash eager
USER eager
COPY error-handler.jar /home/eager
WORKDIR /home/eager
CMD ["java","-jar","error-handler.jar"]

