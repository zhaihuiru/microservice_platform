# favorite-service 收藏服务

动漫作品收藏管理微服务，隶属于 IP 宇宙百科图鉴平台。

## 基本信息

| 项目 | 值 |
|---|---|
| 服务名 | favorite-service |
| 端口 | 9007 |
| 数据库 | favorite_db |
| 注册中心 | Nacos (localhost:8848) |
| 框架 | Spring Boot 4.1.0 / Spring Cloud 2025.1.2 |

## 业务规则

- 每个用户可创建多个收藏夹，收藏夹默认不公开（`isPublic=false`）
- 收藏作品前会通过 Feign 调用 work-service 验证作品是否存在
- 查看他人收藏夹只返回已设为公开（`isPublic=true`）的收藏夹
- 同一作品可被加入同一用户的多个收藏夹

## API 接口

### 收藏夹管理

| 方法 | 路径 | 说明 | 鉴权 |
|---|---|---|---|
| POST | `/api/favorites/folders` | 创建收藏夹 | User-Id |
| GET | `/api/favorites/folders/me` | 获取我的全部收藏夹 | User-Id |
| GET | `/api/favorites/folders/user/{userId}` | 获取他人的公开收藏夹 | 无 |
| DELETE | `/api/favorites/folders/{folderId}` | 删除自己的收藏夹 | User-Id |
| DELETE | `/api/favorites/admin/folders/{folderId}` | 管理员删除任意收藏夹 | User-Role: ADMIN |

### 收藏操作

| 方法 | 路径 | 说明 | 鉴权 |
|---|---|---|---|
| POST | `/api/favorites` | 收藏作品到指定收藏夹 | User-Id |
| DELETE | `/api/favorites` | 取消收藏（需传入 folderId 和 workId） | User-Id |
| GET | `/api/favorites/folder/{folderId}/items` | 获取收藏夹内容 | 无 |

### Feign 内部接口

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/favorites/check?userId=&targetType=WORK&targetId=` | 检查是否已收藏（供 work-service 调用） |

## 请求头规范

| 请求头 | 说明 |
|---|---|
| `User-Id` | 当前用户 ID |
| `User-Role` | 角色，`ADMIN` 为管理员 |

## 环境变量

| 变量 | 默认值 | 说明 |
|---|---|---|
| `SERVER_PORT` | 9007 | 服务端口 |
| `NACOS_ADDR` | localhost:8848 | Nacos 地址 |
| `MYSQL_HOST` | localhost | MySQL 主机 |
| `MYSQL_PORT` | 3306 | MySQL 端口 |
| `MYSQL_USERNAME` | root | 数据库用户名 |
| `MYSQL_PASSWORD` | 123456 | 数据库密码 |

## 启动方式

```bash
# IDE 中运行 FavoriteServiceApplication.main()
# 或 Maven
mvn spring-boot:run
```
