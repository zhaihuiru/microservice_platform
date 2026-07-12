# comment-service 评论服务

动漫作品评论与互动微服务，隶属于 IP 宇宙百科图鉴平台。

## 基本信息

| 项目 | 值 |
|---|---|
| 服务名 | comment-service |
| 端口 | 9006 |
| 数据库 | comment_db |
| 注册中心 | Nacos (localhost:8848) |
| 框架 | Spring Boot 4.1.0 / Spring Cloud 2025.1.2 |

## 业务规则

- 新评论/回复默认状态为 `PENDING`（待审核），需管理员审核通过（`APPROVED`）后公开可见
- 用户查询评论列表只返回 `APPROVED` 状态的评论
- 编辑评论后状态重置为 `PENDING`，需重新审核
- 发表评论前会通过 Feign 调用 work-service 验证作品是否存在

## API 接口

### 用户接口

| 方法 | 路径 | 说明 | 鉴权 |
|---|---|---|---|
| POST | `/api/comments` | 发表评论 | User-Id |
| POST | `/api/comments/{parentId}/reply` | 回复评论 | User-Id |
| GET | `/api/comments/work/{workId}` | 按作品查询已审核评论 | 无 |
| PUT | `/api/comments/{id}/like` | 点赞/取消点赞 | User-Id |
| PUT | `/api/comments/{id}` | 编辑自己的评论 | User-Id |
| DELETE | `/api/comments/{id}` | 删除自己的评论 | User-Id |

### 管理员接口

| 方法 | 路径 | 说明 | 鉴权 |
|---|---|---|---|
| GET | `/api/comments/admin/pending` | 获取待审核评论列表 | User-Role: ADMIN |
| PUT | `/api/comments/admin/{id}/status` | 审核评论（APPROVED/REJECTED） | User-Role: ADMIN |
| PUT | `/api/comments/admin/{id}/sticky` | 置顶/取消置顶 | User-Role: ADMIN |
| PUT | `/api/comments/admin/{id}/essence` | 精华/取消精华 | User-Role: ADMIN |
| DELETE | `/api/comments/admin/{id}` | 管理员删除任意评论 | User-Role: ADMIN |

### Feign 内部接口

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/comments?targetType=WORK&targetId={id}` | 查询评论列表（供 work-service 调用） |

## 请求头规范

| 请求头 | 说明 |
|---|---|
| `User-Id` | 当前用户 ID |
| `User-Role` | 角色，`ADMIN` 为管理员 |

## 环境变量

| 变量 | 默认值 | 说明 |
|---|---|---|
| `SERVER_PORT` | 9006 | 服务端口 |
| `NACOS_ADDR` | localhost:8848 | Nacos 地址 |
| `MYSQL_HOST` | localhost | MySQL 主机 |
| `MYSQL_PORT` | 3306 | MySQL 端口 |
| `MYSQL_USERNAME` | root | 数据库用户名 |
| `MYSQL_PASSWORD` | 123456 | 数据库密码 |

## 启动方式

```bash
# IDE 中运行 CommentServiceApplication.main()
# 或 Maven
mvn spring-boot:run
```
