build:
	mvn clean package

build-docker:
	docker-compose -f docker/docker-compose.yaml build

run-docker:
	docker-compose -f docker/docker-compose.yaml up -d
	docker-compose -f docker/docker-compose.yaml logs -f

stop-docker:
	docker-compose -f docker/docker-compose.yaml down -v