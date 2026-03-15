-- =====================================================
-- 审核模块菜单和按钮权限 SQL
-- 请根据实际情况调整 menu_id，确保不与已有 ID 冲突
-- 建议先查询 SELECT MAX(menu_id) FROM sys_menu 确定起始ID
-- =====================================================

-- 说明：parent_id 需要设为你的「成果管理」一级菜单的 menu_id
-- 如果你的成果管理一级菜单 menu_id 为 2000，则 parent_id 设为 2000
-- 以下示例使用 parent_id = 2000，menu_id 从 2020 开始，请根据实际修改

-- ==================== 院级审核工作台 ====================
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2020, '院级审核工作台', 2000, 10, 'collegeAudit', 'achievement/collegeAudit/index', 1, 0, 'C', '0', '0', 'achievement:audit:collegeList', 'education', 'admin', sysdate(), '院级审核工作台菜单');

-- 院级审核按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2021, '院级审核查看详情', 2020, 1, '', NULL, 1, 0, 'F', '0', '0', 'achievement:audit:query', '#', 'admin', sysdate(), '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2022, '院级审核操作', 2020, 2, '', NULL, 1, 0, 'F', '0', '0', 'achievement:audit:collegeHandle', '#', 'admin', sysdate(), '');

-- ==================== 校级审核工作台 ====================
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2030, '校级审核工作台', 2000, 11, 'schoolAudit', 'achievement/schoolAudit/index', 1, 0, 'C', '0', '0', 'achievement:audit:schoolList', 'education', 'admin', sysdate(), '校级审核工作台菜单');

-- 校级审核按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2031, '校级审核查看详情', 2030, 1, '', NULL, 1, 0, 'F', '0', '0', 'achievement:audit:query', '#', 'admin', sysdate(), '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2032, '校级审核操作', 2030, 2, '', NULL, 1, 0, 'F', '0', '0', 'achievement:audit:schoolHandle', '#', 'admin', sysdate(), '');

-- ==================== 审核记录 ====================
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2040, '审核记录', 2000, 12, 'auditRecord', 'achievement/auditRecord/index', 1, 0, 'C', '0', '0', 'achievement:audit:recordList', 'log', 'admin', sysdate(), '审核记录菜单');

-- 审核记录导出按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2041, '审核记录导出', 2040, 1, '', NULL, 1, 0, 'F', '0', '0', 'achievement:audit:export', '#', 'admin', sysdate(), '');


-- =====================================================
-- 使用说明
-- =====================================================
-- 1. 执行本 SQL 后，在 RuoYi 的「系统管理 -> 菜单管理」中可以看到新增的菜单
-- 2. 进入「系统管理 -> 角色管理」，编辑院级审核员角色，勾选「院级审核工作台」及其按钮
-- 3. 编辑校级审核员角色，勾选「校级审核工作台」及其按钮
-- 4. 审核记录页面可同时分配给院级和校级审核员
-- 5. admin 管理员默认拥有所有权限，无需额外配置
--
-- 【重要】parent_id = 2000 和 menu_id 需要根据你实际数据库情况调整：
--   SELECT MAX(menu_id) FROM sys_menu;  -- 查看当前最大 menu_id
--   SELECT menu_id FROM sys_menu WHERE menu_name = '成果管理';  -- 查看父菜单 ID
