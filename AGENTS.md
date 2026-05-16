# AGENTS.md — 高校教学成果管理系统

## Architecture

Spring Cloud Alibaba microservices, no containerization by default.

| Service | Port | Job |
|---|---|---|
| `edu-gateway` | 8080 | Sole entrypoint, CORS, `AuthFilter` (validates token via Redis) |
| `edu-auth` | 9200 | Login / logout / token refresh |
| `edu-system` | 9201 | Users, roles, menus, depts, dicts, notices, logs |
| `edu-achievement` | 9205 | Achievement CRUD, audit workflow, portal news, stats |
| `edu-file` | 9300 | File upload/download (local / MinIO / FastDFS) |
| `edu-job` | 9203 | Quartz scheduled tasks |
| `edu-monitor` | 9100 | Spring Boot Admin dashboard |

Frontend: Vue 2 (port 80 dev server), proxies `/dev-api` → `http://localhost:8080` (Gateway) and **strips the `/dev-api` prefix**.

## Startup order (CRITICAL — non-standard)

Auth must start **before** Gateway:

```
Nacos → Redis → (DB already up)
  → edu-auth      (port 9200)
  → edu-gateway   (port 8080)
  → edu-system    (port 9201)
  → edu-achievement (port 9205)
  → edu-file      (port 9300)
```

The Gateway `AuthFilter` reads tokens directly from Redis (`login_tokens:{uuid}`), not by calling Auth via RPC. Auth must be running first so login flows work through the gateway.

Infra prerequisites: MySQL 8, Redis 5+, Nacos 2.2.3 (standalone mode, no namespace/group needed — defaults to `public`/`DEFAULT_GROUP`).

## Nacos config

All services share one config in Nacos: `application-dev.yml` (namespace=public, group=DEFAULT_GROUP). Override is set in `bootstrap.yml` only — **no `application.yml`** exists per service.

## Token & auth (dual-token system)

1. Actual session token = UUID stored in Redis: `login_tokens:{uuid}` (TTL 720 min)
2. JWT wraps claim metadata (`user_key`, `user_id`, `username`) — **does NOT carry permissions/roles**
3. Permissions/roles live inside the `LoginUser` object in Redis
4. `AuthFilter` (gateway, order `-200`) extracts `Authorization: Bearer <jwt>`, parses with secret `abcdefghijklmnopqrstuvwxyz`, then checks Redis
5. Downstream services use `HeaderInterceptor` (order `-10`) to set `SecurityContextHolder` from headers
6. Permission checking: `@RequiresPermissions`, `@RequiresRoles`, `@RequiresLogin` annotations → `PreAuthorizeAspect`
7. `@InnerAuth` for inter-service Feign calls — requires `from-source: inner` header
8. Feign calls auto-forward `Authorization`, `user_id`, `user_key`, `username` headers via `FeignRequestInterceptor`

**Hardcoded public bypass**: Gateway skips auth for any URL starting with `/achievement/portal/news`.

## Admin & data scope

- Super-admin: `userId == 1` — bypasses ALL data scope filtering
- `SecurityContextHolder` uses Alibaba `TransmittableThreadLocal` (survives `@Async`)
- `@DataScope` annotation on Service methods injects `params.dataScope` as raw SQL (`${params.dataScope}` in MyBatis mappers, NOT `#{...}`). Data scope levels:
  - `1` = all, `2` = custom dept, `3` = own dept, `4` = dept + children, `5` = self only

## Achievement status flow

Only 4 valid states: **0**=draft, **2**=audit, **3**=passed, **4**=rejected. States 1 and 5 are deprecated. Teacher submits → 2 (audit); SchoolAudit approves → 3 or rejects → 4 (teacher can resubmit → 2).

## Commands

```bash
# Build (skip tests)
mvn clean package -DskipTests

# Run single service (from root)
mvn spring-boot:run -pl edu-gateway

# Run a specific test class
mvn test -pl edu-modules/edu-system -Dtest=XxxTest

# Frontend
cd edu-ui
npm install
npm run dev          # http://localhost (port 80)
npm run build:prod   # output → edu-admin/

# Database init
mysql -u root -p < sql/data/edu_system.sql
mysql -u root -p < sql/data/edu_config.sql
```

## Key class paths

| Concern | Class |
|---|---|
| Gateway auth filter | `com.edu.gateway.filter.AuthFilter` |
| Permission aspect | `com.edu.common.security.aspect.PreAuthorizeAspect` |
| Inner auth aspect | `com.edu.common.security.aspect.InnerAuthAspect` |
| Token service | `com.edu.common.security.service.TokenService` |
| Auth util | `com.edu.common.security.auth.AuthUtil` |
| Operation log aspect | `com.edu.common.log.aspect.LogAspect` |
| Data scope aspect | `com.edu.common.datascope.aspect.DataScopeAspect` |
| Global exception handler | `com.edu.common.security.handler.GlobalExceptionHandler` |
| Security context holder | `com.edu.common.core.SecurityContextHolder` |
| Feign client scan base | `com.edu` |
| Mapper scan base | `com.edu.**.mapper` |

## Conventions

- All services exclude `DataSourceAutoConfiguration` if they don't need a DB (e.g., gateway)
- `@Log` annotation auto-excludes `password`, `oldPassword`, `newPassword`, `confirmPassword` from logged params
- Default admin credentials: `admin / admin123`
- Prefix `from-source` header (set by gateway) must be **stripped** for external requests — only set by internal Feign
- Feign clients are defined in `edu-api/edu-api-system/`
- Common modules (`edu-common-*`) are NOT standalone services — they're libs pulled in as deps
