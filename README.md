<h1 align="center">🎓 高校教学成果管理系统</h1>

<p align="center">
  <em>基于 RuoYi-Cloud 微服务架构的教学成果全生命周期管理平台</em>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Spring%20Cloud-2021.0.9-blue" alt="Spring Cloud">
  <img src="https://img.shields.io/badge/Spring%20Cloud%20Alibaba-2021.0.6.1-cyan" alt="Spring Cloud Alibaba">
  <img src="https://img.shields.io/badge/Vue.js-2.6-42b883" alt="Vue.js">
  <img src="https://img.shields.io/badge/Element%20UI-2.15-409eff" alt="Element UI">
  <img src="https://img.shields.io/badge/MySQL-5.7-4479a1" alt="MySQL">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License">
</p>

---

## 📋 项目简介

本系统为高校教学成果申报、审核、管理提供一站式解决方案。教师可在线提交成果（论文、教材、竞赛、教改项目等），经 **院级审核 → 校级审核** 两级审批后归档管理。系统提供前台门户（新闻公告、快捷申报入口）与后台管理双界面，配备 **ECharts 数据仪表盘**、**Excel 导出**、**站内通知** 等功能。

### ✨ 核心特性

- 🔄 **两级审核流程** — 院级 → 校级，支持通过 / 驳回 / 退回修改
- 🏠 **前台门户系统** — 游客可浏览新闻公告，登录后进入申报/审核界面
- 📊 **ECharts 数据大屏** — 成果类型饼图、状态柱状图、审核通过率仪表盘
- 👤 **角色差异化路由** — 教师/审核员进前台，管理员进后台，自动跳转
- 📩 **站内通知** — 审核完成后自动通知教师
- 📥 **Excel 导出** — 支持中文状态映射和教师姓名导出
- 📰 **新闻管理** — 支持富文本编辑、封面图上传、门户发布
- 🎨 **现代化 UI** — 学术深邃风主题，深海军蓝侧边栏 + 琥珀金强调色

---

## 🏗️ 系统架构

```
                          ┌──────────┐
                          │  Nginx   │
                          │  :80     │
                          └────┬─────┘
                               │
                          ┌────▼─────┐
                          │ Gateway  │
                          │  :8080   │
                          └────┬─────┘
               ┌───────────────┼───────────────┐
          ┌────▼────┐    ┌─────▼─────┐    ┌────▼──────┐
          │  Auth   │    │  System   │    │Achievement │
          │  :9200  │    │  :9201    │    │  :9205     │
          └────┬────┘    └─────┬─────┘    └────┬──────┘
               │               │               │
          ┌────▼───────────────▼───────────────▼────┐
          │        MySQL 5.7  +  Redis 6.0          │
          │        Nacos 2.x 配置中心/注册中心        │
          └─────────────────────────────────────────┘
```

---

## 📂 项目结构

```
edu-achievement-master/
├── edu-gateway              # API 网关（路由转发、JWT 鉴权、XSS 过滤）
├── edu-auth                 # 认证授权中心（JWT 令牌签发/刷新）
├── edu-api                  # 远程调用 Feign 接口
│   └── edu-api-system       #   系统 API（用户/文件/日志远程调用）
├── edu-common               # 公共库（9 个子模块）
│   ├── edu-common-core      #   核心工具（AjaxResult、Excel、异常处理）
│   ├── edu-common-security  #   安全框架（@RequiresPermissions、HeaderInterceptor）
│   ├── edu-common-redis     #   Redis 缓存封装
│   ├── edu-common-log       #   @Log 操作日志切面
│   ├── edu-common-datascope #   数据权限 SQL 拦截
│   ├── edu-common-datasource#   多数据源切换
│   ├── edu-common-seata     #   分布式事务
│   ├── edu-common-swagger   #   API 文档（SpringDoc）
│   └── edu-common-sensitive #   数据脱敏
├── edu-modules              # 业务微服务
│   ├── edu-achievement      #   ★ 教学成果管理（核心业务）
│   ├── edu-system           #   系统管理（用户/角色/菜单/部门）
│   ├── edu-file             #   文件服务（本地/MinIO/FastDFS）
│   ├── edu-gen              #   代码生成器
│   └── edu-job              #   定时任务（Quartz）
├── edu-ui                   # Vue.js 前端
├── sql                      # 数据库脚本
├── docker                   # Docker Compose 部署
└── zMD                      # 开发文档与参考资料
```

