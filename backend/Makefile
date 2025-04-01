.PHONY: db down fresh run test

db:
	docker-compose up -d mysql

down:
	docker-compose down

fresh:
	docker-compose down -v
	make db

ARTIFACT_ID := backend
VERSION := 0.0.1-SNAPSHOT
TARGET_DIR := target
JAR_NAME := ${TARGET_DIR}/${ARTIFACT_ID}-${VERSION}.jar

# Use Maven Wrapper if available, otherwise system Maven
MVN := ./mvnw
ifeq (, $(shell which $(MVN)))
	MVN := mvn
endif

# Build and run the application
run:
	@echo "Building the application..."
	@$(MVN) clean package -DskipTests
	@echo "Running the application..."
	@java -jar ${JAR_NAME}

test:
	@echo "Running the tests..."
	@$(MVN) test
