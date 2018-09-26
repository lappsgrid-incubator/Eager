JAR=eager.jar

jar:
	mvn package
	cp target/$(JAR) src/main/docker/

clean:
	mvn clean
	rm src/main/docker/$(JAR)
	
docker:
	if [ ! -e src/main/docker/$JAR ] ; then cp target/$JAR src/main/docker ; done
	cd src/main/docker && docker build -t lappsgrid/eager .
	docker tag lappsgrid/eager docker.lappsgrid.org/lappsgrid/eager

start:
	docker run -d -p 8080:8080 --name eager lappsgrid/eager

stop:
	docker rm -f eager

push:
	docker push docker.lappsgrid.org/lappsgrid/eager