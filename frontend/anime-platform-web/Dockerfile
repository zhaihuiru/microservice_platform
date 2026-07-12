# ==============================
# 第一阶段：构建 Vue 项目
# 不使用 node:22-alpine，避免 npm Alpine 异常
# ==============================
FROM node:22-bookworm-slim AS build

WORKDIR /app

# 先复制依赖文件，利用 Docker 缓存
COPY package.json package-lock.json .npmrc ./

# 安装生产依赖和开发依赖
# Vite 位于 devDependencies 中
RUN npm ci --include=dev --no-audit --no-fund

# 复制前端源代码
COPY . .

# 构建生产环境文件
RUN npm run build


# ==============================
# 第二阶段：Nginx 部署静态文件
# ==============================
FROM nginx:alpine

RUN rm -rf /usr/share/nginx/html/*

COPY --from=build /app/dist /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]