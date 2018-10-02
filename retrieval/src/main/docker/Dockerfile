FROM openjdk:8-jdk
RUN useradd -ms /bin/bash eager
USER eager
COPY retrieval.jar /home/eager
WORKDIR /home/eager
CMD ["java","-jar","retrieval.jar"]

