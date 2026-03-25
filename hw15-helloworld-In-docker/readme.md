# Hello World in Docker
__Домашнее задание__
Запуск SpringBoot приложения в Docker

__Цель:__
Создать SpringBoot приложение с эндпоинтами и запустить его в Docker

## Building
```shell
mvn clean package
```

## Launching
```shell
java -jar target/hw15-helloworld-In-docker-1.0-SNAPSHOT.jar
```

## Docker
### Building an image
```shell
docker build -t hello-world:v1 .
```

### Creating a container
```shell
docker create --name hello-world -p 80:8080  hello-world:v1
```

### Start a container
```shell
docker start hello-world
```