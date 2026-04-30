# 高校教学成果管理系统 — 最终审查报告

**日期**: 2026-04-30
**版本**: 3.6.7

---

## 一、RuoYi 残留引用移除方案

### 可安全移除的（242+ 处）

| 类别 | 位置 | 数量 | 操作 |
|------|------|:--:|------|
| `@author ruoyi` Java 注释 | edu-auth, edu-gateway, edu-common, edu-api, edu-system, edu-file, edu-job, edu-monitor | 242 个文件 | 批量替换为 `@author zpz` |
| POM 注释 "RuoYi Common" | 各模块 pom.xml | 21 处 | 改为 "Edu Common" |
| JS 文件 Copyright ruoyi | directive/, utils/ruoyi.js | 7 处 | 更新为项目名 |
| SQL 种子数据 | sys_user "ry"/"若依" | 1 行 | 删除 |
| SQL 操作日志 | sys_oper_log 中的"若依" | 8 处 | 替换为实际数据 |
| Nacos 配置 | ruoyi.vip, www.ruoyi.vip | 2 处 | 删除 |
| edu-ui/README.md | 整个文件 | 1 文件 | 重写为项目前端说明 |
| zMD/代码风格记录.md | "若依系"引用 | 3 处 | 改为"项目系" |
| Vue 文件硬编码 | com.ruoyi.quartz... | 1 处 | 改为 com.edu |

### 需要协调操作的（中风险）

| 文件 | 操作 | 影响 |
|------|------|------|
| `ruoyi.js` → `util.js` | 重命名 | 需同步更新 11 个 import |
| `ruoyi.scss` → `global.scss` | 重命名 | 需同步更新 main.js import |
| `RyTask.java` | 类名/方法名 | 需同步更新 sys_job 数据库记录和前端引用 |

---

## 二、当前代码存在的 Bug

### CRITICAL（2 项）
1. **审计处理无事务保护**: `schoolHandle()` 中更新成果状态和插入审核记录不是原子的 → `EduAuditRecordController.java:71-104`
2. **管理员导出日期范围失效**: 前端 `beginTime`/`endTime` 作为普通参数发送，Mapper 期望 `params.beginTime` → `achievement/index.vue:288-296`

### HIGH（5 项）
3. **统计 SQL 无日期过滤**: `countMonthPassed`/`countMonthRejected` 缺少月份条件 → `EduAchievementMapper.xml:200-206`
4. **teacherGet 无所有权校验**: 教师可获取他人成果详情 → `EduAchievementController.java:240`
5. **statistics 无权限注解**: 任何登录用户可访问 → `EduAuditRecordController.java:163`
6. **schoolHandle 无学院范围校验**: 审核员可审核任意学院成果 → `EduAuditRecordController.java:63`
7. **recordList 无教师所有权校验**: 教师可查看他人审核进度 → `EduAuditRecordController.java:122`

### MEDIUM（8 项）
8. 所有 `@RequestBody` 缺少 `@Validated`
9. 多个 Controller 方法缺少 null 检查
10. admin `edit()` 强制设置非管理员 status='2'，逻辑与 teacher 不一致
11. `teacherRemove()` 错误消息误导（"不存在"说成"无权"）
12. 出口按钮无 `v-hasPermi` 权限指令
13. 空 `.catch(() => {})` 静默吞错
14. 死 CSS `.status-1` 残留
15. 硬编码"school"命名误导

### LOW（10+ 项）
16. 全局 `console.log` 未移除
17. 未使用的 import `teacherListAchievement`
18. `collegeAudit` 目录名误导
19. 硬编码魔法字符串
20. `sendAuditNotice` 死代码
21. 并发审计可能竞态
22. 数据库缺少索引
23. 文件上传无服务端大小限制
24. 移动端适配仅门户页面

---

## 三、6 条核心业务流程审计

### 流程 1: 教师提交成果 ✅ 基本通过
- 前端提交逻辑正确（edit/resubmit 分支）
- 后端 teacherId 强制绑定正常
- **问题**: 缺少服务端 title/collegeId 验证

### 流程 2: 审核员审核 ✅ 基本通过
- 状态流转正确 (2→3/4)
- 审核级别正确 (auditLevel='2')
- **问题**: 无事务、无学院范围校验、可能并发竞态

