### Hexlet tests and linter status:
[![Actions Status](https://github.com/mchernichenko/java-project-lvl5/workflows/hexlet-check/badge.svg)](https://github.com/mchernichenko/java-project-lvl5/actions)
![Java CI](https://github.com/mchernichenko/java-project-lvl5/actions/workflows/java-ci.yml/badge.svg)
[![Maintainability](https://api.codeclimate.com/v1/badges/bc990ec32ef307ecfc93/maintainability)](https://codeclimate.com/github/mchernichenko/java-project-lvl5/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/bc990ec32ef307ecfc93/test_coverage)](https://codeclimate.com/github/mchernichenko/java-project-lvl5/test_coverage)

## Менеджер задач

[Demo on Heroku](http://java-project-lvl5.herokuapp.com)

[Swagger documentation](http://java-project-lvl5.herokuapp.com/swagger-ui.html)

### Stack:
* Spring Boot
* Spring Security
* Spring Data JPA
* Swagger (OAS v3)
* Liquibase
* Database: H2, Postgres
* Build tool: Gradle

### Локальный запуск
* Клонировать проект: git clone git@github.com:mchernichenko/java-project-lvl5.git
* Убедитесь, что у вас в системе установлена утилита make, выполнив в терминале команду *make -v*
  * [Что такое Makefile и как начать его использовать](https://guides.hexlet.io/makefile-as-task-runner/)

* Для запуска проект локально, с использованием H2, из корня проекта выполнить команду
```sh
make start 
```
* Для запуска проект локально, с использованием Postgres, в файле конфигурации *java-project-lvl5/src/main/resources/config/application-dev-pg.yaml*
  прописать в *spring.datasource.url* строку коннекта к БД Postgres и из корня проекта выполнить команду
```sh
make start-pg 
``` 
* Для генерации Open API Specification v.3 выполнить команду
```sh
make api-doc 
```
---

* Сгенерированная спецификация будет сохранена в папке проекта в файле *./build/openapi.yaml*
* Запущенное приложение будет доступно по адресу: *http://localhost:5000/welcome*
* OpenAPI приложения см. в Swagger UI, доступный по адресу: *http://localhost:5000/swagger-ui.html*
* OpenAPI description (json format): *http://localhost:5000/v3/api-docs/*



