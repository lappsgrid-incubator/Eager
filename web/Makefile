
jar:
	mvn package
	cp target/eager.jar src/main/docker/

clean:
	mvn clean

docker:
	cd src/main/docker && docker build -t lappsgrid/eager .

