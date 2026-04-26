<h1 align="center">🎓 高校教学成果管理系统</h1>

<p align="center">
  <em>基于 Spring-Cloud 微服务架构的教学成果全生命周期管理平台</em>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/JDK-1.8-orange" alt="JDK">
  <img src="https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Spring%20Cloud-2021.0.9-blue" alt="Spring Cloud">
  <img src="https://img.shields.io/badge/Spring%20Cloud%20Alibaba-2021.0.6.1-cyan" alt="Spring Cloud Alibaba">
  <img src="https://img.shields.io/badge/Vue.js-2.6.12-42b883" alt="Vue.js">
  <img src="https://img.shields.io/badge/Element%20UI-2.15.14-409eff" alt="Element UI">
  <img src="https://img.shields.io/badge/MySQL-8-4479a1" alt="MySQL">
  <img src="https://img.shields.io/badge/Redis-3.2.100-dc382d" alt="Redis">
</p>

---

## 📋 项目简介

本系统为高校教学成果申报、审核、管理提供一站式解决方案。教师可在线提交成果（论文、教材、竞赛、教改项目等），经 **审核** 后归档管理。系统提供前台门户（新闻公告、快捷申报入口）与后台管理双界面，配备 **ECharts 数据仪表盘**、**Excel 导出**、**站内通知** 等功能。

### ✨ 核心特性

- 🔄 **审核流程** — 支持通过 / 驳回
- 🏠 **前台门户系统** — 游客可浏览新闻公告，登录后按角色自动跳转
- 📊 **ECharts 数据大屏** — 成果类型饼图、状态柱状图、审核通过率仪表盘
- 👤 **角色差异化路由** — 教师/审核员进前台，管理员进后台
- 📩 **站内通知** — 审核完成后自动通知教师
- 📥 **Excel 导出** — 支持中文状态映射和教师姓名导出（Apache POI 4.1.2）
- 📰 **新闻管理** — 支持富文本编辑、封面图上传、门户发布
- 🎨 **现代化 UI** — 学术深邃风主题，深海军蓝侧边栏 + 琥珀金强调色
- 🛡️ **数据权限** — 基于 MyBatis 拦截器的行级数据范围控制
- 📝 **操作日志** — 基于切面的全链路操作日志记录
- 🔀 **多数据源** — 基于 Dynamic-DS 的运行时动态数据源切换

---

## 🏗️ 系统架构

```
                          ┌──────────┐
                          │  Nginx   │
                          │  :80     │
                          └────┬─────┘
                               │
                          ┌────▼─────┐
                          │ Gateway  │   edu-gateway
                          │  :8080   │   Spring Cloud Gateway
                          └────┬─────┘
                               │
          ┌────────────────────┼──────────────────────┐
          │                    │                      │
     ┌────▼────┐         ┌─────▼─────┐         ┌──────▼──────┐
     │  Auth   │         │  System   │         │ Achievement │
     │  :9200  │         │  :9201    │         │   :9205     │
     │  edu-   │         │  edu-     │         │   edu-      │
     │  auth   │         │  modules- │         │   modules-  │
     │         │         │  system   │         │ achievement │
     └────┬────┘         └─────┬─────┘         └──────┬──────┘
          │                    │                      │
     ┌────▼────┐         ┌─────▼─────┐         ┌──────▼──────┐
     │ Monitor │         │   File    │         │   Job/Gen   │
     │  :9100  │         │   :9300   │         │             │
     │  edu-   │         │  edu-     │         │  edu-       │
     │  monitor│         │  modules- │         │  modules-   │
     │         │         │  file     │         │  job / gen  │
     └────┬────┘         └─────┬─────┘         └──────┬──────┘
          │                    │                      │
          └────────────────────┼──────────────────────┘
                               │
               ┌───────────────┼───────────────┐
          ┌────▼────┐    ┌─────▼─────┐    ┌────▼────┐
          │  MySQL  │    │   Redis   │    │  Nacos  │
          │    8    │    │  3.2.100  │    │  2.2.3  │
          └─────────┘    └───────────┘    └─────────┘
```

### 服务依赖关系

```
外部请求 → Gateway (8080) → 路由转发 → 各微服务

edu-gateway          API 网关（路由、鉴权、限流）
edu-auth             认证授权中心（登录、Token 签发/刷新）
edu-modules-system   系统管理（用户/角色/部门/菜单/字典/岗位等）
edu-modules-achievement  教学成果管理（申报、审核、统计、新闻）
edu-modules-file     文件服务（上传、下载）
edu-modules-job      定时任务（Quartz 调度）
edu-modules-gen      代码生成器（Velocity 模板）
edu-visual-monitor   Spring Boot Admin 监控中心
```

