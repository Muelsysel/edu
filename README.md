# 高校教学成果管理系统

基于 Spring Cloud Alibaba 微服务架构的教学成果申报、审核、归档及统计分析平台。

---

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 2.7.18 |
| 微服务 | Spring Cloud + Spring Cloud Alibaba | 2021.0.9 / 2021.0.6.1 |
| 注册配置 | Nacos | 2.2.3 |
| 认证鉴权 | JWT (jjwt) + Redis + Spring Security | — |
| ORM / 分页 | MyBatis + PageHelper | — |
| 数据库连接池 | Druid | 1.2.27 |
| 多数据源 | dynamic-datasource | 4.3.1 |
| 缓存 | Redis | — |
| 数据库 | MySQL 8 | — |
| 任务调度 | Quartz | — |
| 文件存储 | MinIO / FastDFS / 本地 | — |
| 监控 | Spring Boot Admin | 2.7.16 |
| 接口文档 | Springdoc OpenAPI | 1.6.9 |
| JSON | FastJSON2 | 2.0.60 |
| 前端框架 | Vue 2 + Vue Router + Vuex | 2.6.12 |
| UI 库 | Element UI | 2.15.14 |
| 图表 | ECharts | 5.4.0 |
| HTTP 客户端 | Axios | 0.28.1 |
| 构建工具 | Vue CLI | 4.4.6 |

---

## 项目结构

```
edu-achievement-master
├── edu-gateway/                  # API 网关 (8080)
├── edu-auth/                     # 认证服务 (9200)
├── edu-modules/
│   ├── edu-system/               # 系统管理 (9201)
│   ├── edu-achievement/          # 教学成果 (9205)
│   ├── edu-file/                 # 文件管理 (9300)
│   └── edu-job/                  # 定时任务 (9203)
├── edu-common/                   # 公共模块
│   ├── edu-common-core/          # 核心工具类
│   ├── edu-common-redis/         # Redis 序列化/配置
│   ├── edu-common-security/      # 安全组件
│   ├── edu-common-log/           # 操作日志
│   ├── edu-common-datascope/     # 数据权限 (MyBatis 行级过滤)
│   ├── edu-common-datasource/    # 多数据源支持
│   ├── edu-common-swagger/       # API 文档
│   ├── edu-common-seata/         # 分布式事务 (预留)
│   └── edu-common-sensitive/      # 脱敏处理
├── edu-api/
│   └── edu-api-system/           # Feign 远程调用接口
├── edu-visual/
│   └── edu-monitor/              # Spring Boot Admin 监控
├── edu-ui/                       # Vue 2 前端
│   ├── src/                     # 源代码
│   ├── public/                  # 静态资源
│   └── edu-admin/               # 构建产物 (Nginx 部署)
├── sql/data/                     # 数据库初始化脚本
└── bin/                         # 本地开发启动脚本
```

## 服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| `edu-gateway` | 8080 | 统一入口，鉴权过滤 |
| `edu-auth` | 9200 | 登录/登出/Token 刷新 |
| `edu-system` | 9201 | 用户/角色/菜单/部门/字典/通知 |
| `edu-achievement` | 9205 | 成果申报/审核/统计/新闻 |
| `edu-file` | 9300 | 文件上传下载 |
| `edu-job` | 9203 | 定时任务调度 |

> 所有前端请求经 Gateway 转发，Gateway 是唯一入口。

---

## 环境要求

| 组件 | 版本要求 |
|------|---------|
| JDK | 1.8 |
| Maven | 3.6+ |
| MySQL | 8.0 |
| Redis | 5+ |
| Nacos | 2.2.3 |
| Node.js | 14+ (前端) |
| npm | 6+ |

---

## 快速启动

### 1. 初始化数据库

```bash
mysql -u root -p < sql/data/edu_system.sql
mysql -u root -p < sql/data/edu_config.sql
```

### 2. 启动 Nacos

**Windows:**
```bash
startup.cmd -m standalone
```

**Linux / macOS:**
```bash
sh startup.sh -m standalone
```

Nacos 控制台：`http://localhost:8848/nacos`，默认账号 `nacos/nacos`。

### 3. 启动后端服务（按顺序）

```bash
mvn spring-boot:run -pl edu-gateway
mvn spring-boot:run -pl edu-auth
mvn spring-boot:run -pl edu-modules/edu-system
mvn spring-boot:run -pl edu-modules/edu-achievement
mvn spring-boot:run -pl edu-modules/edu-file
```

### 4. 启动前端

```bash
cd edu-ui
npm install
npm run dev
```

开发服务器运行在 `http://localhost`（端口 80），请求代理到 Gateway `http://localhost:8080`。

### 5. 访问系统

| 入口 | 地址 |
|------|------|
| 前台门户 | `http://localhost` |
| 登录页 | `http://localhost/login` |
| 管理后台 | `http://localhost/admin` |

默认管理员账号：`admin / admin123`

---

## 构建部署

### 后端打包

```bash
mvn clean package -DskipTests
```

各模块 `target` 下生成可执行 JAR：

