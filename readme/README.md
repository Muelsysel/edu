# 高校教学成果管理系统

基于 RuoYi-Cloud 微服务架构的教学成果申报与审核系统。

## 核心功能

- **教师申报**: 在线填写成果信息、上传附件
- **院级审核**: 学院管理员初审
- **校级审核**: 学校管理员终审
- **状态追踪**: 草稿→院审→校审→通过/驳回

## 技术栈

- **后端**: Spring Boot 2.7.18 + Spring Cloud 2021.0.9 + MyBatis
- **前端**: Vue 2 + Element UI
- **中间件**: Nacos + Redis + MinIO
- **数据库**: MySQL 8.0

## 快速部署

### 1. 执行 SQL

```sql
source sql/newsql/edu_achievement.sql;
source sql/newsql/menu_achievement.sql;
```

### 2. 启动服务

确保以下服务已启动：

- Nacos (8848)
- Redis
- MySQL
- edu-auth
- edu-file
- edu-system

### 3. 运行模块

```bash
cd edu-modules/edu-achievement
mvn clean package
java -jar target/edu-modules-achievement.jar
```

### 4. 启动前端

```bash
cd edu-ui
npm install
npm run dev
```

## 模块说明

| 模块            | 端口 | 说明             |
| --------------- | ---- | ---------------- |
| edu-auth        | 9200 | 认证服务         |
| edu-system      | 9201 | 系统服务         |
| edu-file        | 9204 | 文件服务         |
| edu-achievement | 9205 | 成果管理（新增） |

## 权限说明

| 角色       | 权限                   |
| ---------- | ---------------------- |
| 教师       | 申报成果、查看本人成果 |
| 院级审核员 | 审核本院成果           |
| 校级审核员 | 审核全校成果           |
| 管理员     | 全权限                 |

## 状态流转

```
草稿 → 院级审核中 → 校级审核中 → 已通过
              ↓            ↓
            已驳回 ←───────┘
```

## 文档

- [修复记录](../FIX_RECORD.md)
- [实施计划](../.trae/documents/edu-achievement-implementation-plan.md)

---

**版本**: v1.0.0  
**完成时间**: 2026-03-03
