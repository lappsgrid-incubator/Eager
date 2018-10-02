JAR=retrieval.jar
NAME=retrieval
GROUP=mining
IMAGE=$(GROUP)/$(NAME)
TAG=docker.lappsgrid.org/$(IMAGE)

jar:
	mvn package
	cp target/$(JAR) src/main/docker/

clean:
	mvn clean
	if [ -e src/main/docker/$(JAR) ] ; then rm src/main/docker/$(JAR) ; fi
	
docker:
	if [ ! -e src/main/docker/$JAR ] ; then cp target/$(JAR) src/main/docker ; fi
	cd src/main/docker && docker build -t $(IMAGE) .
	docker tag $(IMAGE) $(TAG)

start:
	docker run -d -p 8080:8080 --name $(NAME) $(IMAGE)

stop:
	docker rm -f $(NAME)

push:
	docker push $(TAG)