---

## 📂 项目结构

```
edu-achievement-master/
├── pom.xml                        # 父工程 POM（版本统一管理）
├── edu-gateway/                   # API 网关
│   └── Spring Cloud Gateway（路由、跨域、Sentinel 限流）
├── edu-auth/                      # 认证授权中心
│   ├── controller/TokenController #   登录/注册/Token刷新
│   ├── form/LoginBody             #   登录表单
│   └── service/SysLoginService    #   认证逻辑
├── edu-api/                       # 远程调用 Feign 接口层
│   └── edu-api-system/            #   系统模块 Feign 接口定义
├── edu-common/                    # 公共组件库（9 个子模块）
│   ├── edu-common-core            #   核心工具（BaseEntity、AjaxResult、Excel、Web 工具、Feign 配置）
│   ├── edu-common-redis           #   Redis 缓存封装（统一序列化、Key 管理）
│   ├── edu-common-security        #   安全框架（@RequiresPermissions、HeaderInterceptor、Token 校验）
│   ├── edu-common-log             #   操作日志（@Log 注解 + AOP 切面）
│   ├── edu-common-datascope       #   数据权限（MyBatis 拦截器行级过滤）
│   ├── edu-common-datasource      #   多数据源（Alibaba Druid + Dynamic-DS）
│   ├── edu-common-swagger         #   API 文档（SpringDoc OpenAPI）
│   ├── edu-common-seata           #   分布式事务（Seata 集成配置）
│   └── edu-common-sensitive       #   数据脱敏（手机号/身份证等字段脱敏）
├── edu-modules/                   # 业务微服务
│   ├── edu-achievement/           #   ★ 教学成果管理（核心业务）
│   │   ├── controller/            #     EduAchievement / EduAuditRecord / EduNews / PortalNews
│   │   ├── domain/                #     EduAchievement / EduAuditRecord / EduNews
│   │   ├── service/               #     IEduAchievementService 等接口 + impl 实现
│   │   └── mapper/                #     MyBatis 接口 + XML SQL 映射
│   ├── edu-system/                #   系统管理（用户/角色/菜单/部门/字典/岗位/日志）
│   ├── edu-file/                  #   文件服务（本地存储 / MinIO / FastDFS）
│   ├── edu-gen/                   #   代码生成器（Velocity 模板引擎）
│   └── edu-job/                   #   定时任务（Quartz 调度）
├── edu-visual/                    # 可视化监控
│   └── edu-monitor/               #   Spring Boot Admin 监控服务端
├── edu-ui/                        # Vue.js 前端
│   ├── src/api/                   #   后端 API 接口封装
│   ├── src/views/                 #   页面组件
│   ├── src/router/                #   路由配置
│   ├── src/store/                 #   Vuex 状态管理
│   └── src/components/            #   公共组件
├── sql/                           # 数据库脚本
│   ├── data/edu_system.sql        #   系统核心表（用户/角色/菜单/部门）
│   ├── data/edu_config.sql        #   Nacos 配置数据
│   ├── quartz.sql                 #   Quartz 定时任务表
│   └── ry_seata_20210128.sql      #   Seata 分布式事务 undo_log 表
├── docker/                        # Docker Compose 部署
│   ├── docker-compose.yml         #   容器编排文件
│   ├── copy.sh / deploy.sh        #   部署辅助脚本
│   ├── mysql/redis/nacos/nginx/   #   各中间件镜像与配置
│   └── edu/                       #   微服务 jar 与 Dockerfile
└── zMD/                           # 开发文档与参考资料
```

### edu-common 内部依赖关系

```
edu-common-core                     （基础层，无内部依赖）
  │
  ├──► edu-common-redis             （依赖 core）
  │      └──► edu-common-security   （依赖 redis + edu-api-system Feign）
  │             ├──► edu-common-log       （依赖 security）
  │             └──► edu-common-datascope （依赖 security）
  │
  ├──► edu-common-sensitive         （依赖 core）
  ├──► edu-common-swagger           （独立，无内部依赖）
  ├──► edu-common-datasource        （独立，无内部依赖）
  └──► edu-common-seata             （独立，无内部依赖）
```

---

