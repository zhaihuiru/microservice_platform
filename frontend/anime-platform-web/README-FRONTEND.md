# Anime Atlas 前端

## 启动

```powershell
cd frontend\anime-platform-web
npm install
npm run dev
```

开发地址：http://localhost:5173
后端 Gateway：http://localhost:8080

## 已覆盖模块

- 登录、注册、个人资料、修改密码
- 作品列表、搜索、分类、详情、评分、评论、收藏、作品群聊
- 角色列表、详情、作品与角色关系
- 人物列表、详情、参与作品
- 收藏夹、通知中心、聊天中心
- 管理员用户、内容、关系、评论、通知、聊天和服务健康管理

## 必须确认的 Gateway 路由

Gateway 需要放行 `/api/categories/**`、`/api/work-categories/**`、`/api/character-relations/**`、`/api/work-character-actors/**`、`/api/person-works/**`、`/work/file/**`、`/character/file/**`、`/person/file/**`。
评分、评论、收藏服务读取 `User-Id` / `User-Role`，Gateway 需要在解析 JWT 后同时写入这两个请求头。
