# AGENTS.md — edu-achievement-master

## Project Type
Spring Cloud Alibaba microservices (Java 8) + Vue 2 frontend. University teaching-achievement management system.

## Quick Start (Backend)
1. **Prerequisites**: JDK 1.8, Maven 3.6+, MySQL 8, Redis, Nacos 2.2.3
2. **DB init** (in order):
   ```bash
   mysql -u root -p < sql/data/edu_system.sql
   mysql -u root -p < sql/data/edu_config.sql
   ```
3. **Start Nacos**: `startup.cmd -m standalone` (Win) / `sh startup.sh -m standalone` (Linux/Mac)
4. **Start services in order**:
   ```bash
   mvn spring-boot:run -pl edu-gateway      # port 8080
   mvn spring-boot:run -pl edu-auth         # port 9200
   mvn spring-boot:run -pl edu-modules/edu-system      # port 9201
   mvn spring-boot:run -pl edu-modules/edu-achievement # port 9205
   mvn spring-boot:run -pl edu-modules/edu-file        # port 9300
   ```

## Quick Start (Frontend)
```bash
cd edu-ui
npm install
npm run dev        # dev server on port 80, proxies /dev-api to localhost:8080
npm run build:prod # outputs to edu-ui/edu-admin
```

## Architecture Notes
- **Gateway** (`edu-gateway`) is the single entry point. All frontend requests go through `localhost:8080`.
- **Auth flow**: JWT (jjwt 0.9.1) + Redis sessions. `login_tokens:{uuid}` in Redis, TTL 720 min.
- **Permission model**: Gateway `AuthFilter` validates token against Redis; downstream services use `HeaderInterceptor` + AOP (`PreAuthorizeAspect`).
- **Redis serialization**: `StringRedisSerializer` for keys, custom `FastJson2JsonRedisSerializer` for values. Auto-type whitelist restricted to `com.edu` package.
- **Achievement status flow**: `0(draft) → 2(audit) → 3(passed)`. Rejection goes to `4(rejected)`. Teachers can resubmit from `4` to `2`.
  Deprecated status values: 1 (old college audit), 5 (old return-for-revision). Only 0,2,3,4 are valid.
- **Data scope**: MyBatis interceptor for row-level filtering based on dept hierarchy.

## Monorepo Boundaries
```
edu-auth            (9200)  login/token/refresh
edu-gateway        (8080)  routing, auth filter, CORS
edu-modules/
  edu-system       (9201)  user/role/menu/dept/dict/post/notice
  edu-achievement  (9205)  achievement CRUD, audit flow, news, statistics
  edu-file         (9300)  upload/download (local/MinIO/FastDFS)
  edu-job          (9203)  Quartz scheduling
edu-common/
  edu-common-core, redis, security, log, datascope, datasource, seata, swagger, sensitive
edu-api/
  edu-api-system           Feign interfaces
edu-ui                     Vue 2 + Element UI + ECharts
```

## Testing / Verification
- No dedicated test command. Build with `mvn clean package -DskipTests`.
- Docker one-shot: `sh docker/copy.sh && cd docker && docker-compose up -d`. Compose includes MySQL 5.7, Redis, Nacos, Nginx, MinIO, and all services.
- Nacos default creds: `nacos/nacos`.
- Default admin account: `admin / admin123`.

## Frontend Dev Notes
- Vue CLI 4, port 80, `VUE_APP_BASE_API=/dev-api` (check `.env.development` if present).
- Proxy target: `http://localhost:8080` (Gateway).
- `publicPath` is `/` for both dev and prod.
- `edu-admin` is the build output dir; Nginx serves `nginx/html/dist` in Docker.
- Portal routes under `/portal/*` are public unless `meta.requiresAuth` is set.

## Permission / Role Mapping
| Role | Role key | Can access |
|------|----------|------------|
| Admin | `admin` | `/admin/*`, all backend permissions |
| Teacher | `teacher` | `/portal/declare`, `/portal/mine` |
| School auditor | `SchoolAudit` | `/portal/audit/school` |

## Important Constraints
- **Do NOT run `git commit` / `git push` unless user explicitly asks.**
- **Never modify files outside the working directory.**
- Backend changes usually require a service restart (Spring Boot devtools not confirmed active).
- Frontend changes are hot-reloaded by Vue CLI dev server.
- MySQL 8 in dev, but Docker compose uses MySQL 5.7.
- Nacos config lives in `sql/data/edu_config.sql`; edit there if bootstrap config changes.

## Known Recent Changes
- **Audit notice feature disabled**: `EduAuditRecordController` no longer calls `sendAuditNotice()` after college/school audit. The `insertSysNotice` method still exists in service but is not triggered.

## When in Doubt
- Check `README.md` for full feature descriptions and UI design tokens.
- Check `pom.xml` root for exact dependency versions (Spring Boot 2.7.18, Cloud 2021.0.9, Alibaba 2021.0.6.1).
