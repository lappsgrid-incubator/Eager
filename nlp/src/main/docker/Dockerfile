FROM openjdk:8-jdk
RUN useradd -ms /bin/bash eager
COPY start.sh /home/eager/start.sh
RUN chmod +x /home/eager/start.sh
COPY *.jar /home/eager
USER eager
WORKDIR /home/eager
CMD ["/home/eager/start.sh"]


