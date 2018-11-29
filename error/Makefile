VERSION=$(shell cat VERSION)

JAR=error-handler.jar
GROUP=mining
NAME=error-handler
IMAGE=$(GROUP)/$(NAME)
FULL=docker.lappsgrid.org/$(IMAGE)
TAG=$(FULL):$(VERSION)	

jar:
	mvn package
	cp target/$(JAR) src/main/docker/

clean:
	mvn clean
	if [ -e src/main/docker/$(JAR) ] ; then rm src/main/docker/$(JAR) ; fi
	
run:
	java -jar target/$(JAR)

docker:
	if [ ! -e src/main/docker/$(JAR) ] ; then cp target/$(JAR) src/main/docker ; fi
	cd src/main/docker && docker build -t $(IMAGE) .
	docker tag $(IMAGE) $(FULL)

start:
	docker run -d --name error $(IMAGE)

stop:
	docker rm -f error

push:
	docker push $(FULL)

tag:
	docker tag $(TAG) $(TAG):$(VERSION)
	docker push $(TAG):$(VERSION)