FROM openjdk:8-jdk
RUN useradd -ms /bin/bash eager
COPY start.sh /home/eager
RUN chmod +x /home/eager/start.sh
RUN mkdir -p /var/log/eager && chown eager:eager /var/log/eager
USER eager
COPY error-handler.jar /home/eager
WORKDIR /home/eager
CMD ["./start.sh"]