## 🔧 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| **运行环境** | JDK | 1.8 |
| **基础框架** | Spring Boot | 2.7.18 |
| **微服务框架** | Spring Cloud | 2021.0.9 |
| **微服务(阿里)** | Spring Cloud Alibaba | 2021.0.6.1 |
| **注册/配置中心** | Nacos | 2.2.3 |
| **网关** | Spring Cloud Gateway | 3.1.x |
| **限流/熔断** | Sentinel | — |
| **持久层** | MyBatis + PageHelper | 5.3.x / 2.0.0 |
| **连接池** | Alibaba Druid | 1.2.27 |
| **多数据源** | Dynamic-DS (baomidou) | 4.3.1 |
| **数据库** | MySQL | 8.x |
| **缓存** | Redis | 3.2.100 |
| **认证** | JWT (jjwt) | 0.9.1 |
| **文件存储** | FastDFS / MinIO | 8.2.2 |
| **Excel** | Apache POI | 4.1.2 |
| **验证码** | Kaptcha | 2.3.3 |
| **API 文档** | SpringDoc OpenAPI | 1.6.9 |
| **分布式事务** | Seata | — |
| **监控** | Spring Boot Admin | 2.7.16 |
| **前端框架** | Vue.js | 2.6.12 |
| **UI 组件库** | Element UI | 2.15.14 |
| **图表** | ECharts | 5.4.0 |
| **构建部署** | Maven + Docker Compose + Nginx | — |

---

## 🚀 快速开始

### 环境要求

| 工具 | 版本 | 说明 |
|------|------|------|
| JDK | 1.8+ | 运行 Java 字节码 |
| Maven | 3.6+ | 项目构建与依赖管理 |
| MySQL | 8.x | 主数据库 |
| Redis | 3.2.100+ | 缓存与 Token 存储 |
| Nacos | 2.2.3 | 服务注册与配置中心 |
| Node.js | 16+ | 前端开发与构建 |

### 1. 初始化环境

```bash
git clone <repository-url>
cd edu-achievement-master
```

### 2. 导入数据库

按顺序执行以下 SQL 文件：

```bash
# 系统核心表（用户/角色/菜单/部门等）
mysql -u root -p < sql/data/edu_system.sql

# Nacos 配置数据
mysql -u root -p < sql/data/edu_config.sql

# Quartz 定时任务表
mysql -u root -p < sql/quartz.sql
```

### 3. 启动 Nacos

```bash
# Windows 单机模式
startup.cmd -m standalone

# Linux / Mac 单机模式
sh startup.sh -m standalone
```

启动后访问 http://localhost:8848/nacos ，使用默认账号 nacos/nacos 登录，确认服务已注册。

### 4. 启动后端微服务

建议按以下顺序启动：

```bash
# 1. 网关（最先启动，接受外部请求）
mvn spring-boot:run -pl edu-gateway

# 2. 认证中心
mvn spring-boot:run -pl edu-auth

# 3. 系统管理（提供用户/角色/菜单数据）
mvn spring-boot:run -pl edu-modules/edu-system

# 4. 教学成果（核心业务）
mvn spring-boot:run -pl edu-modules/edu-achievement

# 5. 文件服务
mvn spring-boot:run -pl edu-modules/edu-file

# 6. 监控中心（可选）
mvn spring-boot:run -pl edu-visual/edu-monitor
```

> 也可使用 IDE（IntelliJ IDEA / Eclipse）导入项目后逐个启动各模块的 `*Application.java` 主类。

### 5. 启动前端

```bash
cd edu-ui

# 安装依赖
npm install

# 开发模式启动（热更新）
npm run dev

# 生产构建
npm run build:prod
```

### 6. Docker 一键部署（可选）

```bash
# 构建所有模块
mvn clean package -DskipTests

# 复制 JAR 到 Docker 目录
sh docker/copy.sh

# 启动所有容器
cd docker
docker-compose up -d
```

---

## 📱 功能模块

### 前台门户（教师/审核员/游客）

| 页面 | 路由 | 功能说明 | 权限 |
|------|------|---------|------|
| 首页门户 | `/portal/home` | 新闻公告、数据概览、快捷入口 | 所有用户 |
| 新闻动态 | `/portal/news` | 新闻列表、详情查看 | 所有用户 |
| 教师申报 | `/portal/declare` | 提交教学成果（论文/教材/竞赛/教改） | 教师 |
| 我的申报 | `/portal/mine` | 查看/编辑/撤回/重新提交自己的成果 | 教师 |

### 后台管理（管理员/审核员）

| 页面 | 路由 | 功能说明 | 权限 |
|------|------|---------|------|
| 数据仪表盘 | `/admin` | ECharts 统计：类型饼图、状态柱状图、学院排名、通过率 | 按角色过滤数据 |
| 成果管理 | `/achievement/achievement` | 全部成果列表、搜索、Excel 导出、删除 | 管理员 |
| 校级审核 | `/achievement/schoolAudit` | 审核员审批：通过/驳回 | 校级审核员 / 管理员 |
| 审核记录 | `/achievement/auditRecord` | 审核日志查询、Excel 导出 | 按角色过滤 |
| 新闻管理 | `/achievement/news` | 新闻增删改查、封面图上传、发布/停止 | 管理员 |
| 系统管理 | `/system/*` | 用户/角色/菜单/部门/岗位/字典/日志管理 | 管理员 |