---

## 🔧 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| **后端框架** | Spring Boot | 2.7.18 |
| **微服务** | Spring Cloud | 2021.0.9 |
| **微服务(阿里)** | Spring Cloud Alibaba | 2021.0.6.1 |
| **注册/配置中心** | Nacos | 2.x |
| **网关** | Spring Cloud Gateway | 3.1.x |
| **持久层** | MyBatis + PageHelper | 2.0.0 |
| **数据库** | MySQL | 5.7 |
| **缓存** | Redis | 6.0+ |
| **认证** | JWT (jjwt) | 0.9.1 |
| **前端框架** | Vue.js | 2.6.12 |
| **UI 组件库** | Element UI | 2.15.14 |
| **图表** | ECharts | 5.4.0 |
| **富文本** | Quill | 2.0.2 |
| **文件存储** | 本地 / MinIO / FastDFS | - |
| **部署** | Docker Compose + Nginx | - |

---

## 🚀 快速开始

### 环境要求

| 工具 | 版本 |
|------|------|
| JDK | 1.8+ |
| Maven | 3.6+ |
| MySQL | 5.7+ |
| Redis | 6.0+ |
| Nacos | 2.x |
| Node.js | 16+ |

### 1. 克隆项目

```bash
git clone https://github.com/your-repo/edu-achievement.git
cd edu-achievement
```

### 2. 导入数据库

```bash
# 核心表
mysql -u root -p < docker/mysql/db/edu_system.sql
mysql -u root -p < docker/mysql/db/edu_config.sql
mysql -u root -p < sql/quartz.sql
```

### 3. 启动 Nacos

```bash
# 单机模式启动
startup.cmd -m standalone    # Windows
sh startup.sh -m standalone  # Linux/Mac
```

### 4. 启动后端微服务

按以下顺序依次启动：

```bash
# 1. 网关
mvn spring-boot:run -pl edu-gateway

# 2. 认证中心
mvn spring-boot:run -pl edu-auth

# 3. 系统管理
mvn spring-boot:run -pl edu-modules/edu-system

# 4. 教学成果（核心）
mvn spring-boot:run -pl edu-modules/edu-achievement

# 5. 文件服务
mvn spring-boot:run -pl edu-modules/edu-file
```

### 5. 启动前端

```bash
cd edu-ui

# 安装依赖
npm install

# 开发模式启动
npm run dev

# 生产构建
npm run build:prod
```

### 6. Docker 部署（可选）

```bash
# 构建所有 jar 包
mvn clean package -DskipTests

# 复制 jar 到 Docker 目录
sh docker/copy.sh

# 启动所有容器
cd docker
docker-compose up -d
```

---

## 📱 功能模块

### 前台门户（游客/教师/审核员）

| 页面 | 路径 | 说明 |
|------|------|------|
| 首页门户 | `/portal/home` | 新闻公告、快捷入口、数据概览 |
| 新闻动态 | `/portal/news` | 新闻列表与详情 |
| 教师申报 | `/portal/declare` | 教师提交成果申报 |
| 我的申报 | `/portal/mine` | 教师查看/管理自己的成果 |

### 后台管理（管理员）

