# 开发环境配置指南

高校教学成果管理系统（edu-achievement-master）完整开发环境搭建说明。

---

## 1. 硬件与操作系统

| 要求 | 建议 |
|------|------|
| 操作系统 | Windows 10/11、Linux、macOS |
| 内存 | 8GB+（Nacos + MySQL + Redis + 5+ 微服务 + 前端） |
| 磁盘 | 10GB+ 空闲空间 |

---

## 2. 必需软件（含版本）

| 软件 | 版本要求 | 用途 | 下载/安装 |
|------|----------|------|-----------|
| **JDK** | **1.8**（Java 8） | 后端编译与运行 | [AdoptOpenJDK 8](https://adoptium.net/) |
| **Maven** | **3.6+** | 后端构建 | [maven.apache.org](https://maven.apache.org/) |
| **MySQL** | **8.0** | 业务数据库 + Nacos 配置库 | [mysql.com](https://dev.mysql.com/downloads/) |
| **Redis** | **5.x / 6.x / 7.x** | 会话缓存、验证码 | [redis.io](https://redis.io/download/) 或 Windows 版从 [Memurai](https://www.memurai.com/) |
| **Nacos** | **2.2.3** | 注册中心 + 配置中心 | [GitHub Releases](https://github.com/alibaba/nacos/releases/tag/2.2.3) |
| **Node.js** | **14.x 或 16.x** | 前端开发与构建 | [nodejs.org](https://nodejs.org/) |

### 可选软件

| 软件 | 用途 | 说明 |
|------|------|------|
| MinIO | 对象存储（文件上传） | 非必需，有本地存储兜底 |
| Sentinel Dashboard | 流量控制监控 | 非必需 |
| Git | 版本管理 | 强烈建议 |

---

## 3. Maven 配置

项目使用阿里云 Maven 镜像仓库。确保 `~/.m2/settings.xml`（Windows: `C:\Users\<用户名>\.m2\settings.xml`）包含：

```xml
<mirrors>
  <mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <name>Aliyun Maven</name>
    <url>https://maven.aliyun.com/repository/public</url>
  </mirror>
</mirrors>
```

---

## 4. MySQL 数据库初始化

### 4.1 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS edu_system DEFAULT CHARSET utf8mb3;
CREATE DATABASE IF NOT EXISTS edu_config DEFAULT CHARSET utf8mb3;
```

### 4.2 导入 SQL（按顺序）

```bash
mysql -u root -p < sql/data/edu_system.sql
mysql -u root -p < sql/data/edu_config.sql
```

- **edu_system** — 业务数据表（用户、角色、成果、审核记录等）
- **edu_config** — Nacos 配置数据表（各微服务配置项）

### 4.3 数据库连接信息

```
Host:     localhost:3306
用户名:   root
密码:     242648
```

> 如你本地 MySQL root 密码不同，需修改 Nacos 中对应配置文件：
> - `edu-system-dev.yml`
> - `edu-achievement-dev.yml`
> - `edu-job-dev.yml`

---

## 5. Redis

```
Host: localhost
Port: 6379
密码: （无密码）
```

用于存储登录 Token（`login_tokens:{uuid}`，TTL 720 分钟）和验证码。

Windows 下可使用 [Memurai](https://www.memurai.com/) 或 WSL 运行 Redis。

---

## 6. Nacos 配置中心

### 6.1 启动

```bash
# Windows（解压后的 nacos/bin 目录）
startup.cmd -m standalone

# Linux / macOS
sh startup.sh -m standalone
```

### 6.2 控制台

```
地址: http://127.0.0.1:8848/nacos
账号: nacos
密码: nacos
```

### 6.3 已预置的配置项（导入 edu_config.sql 后自动生效）

| data_id | 对应服务 | 说明 |
|----------|----------|------|
| `application-dev.yml` | 所有服务 | 公共配置（Feign、Actuator 等） |
| `edu-gateway-dev.yml` | Gateway (8080) | 路由规则、CORS、XSS 过滤 |
| `edu-auth-dev.yml` | Auth (9200) | JWT 密钥、Redis |
| `edu-system-dev.yml` | System (9201) | 数据源、MyBatis |
| `edu-achievement-dev.yml` | Achievement (9205) | 动态数据源、缓存 |
| `edu-file-dev.yml` | File (9300) | MinIO / FastDFS / 本地存储 |
| `edu-job-dev.yml` | Job (9203) | Quartz 调度数据源 |
| `edu-monitor-dev.yml` | Monitor (9100) | Spring Boot Admin |

---

## 7. 微服务清单

| 服务 | Spring 名称 | 端口 | 模块路径 | 说明 |
|------|-------------|------|----------|------|
| **Gateway** | edu-gateway | **8080** | `edu-gateway` | 网关入口，前端统一访问 |
| **Auth** | edu-auth | **9200** | `edu-auth` | 认证授权、JWT |
| **System** | edu-system | **9201** | `edu-modules/edu-system` | 用户/角色/菜单/部门 |
| **Achievement** | edu-achievement | **9205** | `edu-modules/edu-achievement` | 成果申报与审核 |
| **File** | edu-file | **9300** | `edu-modules/edu-file` | 文件上传下载 |
| Job | edu-job | 9203 | `edu-modules/edu-job` | Quartz 定时任务（可选） |
| Monitor | edu-monitor | 9100 | `edu-visual/edu-monitor` | Spring Boot Admin（可选） |

---

## 8. 启动顺序

严格按照以下顺序启动：

```bash
# ============================================
# Step 0: 确保基础设施就绪
# ============================================
# MySQL    localhost:3306
# Redis    localhost:6379
# Nacos    localhost:8848 (standalone 模式)

# ============================================
# Step 1: Gateway（必须先启动，前端依赖它）
# ============================================
mvn spring-boot:run -pl edu-gateway

# ============================================
# Step 2: 核心业务服务
# ============================================
mvn spring-boot:run -pl edu-auth
mvn spring-boot:run -pl edu-modules/edu-system
mvn spring-boot:run -pl edu-modules/edu-achievement
mvn spring-boot:run -pl edu-modules/edu-file

# ============================================
# Step 3: 可选服务
# ============================================
mvn spring-boot:run -pl edu-modules/edu-job
mvn spring-boot:run -pl edu-visual/edu-monitor
```

> 建议在不同终端窗口中分别启动各服务。首次编译可能较慢，后续 `mvn spring-boot:run` 无需重新打包。

### 后端一键构建（非运行）

```bash
mvn clean package -DskipTests
```

---

## 9. 前端搭建

### 9.1 安装依赖

```bash
cd edu-ui
npm install
```

### 9.2 开发模式

```bash
npm run dev
```

- 开发服务器运行在 **端口 80**（访问 `http://localhost`）
- 自动代理 `/dev-api` 到后端 Gateway（`http://localhost:8080`）

### 9.3 生产构建

```bash
npm run build:prod
```

构建产物输出到 `edu-ui/edu-admin/` 目录。

---

## 10. 关键技术栈版本

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 2.7.18 |
| 微服务 | Spring Cloud | 2021.0.9 |
| 微服务 | Spring Cloud Alibaba | 2021.0.6.1 |
| 前端框架 | Vue | 2.6.12 |
| UI 库 | Element UI | 2.15.14 |
| 图表 | ECharts | 5.4.0 |
| JWT | jjwt | 0.9.1 |
| 连接池 | Druid | 1.2.27 |
| JSON | FastJSON2 | 2.0.60 |
| 分页 | PageHelper | 2.0.0 |
| Excel | Apache POI | 4.1.2 |
| 对象存储 | MinIO Client | 8.2.2 |

---

## 11. 默认账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 超级管理员 | `admin` | `admin123` | 可访问 `/admin/*` 全部功能 |
| 普通教师 | `teacher` | （BCrypt 加密） | 可申报成果 |
| 校级审核员 | `schoolAudit` | （BCrypt 加密） | 校级审核 |
| Nacos 控制台 | `nacos` | `nacos` | 配置管理 |
| Spring Boot Admin | `admin` | `242648` | 监控面板 |

---

## 12. 常见问题

### MySQL 8 时区问题
Nacos 配置中 JDBC URL 已设置 `serverTimezone=GMT%2B8`，如仍有问题，在 MySQL 中执行：
```sql
SET GLOBAL time_zone = '+08:00';
```

### 端口冲突
80 端口常被 IIS 或 Skype 占用，可在 `edu-ui/vue.config.js` 中修改 `devServer.port`。

### Nacos 无法连接
确认 `startup.cmd -m standalone` 已执行，访问 `http://127.0.0.1:8848/nacos` 验证。

### 构建报错找不到依赖
首次构建需下载大量 Maven 依赖，确保阿里云镜像配置正确，网络通畅。

---

## 13. 从零搭建速查

```bash
# 1. 启动 MySQL、Redis、Nacos

# 2. 初始化数据库
mysql -u root -p < sql/data/edu_system.sql
mysql -u root -p < sql/data/edu_config.sql

# 3. 启动后端（每个服务一个终端窗口）
mvn spring-boot:run -pl edu-gateway
mvn spring-boot:run -pl edu-auth
mvn spring-boot:run -pl edu-modules/edu-system
mvn spring-boot:run -pl edu-modules/edu-achievement
mvn spring-boot:run -pl edu-modules/edu-file

# 4. 启动前端
cd edu-ui
npm install
npm run dev

# 5. 浏览器访问 http://localhost
# 使用 admin / admin123 登录
```
