<h1 align="center">🎓 高校教学成果管理系统</h1>

<p align="center">
  <em>基于 Spring Cloud 微服务架构的教学成果全生命周期管理平台</em>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Spring%20Cloud-2021.0.9-blue" alt="Spring Cloud">
  <img src="https://img.shields.io/badge/Vue.js-2.x-42b883" alt="Vue.js">
  <img src="https://img.shields.io/badge/Element%20UI-2.x-409eff" alt="Element UI">
  <img src="https://img.shields.io/badge/MySQL-5.7-4479a1" alt="MySQL">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License">
</p>

---

## 📋 项目简介

本系统为高校教学成果申报、审核、管理提供一站式解决方案。教师可在线提交成果（论文、教材、竞赛、教改项目），经 **院级审核 → 校级审核** 两级审批后归档管理。系统提供 **ECharts 数据仪表盘**、**Excel 导出**、**站内通知** 等功能，以现代化 iOS 风格 UI 呈现。

### ✨ 核心特性

- 🔄 **两级审核流程** — 院级 → 校级，支持通过 / 驳回 / 退回修改
- 📊 **ECharts 数据大屏** — 成果类型饼图、状态柱状图、通过率仪表盘、学院分布图
- 👤 **角色差异化展示** — 管理员 / 审核员 / 教师 看到不同的统计面板
- 📩 **站内通知** — 审核完成后自动通知教师
- 📥 **Excel 导出** — 支持中文状态映射和教师姓名导出
- 🎨 **现代化 UI** — 毛玻璃导航栏、弥散阴影、微圆角、悬浮动画

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
          ┌────▼────┐    ┌─────▼─────┐    ┌────▼────┐
          │  Auth   │    │  System   │    │Achievement│
          │  :9200  │    │  :9201    │    │  :9205   │
          └────┬────┘    └─────┬─────┘    └────┬─────┘
               │               │               │
          ┌────▼───────────────▼───────────────▼────┐
          │              MySQL + Redis               │
          │              Nacos 配置中心               │
          └─────────────────────────────────────────┘
```

---

## 📂 项目结构

```
edu-achievement-master/
├── edu-gateway          # API 网关（路由转发、XSS 过滤）
├── edu-auth             # 认证授权中心（JWT 令牌）
├── edu-api              # 远程调用 Feign 接口
├── edu-common           # 公共库（9 个子模块）
│   ├── core             #   核心工具（AjaxResult、Excel、异常处理）
│   ├── security         #   安全框架（@RequiresPermissions）
│   ├── redis            #   Redis 缓存封装
│   ├── log              #   @Log 操作日志切面
│   ├── datascope        #   数据权限 SQL 拦截
│   ├── datasource       #   多数据源切换
│   ├── seata            #   分布式事务
│   ├── swagger          #   API 文档
│   └── sensitive        #   数据脱敏
├── edu-modules          # 业务微服务
│   ├── edu-achievement  #   ★ 教学成果管理（核心业务）
│   ├── edu-system       #   系统管理（用户/角色/菜单/部门）
│   ├── edu-file         #   文件服务
│   ├── edu-gen          #   代码生成器
│   └── edu-job          #   定时任务
├── edu-visual           # 服务监控（Spring Boot Admin）
├── edu-ui               # Vue.js 前端
├── sql                  # 数据库脚本
└── docker               # Docker Compose 部署
```

---

## 🔧 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| **后端框架** | Spring Boot | 2.7.18 |
| **微服务** | Spring Cloud + Alibaba | 2021.0.9 / 2021.0.6.1 |
| **注册中心/配置** | Nacos | 2.x |
| **持久层** | MyBatis + PageHelper | 2.0.0 |
| **数据库** | MySQL | 5.7 |
| **缓存** | Redis | latest |
| **认证** | JWT + sa-token | 0.9.1 |
| **前端** | Vue.js 2 + Element UI | 2.x |
| **图表** | ECharts | 5.x |
| **文件存储** | 本地 / MinIO / FastDFS | - |
| **部署** | Docker Compose + Nginx | - |

---

## 🚀 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis 6.0+
- Nacos 2.x
- Node.js 16+ (前端)

### 后端启动

```bash
# 1. 克隆项目
git clone https://github.com/your-repo/edu-achievement.git

