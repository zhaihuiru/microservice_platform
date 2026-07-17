# Anime Microservice Platform

基于微服务架构的动漫/影视作品社区系统。

## 技术栈

- Java 17
- Spring Boot 4.1.0
- Spring Cloud 2025.1.2
- Spring Cloud Alibaba 2025.1.0.0
- Nacos Discovery 3.1.1
- Spring Cloud Gateway
- OpenFeign
- MySQL 8.0
- Redis 7
- Vue 3 + Vite + Axios
- Docker Compose


## 版本适配说明

| 组件 | 版本 |
|---|---|
| Spring Boot | 4.1.0 |
| Spring Cloud | 2025.1.2 |
| Spring Cloud Alibaba | 2025.1.0.0 |
| Nacos Server | 3.1.1 |
| Gateway Starter | spring-cloud-starter-gateway-server-webflux |

## 服务划分

| 服务 | 端口 | 数据库 |
|---|---:|---|
| gateway-service | 8080 | 无 |
| auth-service | 9001 | auth_db |
| work-service | 9002 | work_db |
| character-service | 9003 | character_db |
| person-service | 9004 | person_db |
| rating-service | 9005 | rating_db |
| comment-service | 9006 | comment_db |
| favorite-service | 9007 | favorite_db |
| notification-service | 9008 | notification_db |
| chat-service | 9009 | chat_db |


