setup:
	gradle wrapper --gradle-version 7.3.1

clean:
	./gradlew clean

lint:
	./gradlew checkstyleMain checkstyleTest

build:
	./gradlew clean build

start:
	./gradlew bootRun

test:
	./gradlew test

install:
	./gradlew install

report:
	./gradlew jacocoTestReport

check-updates:
	./gradlew dependencyUpdates