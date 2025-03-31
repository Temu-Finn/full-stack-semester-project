.PHONY: db, down, fresh

db:
	docker-compose up -d mysql

down:
	docker-compose down

fresh:
	docker-compose down -v
	make db
