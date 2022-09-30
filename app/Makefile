setup:
	gradle wrapper --gradle-version 7.4

clean:
	./gradlew clean

build:
	./gradlew clean build

start:
	APP_ENV=development ./gradlew run

install:
	./gradlew install

start-dist:
	APP_ENV=production ./build/install/app/bin/app

generate-migrations:
	./gradlew generateMigrations

lint:
	./gradlew checkstyleMain checkstyleTest

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

check-updates:
	./gradlew dependencyUpdates

.PHONY: build

#setup:
#	gradle wrapper --gradle-version 7.4
#
#clean:
#	./app/gradlew clean
#
#build:
#	./app/gradlew clean build
#
#run:
#	./app/gradlew run
#
#lint:
#	./app/gradlew checkstyleMain
#
#install: clean
#	./app/gradlew install
#
#run-dist:
#	./app/build/install/app/bin/app
#
#check-updates:
#	./app/gradlew dependencyUpdates
#
#.PHONY: build
