# Test Results

- Total: 19
- Passed: 19
- Failed: 0

| ID | Category | Test | Expected | Actual | Result |
|---|---|---|---|---|---|
| P-01 | 环境 | Docker命令可用 | docker可执行 | True | PASS |
| P-02 | 环境 | Java命令可用 | java可执行 | True | PASS |
| P-03 | 环境 | Maven命令可用 | mvn可执行 | True | PASS |
| P-10 | 工程 | 检查backend/pom.xml | 文件存在 | True | PASS |
| P-11 | 工程 | 检查deploy/docker-compose.yml | 文件存在 | True | PASS |
| P-12 | 工程 | 检查deploy/docker-compose-app.yml | 文件存在 | True | PASS |
| P-13 | 工程 | 检查deploy/mysql/init.sql | 文件存在 | True | PASS |
| P-14 | 工程 | 检查frontend/anime-platform-web/package.json | 文件存在 | True | PASS |
| P-20 | 服务可用性 | auth-service经Gateway可访问 | HTTP 200 | HTTP 200 | PASS |
| P-21 | 服务可用性 | work-service经Gateway可访问 | HTTP 200 | HTTP 200 | PASS |
| P-22 | 服务可用性 | character-service经Gateway可访问 | HTTP 200 | HTTP 200 | PASS |
| P-23 | 服务可用性 | person-service经Gateway可访问 | HTTP 200 | HTTP 200 | PASS |
| P-24 | 服务可用性 | rating-service经Gateway可访问 | HTTP 200 | HTTP 200 | PASS |
| P-25 | 服务可用性 | comment-service经Gateway可访问 | HTTP 200 | HTTP 200 | PASS |
| P-26 | 服务可用性 | favorite-service经Gateway可访问 | HTTP 200 | HTTP 200 | PASS |
| P-27 | 服务可用性 | notification-service经Gateway可访问 | HTTP 200 | HTTP 200 | PASS |
| P-28 | 服务可用性 | chat-service经Gateway可访问 | HTTP 200 | HTTP 200 | PASS |
| P-30 | 服务治理 | Gateway Actuator健康检查 | HTTP 200 | HTTP 200 | PASS |
| P-31 | 部署 | 保存Docker容器状态 | 命令成功 | 命令成功 | PASS |
