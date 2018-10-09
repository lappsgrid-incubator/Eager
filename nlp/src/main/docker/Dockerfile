FROM openjdk:8-jdk
ARG JAR=stanford.jar
RUN useradd -ms /bin/bash eager
USER eager
COPY $JAR /home/eager
WORKDIR /home/eager
CMD ["java","-jar", "$JAR"]

