JAR=stanford.jar
GROUP=mining
NAME=stanford
IMAGE=$(GROUP)/$(IMAGE)

jar:
	mvn package
	cp target/$(JAR) src/main/docker/

clean:
	mvn clean
	if [ -e src/main/docker/$(JAR) ] ; then rm src/main/docker/$(JAR) ; fi

docker:
	if [ ! -e src/main/docker/$JAR ] ; then cp target/$(JAR) src/main/docker ; fi
	cd src/main/docker && docker build -t (IMAGE) .
	docker tag $(IMAGE) docker.lappsgrid.org/$(IMAGE)

start:
	docker run -d -p 8080:8080 --name $(NAME) $(IMAGE)

stop:
	docker rm -f $(NAME)

push:
	docker push docker.lappsgrid.org/$(IMAGE)
