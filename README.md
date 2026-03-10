# 🎓 高校教学成果管理系统 (Educational Achievement Management System)

基于 Spring Cloud Alibaba 微服务架构构建的高校教学成果全生命周期管理平台。

本项目致力于解决高校内部教学成果（科研、教材、竞赛、教改等）申报流程繁琐、跨部门审核不透明、数据孤岛等痛点。通过分布式架构与无纸化审批流，实现教学成果的标准化采集、多级智能化审核、数字化存储与多维可视化分析。

---

## 🚀 核心技术栈

本项目采用标准的前后端分离与微服务架构。

### 后端 (Backend)
* **核心框架**: Spring Boot 2.7 + Spring Cloud 2021.0.9
* **微服务生态**: Spring Cloud Alibaba 2021.0.6 (Nacos 注册/配置中心)
* **API 网关**: Spring Cloud Gateway
* **安全鉴权**: 基于 JWT 的分布式统一 OAuth2 认证 (自定义 `edu-auth`)
* **持久层**: MyBatis + PageHelper
* **分布式事务**: Seata (保障审批流转数据一致性)
* **对象存储**: MinIO 分布式文件系统 (承载大容量成果证明材料)

### 前端 (Frontend)
* **核心框架**: Vue 2.6.12 + Vue Router + Vuex
* **UI 组件库**: Element UI
* **数据可视化**: ECharts (构建多维成果贡献度看板)
* **构建工具**: Webpack / pnpm

### 中间件 & 环境 (Infrastructure)
* **数据库**: MySQL 8.0
* **缓存**: Redis 3.2 (热点字典数据、Token 管理)
* **运行环境**: JDK 1.8+, Node.js (v16+ recommended), Docker

---

## 🧩 系统架构与模块划分

系统遵循高内聚低耦合的原则，核心目录结构如下：

```text
edu-achievement
├── edu-ui              // 接入层：Vue 响应式前端门户
├── edu-gateway         // 网关层：微服务动态路由、跨域与限流控制 [8080]
├── edu-auth            // 认证层：统一登录中心与 Token 签发 [9200]
├── edu-api             // 接口层：通用 Feign 接口与微服务间通信定义
├── edu-common          // 公共支撑层
│   ├── common-core       // 核心基础工具与实体
│   ├── common-security   // 细粒度角色权限拦截 (RBAC)
│   ├── common-sensitive  // 隐私数据脱敏组件 (AOP + 注解实现)
│   ├── common-redis      // 分布式缓存组件
│   └── common-seata      // 分布式事务组件
├── edu-modules         // 核心业务层
│   ├── edu-system        // 系统基础支撑模块 (部门/字典/权限配置)
│   ├── edu-file          // 对象存储微服务 (MinIO 附件管理)
│   └── edu-achievement   // 🎓 教学成果核心业务微服务 (申报与多级审核状态机)
✨ 核心业务功能
👨‍🏫 教师自助申报门户

支持多类型成果在线申报与数据异步校验。

基于 MinIO 的证明材料切片上传与在线回显预览。

申报进度实时追踪追踪与历史电子档案归档。

🛡️ 动态流转审批中枢

引入严格的状态机模型控制审批流 (0草稿 -> 1待院审 -> 2校审中 -> 3已通过/4已驳回)。

支持多级协同审核，支持审核意见批注与驳回重修。

全生命周期审核日志 (AuditLog) 追溯。

📊 多维数据可视化大屏

通过聚合查询，按学院、按月份、按成果类型生成统计图表。

辅助校级管理层进行教学质量评估与决策。

🔒 细粒度安全与权限控制 (RBAC)

实现“数据行级与列级隔离”：教师仅见个人成果，院级专家管理本院数据，校级管理员纵览全局。

提供基于 @Sensitive 注解的敏感数据（如教师手机号、身份证）自动化脱敏输出，保障数据合规。

🛠️ 本地开发与运行指南
1. 环境准备
确保本地已安装并启动以下基础服务：

Nacos (2.2.3)

Redis

MySQL (8.0+)

MinIO (可选，默认可回退使用本地存储)

2. 数据库初始化
建立数据库 edu_config，导入 sql/edu_config.sql（Nacos 配置）。

建立数据库 edu_system，导入 sql/edu_system.sql（系统基础表）。

导入 sql/data/edu_achievement.sql（教学成果核心业务表）。

3. 后端启动
启动 edu-gateway (网关模块)。

启动 edu-auth (认证模块)。

启动 edu-modules-system (系统基础模块)。

启动 edu-modules-achievement (教学成果业务模块)。

4. 前端启动
Bash

# 进入前端目录
cd edu-ui

# 安装依赖 (推荐使用 pnpm 或 npm)
npm install

# 启动开发服务器
npm run dev
打开浏览器访问 http://localhost:80 即可进入系统。默认超管账号：admin / admin123。

📄 许可证 (License)
本项目仅供学术交流与毕业设计参考使用。遵循 MIT License 开源协议。

Designed & Developed by [你的名字/张鹏展] - 郑州轻工业大学软件工程