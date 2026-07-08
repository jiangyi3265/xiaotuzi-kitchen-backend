# 小兔子厨房后端服务

小兔子厨房后端服务为私房菜点单项目提供接口、权限、订单、菜品、分类、厨师、配送员、分享广场、文件上传和微信小程序登录能力。项目基于 RuoYi-Vue 后端改造，面向管理后台和用户端小程序提供统一业务数据。

## 技术栈

- Java 17
- Spring Boot 2.5
- Spring Security / JWT
- MyBatis / MySQL 8
- Redis
- Maven
- Docker / Docker Compose

## 关联仓库

| 子项目 | 仓库 | 说明 |
| --- | --- | --- |
| 后端服务 | [xiaotuzi-kitchen-backend](https://github.com/jiangyi3265/xiaotuzi-kitchen-backend) | Spring Boot API、权限、订单和业务数据 |
| 管理后台 | [xiaotuzi-kitchen-admin](https://github.com/jiangyi3265/xiaotuzi-kitchen-admin) | Vue3 管理端，用于维护菜品、订单、配送员等 |
| 用户端 | [xiaotuzi-kitchen-app](https://github.com/jiangyi3265/xiaotuzi-kitchen-app) | uni-app 微信小程序，面向用户点单和分享 |

## 快速启动

准备 MySQL 和 Redis 后，按需导入 `sql/ry_20250522.sql`、`sql/kitchen.sql`、`sql/kitchen_seed.sql`。

```bash
mvn clean package -DskipTests
java -jar ruoyi-admin/target/ruoyi-admin.jar
```

常用生产环境变量：

```bash
SPRING_PROFILES_ACTIVE=druid,prod
MYSQL_HOST=127.0.0.1
MYSQL_DATABASE=ha
MYSQL_USERNAME=root
MYSQL_PASSWORD=your_mysql_password
REDIS_HOST=127.0.0.1
REDIS_PASSWORD=your_redis_password
JWT_SECRET=your_jwt_secret
WX_APPID=your_wechat_appid
WX_SECRET=your_wechat_secret
```

Docker 构建：

```bash
docker build -t xiaotuzi-backend:latest .
```

## GitHub Actions 部署

推送到 `main` 后会通过 SSH 登录服务器，更新 `/opt/xiaotuzi-kitchen/current/RuoYi-Vue`，执行数据库补丁，并重建 `backend` 容器。

仓库需要配置以下 Secrets：

```text
SERVER_HOST
SERVER_PORT
SERVER_USER
SERVER_SSH_KEY
SERVER_DEPLOY_PATH
```

## 简历描述示例

基于 Spring Boot + RuoYi 搭建私房菜业务后端，完成微信小程序登录、菜品/分类/订单/配送员/分享广场等模块，并通过 Docker Compose 和 GitHub Actions 实现自动化部署。
