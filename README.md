# 高校教学成果管理系统

本项目是一个面向高校教学成果申报、审核、归档与统计分析的管理系统。系统采用 Spring Cloud Alibaba 微服务架构，前端基于 Vue 2 和 Element UI 实现，支持教师前台申报、学校审核、管理员后台管理、新闻公告展示和数据统计。

## 项目定位

系统围绕教学成果管理的主要业务流程建设：

- 教师在线填报教学成果，上传佐证材料。
- 学校审核人员对申报成果进行审核、驳回或通过。
- 教师查看个人申报记录、审核进度和归档状态。
- 管理端维护用户、角色、部门、字典、菜单、通知等基础数据。
- 前台门户展示首页、新闻动态、通知公告、成果申报入口和个人工作台。

## 技术栈

后端：

- Java 8
- Spring Boot 2.7.x
- Spring Cloud 2021.x
- Spring Cloud Alibaba
- MyBatis
- Redis
- MySQL
- Nacos
- Maven

前端：

- Vue 2
- Vue Router
- Vuex
- Element UI
- ECharts
- Axios
- Vue CLI 4

## 目录结构

```text
edu-achievement-master
├── edu-api                  # Feign 接口定义
├── edu-auth                 # 认证服务
├── edu-common               # 公共能力模块
├── edu-gateway              # 网关服务
├── edu-modules              # 业务微服务
│   ├── edu-achievement      # 教学成果、审核、门户新闻等业务
│   ├── edu-file             # 文件服务
│   ├── edu-job              # 定时任务
│   └── edu-system           # 系统管理
├── edu-ui                   # Vue 前端
├── docker                   # Docker 部署相关文件
├── sql                      # 数据库初始化脚本
└── pom.xml                  # Maven 聚合工程
```

## 核心模块

| 模块 | 说明 | 默认端口 |
| --- | --- | --- |
| edu-gateway | 统一网关入口 | 8080 |
| edu-auth | 登录认证、Token 管理 | 9200 |
| edu-system | 用户、角色、菜单、部门、字典、通知 | 9201 |
| edu-achievement | 教学成果申报、审核、统计、门户新闻 | 9205 |
| edu-file | 文件上传下载 | 9300 |
| edu-job | 定时任务 | 9203 |
| edu-ui | 前端项目 | 80 |

## 环境要求

建议使用以下环境：

- JDK 1.8
- Maven 3.6+
- Node.js 14 或 16
- MySQL 8
- Redis
- Nacos 2.2.3

Docker 部署场景下，项目也提供了 `docker` 目录用于容器化启动。

## 数据库初始化

按顺序执行以下 SQL：

```bash
mysql -u root -p < sql/data/edu_system.sql
mysql -u root -p < sql/data/edu_config.sql
mysql -u root -p < sql/quartz.sql
```

说明：

- `edu_system.sql`：系统基础表、业务表、菜单、角色、字典及初始化数据。
- `edu_config.sql`：Nacos 配置数据。
- `quartz.sql`：定时任务相关表。

## 后端启动

先启动 Nacos、MySQL、Redis，然后按顺序启动服务：

```bash
mvn spring-boot:run -pl edu-gateway
mvn spring-boot:run -pl edu-auth
mvn spring-boot:run -pl edu-modules/edu-system
mvn spring-boot:run -pl edu-modules/edu-achievement
mvn spring-boot:run -pl edu-modules/edu-file
```

如需启动定时任务服务：

```bash
mvn spring-boot:run -pl edu-modules/edu-job
```

## 前端启动

进入前端目录：

```bash
cd edu-ui
npm install
npm run dev
```

默认开发服务端口为 `80`，接口通过 `/dev-api` 代理到网关。

生产构建：

```bash
cd edu-ui
npm run build:prod
```

构建产物输出到：

```text
edu-ui/edu-admin
```

## 访问入口

前台门户：

```text
http://localhost/
```

登录页：

```text
http://localhost/login
```

后端网关：

```text
http://localhost:8080
```

## 默认账号

管理员账号：

```text
admin / admin123
```

具体教师、审核员账号以初始化 SQL 或系统管理后台维护的数据为准。

## 角色与权限

| 角色 | 角色标识 | 主要入口 |
| --- | --- | --- |
| 管理员 | admin | 后台管理、全部权限 |
| 教师 | teacher | 成果申报、我的成果 |
| 学校审核员 | SchoolAudit | 审核工作台、审核档案 |

系统通过网关鉴权、Redis Token、后端权限注解和角色菜单共同控制访问权限。

## 教学成果状态流转

成果状态主要包括：

| 状态值 | 含义 |
| --- | --- |
| 0 | 草稿 |
| 2 | 审核中 |
| 3 | 已通过 |
| 4 | 已驳回 |

基本流程：

```text
草稿 -> 提交审核 -> 审核中 -> 已通过
                      └-> 已驳回 -> 重新提交 -> 审核中
```

已废弃状态：

- `1`：旧版学院审核状态
- `5`：旧版退回修改状态

新业务流程中不应继续使用废弃状态。

## 前台功能

前台页面主要包括：

- 首页：展示门户导航、新闻、通知、服务入口。
- 新闻动态：查看新闻列表和详情。
- 通知公告：查看公告类信息。
- 成果申报：教师填写成果信息、上传附件并提交审核。
- 我的成果：教师查看申报记录、状态和审核进度。
- 审核工作台：审核人员处理待审成果，支持连续审核。
- 审核档案：查看历史审核记录。
- 个人中心：维护个人资料、头像和密码。

## 后台功能

后台管理主要包括：

- 用户管理
- 角色管理
- 菜单管理
- 部门管理
- 岗位管理
- 字典管理
- 参数配置
- 通知公告
- 日志管理
- 文件管理
- 教学成果管理
- 审核记录管理

## 常用构建命令

后端编译：

```bash
mvn clean package -DskipTests
```

后端跳过测试编译：

```bash
mvn -DskipTests compile
```

前端开发：

```bash
cd edu-ui
npm run dev
```

前端生产构建：

```bash
cd edu-ui
npm run build:prod
```

## Docker 启动

项目提供 Docker 相关配置，可按需使用：

```bash
sh docker/copy.sh
cd docker
docker-compose up -d
```

Docker 环境会启动数据库、Redis、Nacos、MinIO、Nginx 和各后端服务。实际端口、账号和挂载目录请以 `docker` 目录内配置为准。

## 开发注意事项

- 前端请求统一通过网关转发。
- 后端业务服务依赖网关传递的用户上下文。
- 修改后端服务代码后通常需要重启对应服务。
- 修改前端页面后，开发环境支持热更新。
- 生产构建后需要部署 `edu-ui/edu-admin` 目录。
- 数据字典、菜单权限和角色权限需要保持一致。
- 教师端接口不得信任客户端传入的审核状态，应由后端根据动作强制设置合法状态。
- 审核接口应校验审核结果，只允许合法审核值。

## 推荐启动顺序

```text
1. MySQL
2. Redis
3. Nacos
4. edu-gateway
5. edu-auth
6. edu-system
7. edu-achievement
8. edu-file
9. edu-ui
```

## 项目说明

本系统适用于高校教学成果申报与审核场景，重点关注流程规范、权限控制、审核留痕和前台门户展示。二次开发时建议优先复用现有模块、字典、权限和 API 结构，避免重复建设相近功能。
