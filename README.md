# Проект Task Manager

Технологии: Java 11, Spring Boot 2.7.2, PostgreSQL, Maven, Docker, Hibernate, Lombok, Logbook.

## Описание

Трекер задач, позволяющий эффективно организовать совместную работу над задачами. При выполнении масштабной
задачи, её можно разбить на подзадачи. Дает возможность добавлять, обновлять, удалять и получать задачи, в том числе в
порядке приоритета. Также трекер отображает последние просмотренные пользователем задачи.

Модель базы данных(InheritanceType.SINGLE_TABLE)

![Модель базы данных](src/main/resources/task_manager.png)

## Запуск приложения

### С помощью командной строки

Необходимые инструменты:

* [Java (JDK) 11;](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html)
* [Apache Maven 4.x](https://maven.apache.org/users/index.html)

Находясь в корневой папке проекта, выполнить:

* mvn package
* java -jar target/task-manager-0.0.1-SNAPSHOT.jar

### С помощью Docker

Необходимые инструменты:

* [Java (JDK) 11;](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html)
* [Apache Maven 4.x](https://maven.apache.org/users/index.html)
* [Docker](https://www.docker.com/)

Находясь в корневой папке проекта, запустить Docker и выполнить:

* docker-compose up
