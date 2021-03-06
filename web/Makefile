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
	if [ ! -e src/main/docker/$(JAR) ] ; then rm src/main/docker/$(JAR) ; fi
	
docker:
	if [ ! -e src/main/docker/$(JAR) ] || [ target/$(JAR) -nt src/main/docker/$(JAR) ] ; then echo "Copying JAR file" ; cp target/$(JAR) src/main/docker ; fi
	cd src/main/docker && docker build -t $(IMAGE) .
	docker tag $(IMAGE) $(TAG)

style:
	cd src/main/resources/static/css && lessc main.less main.css

run:
	java -Xmx8G -jar target/$(JAR)

dev:
	java -Xmx8G -Dspring.profiles.active=dev -jar target/$(JAR)

start:
	docker run -d -p 8080:8080 --name $(NAME) $(IMAGE)

stop:
	docker rm -f $(NAME)

tag:
	docker tag $(IMAGE) $(TAG)

push:
	docker push $(TAG)

upload:
	scp -i ~/.ssh/lappsgrid-shared-key.pem target/eager.jar root@149.165.156.190:/root
