
run:
	docker run -d -p 5672:5672 -p 15672:15672 --hostname rabbitmq --name rabbitmq -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=password rabbitmq:3-management
	
stop:
	docker stop rabbitmq

start:
	docker start rabbitmq

delete:
	docker rm -f rabbitmq

