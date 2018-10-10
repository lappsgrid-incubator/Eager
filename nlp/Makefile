JAR=stanford.jar
REPO=docker.lappsgrid.org
GROUP=mining
NAME=nlp
IMAGE=$(GROUP)/$(NAME)
TAG=$(REPO)/$(IMAGE)
VERSION=$(shell cat VERSION)

jar:
	mvn package
	cp target/$(JAR) src/main/docker/

clean:
	mvn clean
	if [ -e src/main/docker/$(JAR) ] ; then rm src/main/docker/$(JAR) ; fi

docker:
	if [ ! -e src/main/docker/$(JAR) ] ; then cp target/$(JAR) src/main/docker ; fi
	cd src/main/docker && docker build --build-arg "JAR=$(JAR)" -t $(IMAGE) .
	docker tag $(IMAGE) $(TAG)

start:
	docker run -d --name $(NAME) $(IMAGE)

stop:
	docker rm -f $(NAME)

push:
	docker push $(TAG)

tag:
	docker tag $(IMAGE) $(TAG):$(VERSION)
	docker push $(TAG):$(VERSION)

