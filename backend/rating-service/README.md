# rating-service 评分与投票服务

动漫作品评分与用户投票微服务，隶属于 IP 宇宙百科图鉴平台。

## 基本信息

| 项目 | 值 |
|---|---|
| 服务名 | rating-service |
| 端口 | 9005 |
| 数据库 | rating_db |
| 注册中心 | Nacos (localhost:8848) |
| 框架 | Spring Boot 4.1.0 / Spring Cloud 2025.1.2 |

## API 接口

### 评分接口

| 方法 | 路径 | 说明 | 鉴权 |
|---|---|---|---|
| POST | `/api/ratings` | 提交/修改评分（1-10分） | User-Id |
| GET | `/api/ratings/avg/{workId}` | 获取作品平均分 | 无 |
| GET | `/api/ratings/work/{workId}` | 获取当前用户对作品的评分 | User-Id |
| DELETE | `/api/ratings/{workId}` | 删除自己的评分 | User-Id |
| DELETE | `/api/ratings/admin/{id}` | 管理员删除任意评分 | User-Role: ADMIN |

### 投票接口

| 方法 | 路径 | 说明 | 鉴权 |
|---|---|---|---|
| POST | `/api/votes` | 投票（每个主题限投一次） | User-Id |
| GET | `/api/votes/topic/{topicId}/result` | 查看投票结果 | 无 |

### Feign 内部接口

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/ratings/works/{workId}/stat` | 获取作品评分统计（供 work-service 调用） |

## 请求头规范

| 请求头 | 说明 |
|---|---|
| `User-Id` | 当前用户 ID |
| `User-Role` | 角色，`ADMIN` 为管理员 |

## 环境变量

| 变量 | 默认值 | 说明 |
|---|---|---|
| `SERVER_PORT` | 9005 | 服务端口 |
| `NACOS_ADDR` | localhost:8848 | Nacos 地址 |
| `MYSQL_HOST` | localhost | MySQL 主机 |
| `MYSQL_PORT` | 3306 | MySQL 端口 |
| `MYSQL_USERNAME` | root | 数据库用户名 |
| `MYSQL_PASSWORD` | 123456 | 数据库密码 |

## 启动方式

```bash
# IDE 中运行 RatingServiceApplication.main()
# 或 Maven
mvn spring-boot:run
```
