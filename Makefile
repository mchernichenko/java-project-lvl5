setup:
	gradle wrapper --gradle-version 7.3.1

clean:
	./gradlew clean

lint:
	./gradlew checkstyleMain checkstyleTest

build:
	./gradlew clean build

start:
	./gradlew bootRun --args='--spring.profiles.active=dev'

start-pg:
	./gradlew bootRun --args='--spring.profiles.active=dev-pg'

start-prod:
	./gradlew bootRun --args='--spring.profiles.active=prod'

test:
	./gradlew test

install:
	./gradlew install

report:
	./gradlew jacocoTestReport

check-updates:
	./gradlew dependencyUpdates

generate-migrations:
	./gradlew diffChangeLog