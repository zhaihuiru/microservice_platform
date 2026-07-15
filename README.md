# Anime Microservice Platform

基于微服务架构的动漫/影视作品社区系统课程大作业骨架。

## 技术栈

- Java 17（Spring Boot 4.1.0 最低要求；也可使用 Java 21/25）
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

本骨架已按 Spring Boot 4.1.0 适配：

| 组件 | 版本 | 说明 |
|---|---|---|
| Spring Boot | 4.1.0 | 基于 Spring Framework 7.x，最低 Java 17 |
| Spring Cloud | 2025.1.2 | Oakwood SR2，兼容 Spring Boot 4.1.0 |
| Spring Cloud Alibaba | 2025.1.0.0 | 支持 Spring Boot 4.x / Spring Cloud 2025.1.x |
| Nacos Server | 3.1.1 | 与 Spring Cloud Alibaba 2025.1.0.0 对应 |
| Gateway Starter | spring-cloud-starter-gateway-server-webflux | Spring Cloud 2025 推荐的新 Gateway WebFlux starter |

注意：Spring Cloud Alibaba 官方版本表目前主要标注 2025.1.0.0 对应 Spring Boot 4.0.0，但其发布说明写明支持 Spring Boot 4.x 和 Spring Cloud 2025.1.x。因此课程项目使用 Boot 4.1.0 时，建议优先使用本骨架的版本组合。

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


