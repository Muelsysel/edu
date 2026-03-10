# 🎓 高校教学成果管理系统 (Educational Achievement Management System)

基于 Spring Cloud Alibaba 微服务架构构建的高校教学成果全生命周期管理平台。

本项目致力于解决高校内部教学成果（科研、教材、竞赛、教改等）申报流程繁琐、跨部门审核不透明、数据孤岛等痛点。通过分布式架构与无纸化审批流，实现教学成果的标准化采集、多级智能化审核、数字化存储与多维可视化分析。

---

## 🚀 核心技术栈

本项目采用标准的前后端分离与微服务架构，底层全面完成“去框架化”与工程级定制。

### 📦 后端 (Backend)
* **核心框架**: Spring Boot 2.7.18 + Spring Cloud 2021.0.9
* **微服务生态**: Spring Cloud Alibaba 2021.0.6.1 (Nacos 2.2.3 注册/配置中心)
* **API 网关**: Spring Cloud Gateway
* **安全鉴权**: 基于 JWT 的分布式统一认证 (自定义 `edu-auth` 模块)
* **持久层**: MyBatis + PageHelper Boot 2.0.0 + Druid 1.2.27
* **分布式事务**: Seata (保障跨服务状态流转数据一致性)
* **对象存储**: MinIO 分布式文件系统 (承载大容量成果证明材料上传与预览)

### 💻 前端 (Frontend)
* **核心框架**: Vue 2.6.12 + Vue Router + Vuex
* **UI 组件库**: Element UI
* **数据可视化**: ECharts (构建多维成果贡献度及趋势看板)
* **包管理工具**: Node.js (npm 20.18.0 / pnpm 10.28.0)

### 🛠️ 中间件 & 环境 (Infrastructure)
* **数据库**: MySQL 8.0
* **缓存**: Redis 3.2.100 (热点字典数据、Token 与会话管理)
* **Web 容器**: Tomcat 9.0.112
* **开发工具**: IDEA 2025.3


---
## 🧩 系统架构与模块划分

系统遵循“高内聚低耦合”的工程化原则，核心目录结构如下：

```text
edu-achievement
├── edu-ui              // 接入层：Vue 响应式前端门户 [80]
├── edu-gateway         // 网关层：微服务动态路由、跨域与限流控制 [8080]
├── edu-auth            // 认证层：统一登录中心与 Token 签发 [9200]
├── edu-api             // 接口层：通用 Feign 接口与微服务间通信定义
├── edu-common          // 公共支撑层
│   ├── common-core       // 核心基础工具与实体
│   ├── common-security   // 细粒度角色权限拦截 (RBAC)
│   ├── common-sensitive  // 隐私数据脱敏组件 (AOP + 注解实现)
│   ├── common-redis      // 分布式缓存组件
│   └── common-seata      // 分布式事务组件
├── edu-modules         // 核心业务微服务层
│   ├── edu-system        // 系统基础支撑模块 (部门/字典/权限配置)
│   ├── edu-file          // 对象存储微服务 (集成 MinIO 附件管理)
│   └── edu-achievement   // 🎓 教学成果核心业务微服务 (申报与多级审核状态机)
