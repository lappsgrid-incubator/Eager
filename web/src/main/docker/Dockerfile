FROM openjdk:8-jdk
RUN useradd -ms /bin/bash eager
USER eager
COPY eager.jar /home/eager
WORKDIR /home/eager
CMD ["java","-Xmx8G","-jar","eager.jar"]
