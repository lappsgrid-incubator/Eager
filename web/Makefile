NAME=eager
ORG=lappsgrid
IMAGE=$(ORG)/$(NAME)
TAG=docker.lappsgrid.org/$(IMAGE)
JAR=$(NAME).jar

jar:
	mvn package
	cp target/$(JAR) src/main/docker/

clean:
	mvn clean
	rm src/main/docker/$(JAR)
	
docker:
	if [ ! -e src/main/docker/$(JAR) ] ; then cp target/$(JAR) src/main/docker ; fi
	cd src/main/docker && docker build -t $(IMAGE) .
	docker tag $(IMGAE) $(TAG)

start:
	docker run -d -p 8080:8080 --name $(NAME) $(IMAGE)\

stop:
	docker rm -f $(NAME)

push:
	docker push $(TAG)
