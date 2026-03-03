# 修复记录 - edu-achievement 模块启动问题

## 问题描述

edu-achievement 模块无法启动，出现以下错误：

1. Controller 层：无法自动装配 RemoteFileService 类型的 Bean
2. Service 层：无法解析符号 ISysUserService
3. Service 层：无法自动装配 EduAchievementMapper 和 EduAchievementAuditLogMapper

## 修复内容

### 1. 修复 RemoteFileService 注入问题

**文件**: `EduAchievementController.java`

**问题**: 使用@Autowired 注入 Feign Client 失败

**修复**: 将@Autowired 改为@Resource

```java
@Resource
private RemoteFileService remoteFileService;
```

**原因**: 在 Spring Cloud 微服务架构中，Feign Client 的注入最好使用@Resource 注解，避免与其他@Autowired 注入冲突。

### 2. 移除 ISysUserService 依赖

**文件**: `EduAchievementServiceImpl.java`

**问题**: 直接依赖 edu-system 模块的 ISysUserService，违反微服务架构原则

**修复**:

- 移除 ISysUserService 的导入和注入
- 简化数据权限逻辑，使用 SecurityUtils 获取当前用户信息
- 教师角色只能查看本人数据（通过设置 teacherId 过滤）

```java
// 修复前
@Autowired
private ISysUserService sysUserService;

// 修复后 - 直接使用 SecurityUtils
Long userId = SecurityUtils.getUserId();
if (eduAchievement.getTeacherId() == null) {
    eduAchievement.setTeacherId(userId);
}
```

**原因**:

- 微服务之间应该通过 Feign Client（RemoteUserService）进行通信
- 不应该直接依赖其他业务模块的 Service 接口
- 数据权限控制在当前模块内完成

### 3. 清理重复的 import

**文件**: `EduAchievementController.java`

**问题**: 存在重复和顺序混乱的 import 语句

**修复**: 整理 import 顺序，移除重复项

```java
import javax.annotation.Resource;
import org.springframework.web.multipart.MultipartFile;
```

### 4. 移除自动填充 college_id

**文件**: `EduAchievementController.java`

**问题**: 尝试使用 `SecurityUtils.getDeptId()` 获取学院 ID

**修复**: 移除自动填充 college_id 的逻辑

```java
// 修复前
eduAchievement.setCollegeId(SecurityUtils.getDeptId());  // 无法获取

// 修复后
// college_id 由前端传递或在审核时由审核员设置
```

**原因**:

- SecurityUtils 主要获取用户基本信息（userId, username）
- 不包含部门（dept）信息
- college_id 应该由前端传递（教师选择所属学院）
- 或者在审核环节由审核员设置

**前端实现**:

```javascript
// index.vue - 添加学院选择器
<el-form-item label="所属学院" prop="collegeId">
  <el-select v-model="form.collegeId" placeholder="请选择所属学院">
    <el-option
      v-for="dept in deptList"
      :key="dept.deptId"
      :label="dept.deptName"
      :value="dept.deptId"
    />
  </el-select>
</el-form-item>

// 调用部门接口获取学院列表
getDeptList() {
  listDept().then(response => {
    this.deptList = response.data;
  });
}
```

### 5. 验证 Mapper 扫描配置

**验证结果**: ✅ 正确

**配置位置**: `EnableCustomConfig.java`

```java
@MapperScan("com.edu.**.mapper")
```

**说明**:

- Mapper 接口不需要添加 @Mapper 注解
- XML 文件路径配置正确：`src/main/resources/mapper/achievement/`
- MyBatis 会自动扫描并注册 Mapper Bean

### 6. 清理重复的 import

**文件**: `EduAchievementController.java`

**问题**: 存在重复和顺序混乱的 import 语句

**修复**: 整理 import 顺序，移除重复项

```java
import javax.annotation.Resource;
import org.springframework.web.multipart.MultipartFile;
```

## 架构说明

### 微服务依赖关系

```
edu-achievement
├── edu-api-system (Feign Client)
│   ├── RemoteFileService (调用 edu-file)
│   └── RemoteUserService (调用 edu-system)
├── edu-common-* (通用模块)
└── 不直接依赖 edu-system 或 edu-file 模块
```

### 数据权限实现策略

1. **教师角色**: 自动设置 teacherId 为当前用户 ID，只能查看本人数据
2. **管理员角色**: 不设置过滤条件，可以查看所有数据
3. **院级/校级审核员**: 通过查询参数 college_id 过滤（待完善）

### 正确的服务调用方式

```java
// ✅ 正确 - 使用 Feign Client
@Resource
private RemoteFileService remoteFileService;

// ❌ 错误 - 直接依赖其他模块的 Service
@Autowired
private ISysUserService sysUserService;  // 不允许
```

## 待完善功能

### 1. 完整的角色权限控制

当前实现较简单，需要：

- 通过 RemoteUserService 获取用户角色信息
- 根据角色动态过滤数据
- 院级审核员：过滤 college_id
- 校级审核员：不限制学院，但限制状态为"校级审核中"

### 2. 文件下载功能

需要实现文件下载和预览接口，调用 edu-file 模块的服务。

## 测试建议

### 启动测试

```bash
# 确保以下服务已启动
- Nacos (127.0.0.1:8848)
- Redis
- MySQL
- edu-auth
- edu-file
- edu-system

# 启动 edu-achievement
cd edu-modules/edu-achievement
mvn clean install
java -jar target/edu-modules-achievement.jar
```

### 预期结果

- 启动成功，无 Bean 装配错误
- 控制台打印启动成功信息
- Nacos 服务列表中显示 edu-achievement

## 文件变更清单

1. `EduAchievementController.java`
   - 修改 RemoteFileService 注入方式
   - 整理 import 语句

2. `EduAchievementServiceImpl.java`
   - 移除 ISysUserService 依赖
   - 简化数据权限逻辑

---

**修复时间**: 2026-03-03
**修复人**: AI Assistant
**验证状态**: 待用户验证