# 2. 导入数据库
mysql -u root -p < sql/edu_system.sql
mysql -u root -p < sql/edu_config.sql
mysql -u root -p < sql/quartz.sql
mysql -u root -p < sql/data/edu_achievement.sql
mysql -u root -p < sql/newsql/edu_audit_record.sql
mysql -u root -p < sql/newsql/sys_menu_audit.sql

# 3. 启动 Nacos
startup.cmd -m standalone

# 4. 依次启动微服务
# 顺序：Gateway → Auth → System → Achievement → File
mvn spring-boot:run -pl edu-gateway
mvn spring-boot:run -pl edu-auth
mvn spring-boot:run -pl edu-modules/edu-system
mvn spring-boot:run -pl edu-modules/edu-achievement
mvn spring-boot:run -pl edu-modules/edu-file
```

### 前端启动

```bash
cd edu-ui

# 安装依赖
npm install

# 开发模式启动
npm run dev

# 生产构建
npm run build:prod
```

### Docker 部署

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

### 成果管理

| 功能 | 教师 | 审核员 | 管理员 |
|------|:----:|:------:|:------:|
| 申报成果 | ✅ | - | - |
| 查看自己的成果 | ✅ | - | ✅ |
| 编辑/删除自己的成果 | ✅ | - | ✅ |
| 院级审核 | - | ✅ | ✅ |
| 校级审核 | - | ✅ | ✅ |
| 查看审核记录 | - | ✅(自己的) | ✅(全部) |
| Excel 导出 | - | - | ✅ |
| 数据统计仪表盘 | ✅ | ✅ | ✅ |

### 审核流程

```
教师提交 → status=1 (院审中)
    │
    ├─ 院级通过 → status=2 (校审中)
    │       │
    │       ├─ 校级通过 → status=3 ✅ (最终通过)
    │       ├─ 校级驳回 → status=4 ❌ (已驳回)
    │       └─ 校级退回 → status=5 🔄 (退回修改)
    │
    ├─ 院级驳回 → status=4 ❌ (已驳回)
    └─ 院级退回 → status=5 🔄 (退回修改)
                       │
                       └─ 教师重新编辑提交 → status=1 (重新开始)
```

### 系统管理

- 用户管理 / 角色管理 / 菜单管理
- 部门管理（学院层级）
- 字典管理 / 参数设置
- 通知公告 / 操作日志 / 登录日志
- 在线用户 / 服务监控

---

## 📊 数据仪表盘

Dashboard 根据登录角色展示不同数据：

- **管理员**：全校总成果、已通过、审核中、已驳回/退回
- **审核员**：待审核、已通过、退回修改、已驳回
- **教师**：我的申报、审核中、已通过、退回修改

配合 4 个 ECharts 图表：
1. 📊 成果类型分布（环形饼图）
2. 📈 审核状态统计（柱状图）
3. 🎯 审核通过率（仪表盘）
4. 🏛️ 各学院成果数量（横向柱状图）

---

## 🎨 UI 设计

系统采用 **iOS 风格现代化设计**：

- 🧊 毛玻璃导航栏（`backdrop-filter: blur(20px)`）
- 🌫️ 弥散阴影卡片（`box-shadow: 0 8px 30px rgba(0,0,0,0.04)`）
- 📐 统一微圆角体系（8px / 12px / 16px）
- ✨ 悬浮上浮动效（`transform: translateY(-4px)`）
- 🔵 侧边栏渐变竖条激活态
- 🍔 CSS 动画汉堡菜单图标

---

## 📁 数据库

| 核心表 | 说明 |
|--------|------|
| `edu_achievement` | 教学成果主表（标题、内容、类型、状态、教师ID、学院ID） |
| `edu_audit_record` | 审核记录（审核级别、结果、意见、审核人） |
| `sys_user` | 用户表（教师/管理员/审核员） |
| `sys_dept` | 部门表（学院） |
| `sys_menu` | 菜单权限表 |
| `sys_role` | 角色表 |
| `sys_notice` | 站内通知表 |

---

## 🔑 默认账号

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | admin123 | 超级管理员 |

> 其他角色账号需通过系统管理中的用户管理手动创建。

---

## 📄 License

MIT License

---

<p align="center">
  <strong>ZZULI · 高校教学成果管理系统</strong><br>
  <sub>Built with ❤️ using Spring Cloud & Vue.js</sub>
</p>
