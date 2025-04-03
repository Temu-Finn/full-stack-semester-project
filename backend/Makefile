.PHONY: db down fresh run test build backend

default: build
	docker compose up -d

db:
	docker compose up -d mysql

down:
	docker compose down

fresh:
	docker compose down -v
	make db

build:
	./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=spring-boot-backend

backend:
	docker compose up -d spring-boot

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
