DB_URL=postgresql://admin:admin@localhost:5432/postgres_springapi?sslmode=disable

docker_compose_build:
	docker-compose build

docker_build:
	docker buildx build -t nguyenthuanit265/springapi-image .

docker_push:
	docker push nguyenthuanit265/springapi-image

docker_build_run:
	docker-compose up

run_postgres:
	-docker network create springapi-network
	docker run --name postgres-springapi --network springapi-network -p 5432:5432 -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin -d postgres:13.12

start_postgres:
	docker start postgres-springapi

build_app:
	docker build -t nguyenthuanit265/springapi-image:latest .

run_app:
	docker run --name springapi-service --network springapi-network -p 8082:8082 -e DB_SOURCE="postgresql://admin:admin@postgres-springapi:5432/postgres_springapi_db?sslmode=disable" nguyenthuanit265/springapi-image:latest

start_app:
	docker start springapi-service

create_db:
	docker exec -it postgres-springapi createdb --username=admin --owner=admin postgres_springapi_db

drop_db:
	docker exec -it postgres-springapi dropdb --username=admin postgres_springapi_db
