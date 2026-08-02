# Copyright 2026 上海如静知华信息科技有限公司
.PHONY: dev demo test build up down

dev:
	cd frontend && npm run dev

demo:
	cd frontend && npm run dev:demo

test:
	cd backend && mvn test

build:
	cd backend && mvn package
	cd frontend && npm run build

up:
	docker compose up --build -d

down:
	docker compose down