### 流程 3: 教师查看审核进度 ✅ 通过
- recordId 跳过审核人过滤逻辑正确
- 时间线渲染正常
- 字段映射 (auditStatus→audit_result) 验证通过

### 流程 4: 统计数据展示 ⚠️ 有问题
- 后端键名和前端访问完全匹配
- **问题**: countMonthPassed/countMonthRejected 无日期过滤

### 流程 5: 导出（3 个变体）⚠️ 有问题
- 权限控制正确（admin/teacher/auditor 三路）
- 教师和审计员导出数据范围正确
- **问题**: 管理员导出日期范围参数绑定失败

### 流程 6: 新闻/通知双轨展示 ✅ 通过
- noticeType 过滤前后端一致
- 公共访问（跳过 token）正确
- 分页/搜索正常

---

## 四、毕业设计论文可用信息

### 技术栈清单
Java 1.8, Spring Boot 2.7.18, Spring Cloud 2021.0.9, Spring Cloud Alibaba 2021.0.6.1, MyBatis, Redis, MySQL 8, Nacos 2.2.3, JWT (jjwt 0.9.1), Apache POI 4.1.2, Kaptcha 2.3.3, Fastjson2 2.0.60, MinIO 8.2.2, Vue 2.6, Element UI 2.15, ECharts 5.4, Axios 0.28, Vue Router 3.4, Vuex 3.6

### 架构组成
- **7 个微服务**: gateway(8080), auth(9200), system(9201), achievement(9205), file(9300), job(9203), monitor(9100)
- **7 个公共模块**: core, redis, security, log, datascope, datasource, swagger, sensitive, seata
- **1 个前端应用**: Vue 2 SPA

### 核心创新点
1. 单级审核简化（从两级简化为一级）
2. 基于 MyBatis 拦截器的数据权限行级过滤
3. Redis + JWT 无状态认证
4. 新闻/通知双轨门户展示
5. 成果多级字典化（类型、等级、得分）
6. 审核流程时间线可视化
7. 多角色差异化导出（管理员/教师/审核员）
8. Excel 导出（Apache POI + @Excel 注解驱动）
9. 多数据源支持（Dynamic-DS）
10. 分布式事务集成（Seata）

### 数据库设计
- **业务表**: edu_achievement (教学成果), edu_audit_record (审核记录), edu_news (新闻公告)
- **系统表**: sys_user, sys_dept, sys_role, sys_menu, sys_dict_data/type, sys_notice, sys_config, sys_post, sys_oper_log, sys_logininfor
- 总计 19 张表

### 项目规模统计
- Java 源文件: ~280+
- Vue 组件: 51
- 总代码行数: ~77,000
- REST 控制器: 37
- API 端点: 58+
- 角色: 4 (admin/teacher/SchoolAudit/auditor)
- 部门: 175 (25 个学院 + 150+ 子部门)

---

## 五、部署信息

### 开发环境
| 组件 | 端口 | 启动命令 |
|------|------|---------|
| MySQL 8 | 3306 | 系统服务 |
| Redis | 6379 | 系统服务 |
| Nacos | 8848 | `startup.cmd -m standalone` |
| edu-gateway | 8080 | `mvn spring-boot:run -pl edu-gateway` |
| edu-auth | 9200 | `mvn spring-boot:run -pl edu-auth` |
| edu-system | 9201 | `mvn spring-boot:run -pl edu-modules/edu-system` |
| edu-achievement | 9205 | `mvn spring-boot:run -pl edu-modules/edu-achievement` |
| Frontend | 80 | `cd edu-ui && npm run dev` |

### 默认账户
- 管理员: admin / admin123
- 教师: teacher / 123456 (teacher01-18 同样密码)
- 审核员: schoolAudit / 123456

### 自动化测试
- `test.ps1`: 6 步 PowerShell 自动化测试，覆盖登录→提交→审核→验证→档案全流程

---

## 六、优化建议（非紧急）

1. 提取状态常量类 `AchievementConstants`
2. 添加数据库索引
3. 给所有 `@RequestBody` 加 `@Validated`
4. 添加 `@Transactional` 注解
5. 实现乐观锁防并发
6. 管理员导出 fix 日期范围参数绑定
7. 清理死代码和 console.log
8. 移动端全面响应式适配
9. 添加 API 文档（Springdoc OpenAPI 已集成，补充描述即可）