```bash
java -jar edu-gateway/target/edu-gateway.jar
java -jar edu-auth/target/edu-auth.jar
# ... 其他模块同理
```

### 前端构建

```bash
cd edu-ui
npm run build:prod    # 生产构建，输出到 edu-admin/
npm run build:stage   # 预发布构建
```

构建产物部署至 Nginx 静态目录即可。

---

## 权限模型

| 角色 | Role Key | 可访问范围 |
|------|----------|------------|
| 管理员 | `admin` | 全部后台功能 (`/admin/*`) |
| 教师 | `teacher` | 成果申报 (`/portal/declare`)、我的成果 (`/portal/mine`) |
| 院级审核员 | `SchoolAudit` | 院级审核 (`/portal/audit/school`) |

### 数据权限

通过 MyBatis 自定义拦截器实现行级过滤，基于部门层级进行数据范围控制。非管理员用户仅能查看本部门及下级部门的数据。

---

## 认证流程

```
客户端 → Gateway (AuthFilter 校验 Token)
         ├─ 无 Token → 返回 401
         ├─ Token 过期 → 返回 401
         └─ Token 有效 → 从 Redis 读取登录态 → 放行
              └─ 下游服务 (HeaderInterceptor 解析用户信息)
                   └─ AOP 切面校验权限注解 (@PreAuthorize)
```

- Token 存储在 Redis：`login_tokens:{uuid}`，过期时间 720 分钟
- 密钥序列化：Key 使用 `StringRedisSerializer`，Value 使用 `FastJson2JsonRedisSerializer`
- AutoType 白名单限制在 `com.edu` 包下

---

## 成果审核流程

```
游客/前台查看新闻通知
    │
教师 (teacher) 登录
    │
    ├── 个人中心：完善资料、修改密码
    │
    ├── 成果申报 (declare)：填写成果信息 → 保存草稿
    │   └── 编辑/提交审核 → 状态: 0(draft) → 2(audit)
    │
    └── 我的成果 (mine)：查看申报记录与审核状态
            │
            ▼
院级审核员 (SchoolAudit) 登录
    │
    └── 审核工作台：查看待审/已审成果列表
        ├── 同意 → 状态: 3(passed)
        └── 退回 → 状态: 4(rejected)
                    └── 教师可重新提交 → 2(audit)
```

### 状态码说明（仅以下 4 种有效）

| 状态码 | 含义 |
|--------|------|
| 0 | 草稿 |
| 2 | 审核中 |
| 3 | 审核通过 |
| 4 | 审核退回 |

> 1（旧院级审核）和 5（旧退回修改）已废弃。

---

## 前端功能

| 模块 | 功能 |
|------|------|
| 门户 | 新闻公告浏览、成果公示 |
| 成果申报 | 填报教学成果信息（表单+附件上传） |
| 我的成果 | 申报记录查看、状态跟踪、退回后重新提交 |
| 审核工作台 | 院级审核列表、审核操作、审核记录 |
| 审核归档 | 历史审核记录查询 |
| 个人中心 | 个人信息、修改密码、头像上传 |

## 后端功能

| 模块 | 功能 |
|------|------|
| 系统管理 | 用户管理、角色管理、菜单管理、部门管理 |
| 字典管理 | 系统字典、参数配置 |
| 通知管理 | 系统通知、公告发布 |
| 日志管理 | 操作日志、登录日志 |
| 文件管理 | 上传/下载（本地/MinIO/FastDFS 多策略） |
| 成果管理 | 成果 CRUD、审核流程、统计报表 |
| 任务调度 | Quartz 定时任务配置与监控 |
| 服务监控 | Spring Boot Admin 服务状态监控 |

---

## 开发约定

- **不要直接操作 Redis Key**：优先使用封装好的 `RedisCache` 工具类
- **数据权限**：Service 层查询时调用 `DataScopeHelper` 设置部门过滤
- **日志记录**：使用 `@Log` 注解统一记录操作日志
- **异常处理**：统一抛出 `ServiceException` 或自定义业务异常，由全局异常处理器捕获
- **接口文档**：`http://localhost:8080/swagger-ui.html`（开发环境）
- **跨域**：Gateway 已配置 CORS，无需在下游服务重复配置
- **服务重启**：修改 Controller/Service/配置后需重启对应服务，前端 Vue 变更自动热更新

---

## 常见问题

**Q: Gateway 启动报 Nacos 连接失败？**
A: 确保 Nacos 已启动，且 `edu-config.sql` 已导入 MySQL。检查 `edu-gateway/bootstrap.yml` 中的 Nacos 地址。

**Q: 登录后提示 Token 无效？**
A: 检查 Redis 是否正常运行，`login_tokens:{uuid}` 是否已生成。

**Q: 前端页面空白？**
A: 确认 `.env.development` 中 `VUE_APP_BASE_API=/dev-api`，且 Gateway 已启动。

**Q: 文件上传失败？**
A: 查看 `edu-file` 模块配置，默认使用本地存储，可切换为 MinIO 或 FastDFS。

---

## License

MIT