### 权限矩阵

| 功能 | 教师 | 校级审核员 | 管理员 |
|------|:----:|:---------:|:------:|
| 申报成果 | ✅ | — | — |
| 查看/编辑自己的成果 | ✅ | — | ✅ |
| 校级审核 | — | ✅ | ✅ |
| 查看审核记录 | — | ✅(自己的) | ✅(全部) |
| 数据统计 | ✅(个人) | ✅(全校) | ✅(全部) |
| Excel 导出 | — | — | ✅ |
| 新闻管理 | — | — | ✅ |
| 系统管理 | — | — | ✅ |

---

## 🔄 审核流程

```
教师提交 → 草稿(0) ──→ 审核中(2) ──→ 已通过(3) ✅
                        │
                        ├── 驳回(4) → 教师编辑 → 审核中(2)
```

| 状态码 | 状态名称 | 可执行操作 |
|--------|---------|-----------|
| 0 | 草稿 | 教师编辑、提交 |
| 2 | 审核中 | 审核员：通过 / 驳回 |
| 3 | 已通过 | 归档（终态，不可变更） |
| 4 | 已驳回 | 教师可重新提交 |

---

## 📊 数据仪表盘

Dashboard 根据登录角色展示不同维度的统计数据：

| 角色 | 展示维度 |
|------|---------|
| **管理员** | 全校总成果、已通过、审核中、已驳回/退回、各学院排名 |
| **校级审核员** | 全校待审核、已通过、退回修改、已驳回 |
| **教师** | 我的申报总数、审核中、已通过、退回修改 |

ECharts 图表组成：

1. 📊 **成果类型分布** — 环形饼图（论文 / 教材 / 竞赛 / 教改项目）
2. 📈 **审核状态统计** — 柱状图（草稿 / 审核中 / 已通过 / 已驳回）
3. 🎯 **审核通过率** — 仪表盘（已通过 / 总数 × 100%）
4. 🏛️ **各学院成果数量** — 横向柱状图（按学院分组统计）

---

## 📁 数据库

### 业务表

| 表名 | 所属模块 | 说明 |
|------|---------|------|
| `edu_achievement` | achievement | 教学成果主表（标题、内容、类型、状态、教师ID、学院ID、文件URL） |
| `edu_audit_record` | achievement | 审核记录（审核级别 1院/2校、结果、意见、审核人、审核时间） |
| `edu_news` | achievement | 新闻公告（标题、摘要、封面图、富文本内容、发布状态、浏览量） |

### 系统表

| 表名 | 所属模块 | 说明 |
|------|---------|------|
| `sys_user` | system | 用户表（账号、昵称、密码、部门、角色） |
| `sys_dept` | system | 部门表（学院层级结构，含 ancestors 字段） |
| `sys_role` | system | 角色表（admin / teacher / SchoolAudit） |
| `sys_menu` | system | 菜单权限表（路由、按钮权限标识） |
| `sys_notice` | system | 站内通知表（审核结果自动推送） |
| `sys_config` | system | 系统参数配置表 |
| `sys_dict_data` / `sys_dict_type` | system | 字典管理 |
| `sys_post` | system | 岗位管理 |
| `sys_oper_log` | system | 操作日志记录表 |
| `sys_logininfor` | system | 登录日志记录表 |

---

## 🔑 默认账号

| 账号 | 密码 | 角色 | 说明 |
|------|------|------|------|
| admin | admin123 | 超级管理员 | 拥有全部权限 |

> 其他角色账号（教师 / 校级审核员）需登录后通过「系统管理 → 用户管理」手动创建，并分配对应角色。

---

## 🎨 UI 设计语言

系统采用 **学术深邃风** 主题设计：

| 设计要素 | 色值 | 用途 |
|---------|------|------|
| 深海军蓝 | `#0f172a` | 侧边栏背景 |
| 学术蓝 | `#1890ff` | 主色调、链接、激活态 |
| 琥珀金 | `#d4a853` | 强调色、数据高亮、标题装饰 |
| 圆角体系 | 8px / 12px / 16px | 卡片、按钮、弹窗等组件 |
| 悬浮动效 | `translateY(-2px)` | 卡片悬浮上浮效果 |
| 侧边栏激活态 | 渐变竖条 | 菜单选中标识 |

---

## 📄 License

MIT License

---

<p align="center">
  <strong>高校教学成果管理系统</strong><br>
  <sub>Built with Spring Cloud Microservices & Vue.js</sub>
</p>