| 页面 | 路径 | 说明 |
|------|------|------|
| 数据仪表盘 | `/admin` | ECharts 统计图表 |
| 成果管理 | `/achievement/achievement` | 全部成果列表 |
| 院级审核 | `/achievement/collegeAudit` | 院级审核员审批 |
| 校级审核 | `/achievement/schoolAudit` | 校级审核员审批 |
| 审核记录 | `/achievement/auditRecord` | 审核日志查询 |
| 新闻管理 | `/achievement/news` | 新闻增删改查 |
| 系统管理 | `/system/*` | 用户/角色/菜单/部门/字典等 |

### 权限矩阵

| 功能 | 教师 | 院级审核员 | 校级审核员 | 管理员 |
|------|:----:|:---------:|:---------:|:------:|
| 申报成果 | ✅ | - | - | - |
| 查看/编辑自己的成果 | ✅ | - | - | ✅ |
| 院级审核 | - | ✅ | - | ✅ |
| 校级审核 | - | - | ✅ | ✅ |
| 查看审核记录 | - | ✅(自己的) | ✅(自己的) | ✅(全部) |
| Excel 导出 | - | - | - | ✅ |
| 数据统计仪表盘 | ✅ | ✅ | ✅ | ✅ |
| 系统管理 | - | - | - | ✅ |

---

## 🔄 审核流程

```
教师提交 → 草稿(0) → 院审中(1) → 校审中(2) → 已通过(3) ✅
                        │            │
                        ├─ 驳回(4) ←─┘
                        │
                        └─ 退回修改(5) → 教师重新编辑 → 院审中(1)
```

| 状态码 | 含义 | 可执行操作 |
|--------|------|-----------|
| 0 | 草稿 | 教师编辑、提交 |
| 1 | 院审中 | 院级审核员：通过/驳回/退回 |
| 2 | 校审中 | 校级审核员：通过/驳回/退回 |
| 3 | 已通过 | 归档（终态） |
| 4 | 已驳回 | 教师可重新提交 |
| 5 | 退回修改 | 教师修改后重新提交 |

---

## 📊 数据仪表盘

Dashboard 根据登录角色展示不同数据：

- **管理员**：全校总成果、已通过、审核中、已驳回/退回
- **审核员**：待审核、已通过、退回修改、已驳回
- **教师**：我的申报、审核中、已通过、退回修改

配合 ECharts 图表：
1. 📊 成果类型分布（环形饼图）
2. 📈 审核状态统计（柱状图）
3. 🎯 审核通过率（仪表盘）
4. 🏛️ 各学院成果数量（横向柱状图）

---

## 📁 数据库

| 核心表 | 说明 |
|--------|------|
| `edu_achievement` | 教学成果主表（标题、内容、类型、状态、教师ID、学院ID） |
| `edu_audit_record` | 审核记录（审核级别、结果、意见、审核人） |
| `edu_news` | 新闻管理（标题、封面、富文本内容、发布状态） |
| `sys_user` | 用户表（教师/管理员/审核员） |
| `sys_dept` | 部门表（学院层级） |
| `sys_role` | 角色表（admin/teacher/CollegeAudit/SchoolAudit） |
| `sys_menu` | 菜单权限表 |
| `sys_notice` | 站内通知表 |

---

## 🔑 默认账号

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | admin123 | 超级管理员 |

> 其他角色账号（teacher / CollegeAudit / SchoolAudit）需通过系统管理中的用户管理手动创建。

---

## 🎨 UI 设计

系统采用 **学术深邃风** 主题设计：

- 🌊 深海军蓝侧边栏（`#0f172a`）
- 🔵 学术蓝主色调（`#1890ff`）
- 🟡 琥珀金强调色（`#d4a853`）
- 📐 统一微圆角体系（8px / 12px / 16px）
- ✨ 悬浮上浮动效（`transform: translateY(-2px)`）
- 🔵 侧边栏渐变竖条激活态

---

## 📄 License

MIT License

---

<p align="center">
  <strong>高校教学成果管理系统</strong><br>
  <sub>Built with ❤️ using Spring Cloud & Vue.js</sub>
</p>
