FROM openjdk:8-jdk
RUN useradd -ms /bin/bash eager
USER eager
COPY *.jar /home/eager
WORKDIR /home/eager
CMD ["java","-jar", "stanford.jar"]

