/*
Navicat MySQL Data Transfer

Source Server         : BS
Source Server Version : 80033
Source Host           : localhost:3306
Source Database       : edu_system

Target Server Type    : MYSQL
Target Server Version : 80033
File Encoding         : 65001

Date: 2026-03-15 17:59:54
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) DEFAULT '' COMMENT '路由名称',
  `is_frame` int DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` int DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3048 DEFAULT CHARSET=utf8mb3 COMMENT='菜单权限表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('1', '系统管理', '0', '9', 'system', null, '', '', '1', '0', 'M', '0', '0', '', 'system', 'admin', '2026-03-02 22:42:14', 'admin', '2026-03-03 16:44:48', '系统管理目录');
INSERT INTO `sys_menu` VALUES ('2', '系统监控', '0', '10', 'monitor', null, '', '', '1', '0', 'M', '0', '0', '', 'monitor', 'admin', '2026-03-02 22:42:14', 'admin', '2026-03-03 16:44:31', '系统监控目录');
INSERT INTO `sys_menu` VALUES ('3', '系统工具', '0', '10', 'tool', null, '', '', '1', '0', 'M', '0', '0', '', 'tool', 'admin', '2026-03-02 22:42:14', 'admin', '2026-03-03 16:44:27', '系统工具目录');
INSERT INTO `sys_menu` VALUES ('100', '用户管理', '1', '1', 'user', 'system/user/index', '', '', '1', '0', 'C', '0', '0', 'system:user:list', 'user', 'admin', '2026-03-02 22:42:14', '', null, '用户管理菜单');
INSERT INTO `sys_menu` VALUES ('101', '角色管理', '1', '2', 'role', 'system/role/index', '', '', '1', '0', 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2026-03-02 22:42:14', '', null, '角色管理菜单');
INSERT INTO `sys_menu` VALUES ('102', '菜单管理', '1', '3', 'menu', 'system/menu/index', '', '', '1', '0', 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2026-03-02 22:42:14', '', null, '菜单管理菜单');
INSERT INTO `sys_menu` VALUES ('103', '部门管理', '1', '4', 'dept', 'system/dept/index', '', '', '1', '0', 'C', '0', '0', 'system:dept:list', 'tree', 'admin', '2026-03-02 22:42:14', '', null, '部门管理菜单');
INSERT INTO `sys_menu` VALUES ('104', '岗位管理', '1', '5', 'post', 'system/post/index', '', '', '1', '0', 'C', '0', '0', 'system:post:list', 'post', 'admin', '2026-03-02 22:42:14', '', null, '岗位管理菜单');
INSERT INTO `sys_menu` VALUES ('105', '字典管理', '1', '6', 'dict', 'system/dict/index', '', '', '1', '0', 'C', '0', '0', 'system:dict:list', 'dict', 'admin', '2026-03-02 22:42:14', '', null, '字典管理菜单');
INSERT INTO `sys_menu` VALUES ('106', '参数设置', '1', '7', 'config', 'system/config/index', '', '', '1', '0', 'C', '0', '0', 'system:config:list', 'edit', 'admin', '2026-03-02 22:42:14', '', null, '参数设置菜单');
INSERT INTO `sys_menu` VALUES ('107', '通知公告', '1', '8', 'notice', 'system/notice/index', '', '', '1', '0', 'C', '0', '0', 'system:notice:list', 'message', 'admin', '2026-03-02 22:42:14', '', null, '通知公告菜单');
INSERT INTO `sys_menu` VALUES ('108', '日志管理', '1', '9', 'log', '', '', '', '1', '0', 'M', '0', '0', '', 'log', 'admin', '2026-03-02 22:42:14', '', null, '日志管理菜单');
INSERT INTO `sys_menu` VALUES ('109', '在线用户', '2', '1', 'online', 'monitor/online/index', '', '', '1', '0', 'C', '0', '0', 'monitor:online:list', 'online', 'admin', '2026-03-02 22:42:14', '', null, '在线用户菜单');
INSERT INTO `sys_menu` VALUES ('110', '定时任务', '2', '2', 'job', 'monitor/job/index', '', '', '1', '0', 'C', '0', '0', 'monitor:job:list', 'job', 'admin', '2026-03-02 22:42:14', '', null, '定时任务菜单');
INSERT INTO `sys_menu` VALUES ('111', 'Sentinel控制台', '2', '3', 'http://localhost:8718', '', '', '', '0', '0', 'C', '0', '0', 'monitor:sentinel:list', 'sentinel', 'admin', '2026-03-02 22:42:14', '', null, '流量控制菜单');
INSERT INTO `sys_menu` VALUES ('112', 'Nacos控制台', '2', '4', 'http://localhost:8848/nacos', '', '', '', '0', '0', 'C', '0', '0', 'monitor:nacos:list', 'nacos', 'admin', '2026-03-02 22:42:14', '', null, '服务治理菜单');
INSERT INTO `sys_menu` VALUES ('113', 'Admin控制台', '2', '5', 'http://localhost:9100/login', '', '', '', '0', '0', 'C', '0', '0', 'monitor:server:list', 'server', 'admin', '2026-03-02 22:42:14', '', null, '服务监控菜单');
INSERT INTO `sys_menu` VALUES ('114', '表单构建', '3', '1', 'build', 'tool/build/index', '', '', '1', '0', 'C', '0', '0', 'tool:build:list', 'build', 'admin', '2026-03-02 22:42:14', '', null, '表单构建菜单');
INSERT INTO `sys_menu` VALUES ('115', '代码生成', '3', '2', 'gen', 'tool/gen/index', '', '', '1', '0', 'C', '0', '0', 'tool:gen:list', 'code', 'admin', '2026-03-02 22:42:14', '', null, '代码生成菜单');
INSERT INTO `sys_menu` VALUES ('116', '系统接口', '3', '3', 'http://localhost:8080/swagger-ui/index.html', '', '', '', '0', '0', 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', '2026-03-02 22:42:14', '', null, '系统接口菜单');
INSERT INTO `sys_menu` VALUES ('500', '操作日志', '108', '1', 'operlog', 'system/operlog/index', '', '', '1', '0', 'C', '0', '0', 'system:operlog:list', 'form', 'admin', '2026-03-02 22:42:14', '', null, '操作日志菜单');
INSERT INTO `sys_menu` VALUES ('501', '登录日志', '108', '2', 'logininfor', 'system/logininfor/index', '', '', '1', '0', 'C', '0', '0', 'system:logininfor:list', 'logininfor', 'admin', '2026-03-02 22:42:15', '', null, '登录日志菜单');
INSERT INTO `sys_menu` VALUES ('1000', '用户查询', '100', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:query', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1001', '用户新增', '100', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:add', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1002', '用户修改', '100', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:edit', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1003', '用户删除', '100', '4', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:remove', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1004', '用户导出', '100', '5', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:export', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1005', '用户导入', '100', '6', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:import', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1006', '重置密码', '100', '7', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1007', '角色查询', '101', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'system:role:query', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1008', '角色新增', '101', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'system:role:add', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1009', '角色修改', '101', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'system:role:edit', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1010', '角色删除', '101', '4', '', '', '', '', '1', '0', 'F', '0', '0', 'system:role:remove', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1011', '角色导出', '101', '5', '', '', '', '', '1', '0', 'F', '0', '0', 'system:role:export', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1012', '菜单查询', '102', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'system:menu:query', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1013', '菜单新增', '102', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'system:menu:add', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1014', '菜单修改', '102', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1015', '菜单删除', '102', '4', '', '', '', '', '1', '0', 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1016', '部门查询', '103', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'system:dept:query', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1017', '部门新增', '103', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'system:dept:add', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1018', '部门修改', '103', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1019', '部门删除', '103', '4', '', '', '', '', '1', '0', 'F', '0', '0', 'system:dept:remove', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1020', '岗位查询', '104', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'system:post:query', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1021', '岗位新增', '104', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'system:post:add', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1022', '岗位修改', '104', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'system:post:edit', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1023', '岗位删除', '104', '4', '', '', '', '', '1', '0', 'F', '0', '0', 'system:post:remove', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1024', '岗位导出', '104', '5', '', '', '', '', '1', '0', 'F', '0', '0', 'system:post:export', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1025', '字典查询', '105', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:dict:query', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1026', '字典新增', '105', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:dict:add', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1027', '字典修改', '105', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:dict:edit', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1028', '字典删除', '105', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:dict:remove', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1029', '字典导出', '105', '5', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:dict:export', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1030', '参数查询', '106', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:config:query', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1031', '参数新增', '106', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:config:add', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1032', '参数修改', '106', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:config:edit', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1033', '参数删除', '106', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:config:remove', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1034', '参数导出', '106', '5', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:config:export', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1035', '公告查询', '107', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:notice:query', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1036', '公告新增', '107', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:notice:add', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1037', '公告修改', '107', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1038', '公告删除', '107', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1039', '操作查询', '500', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:operlog:query', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1040', '操作删除', '500', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:operlog:remove', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1041', '日志导出', '500', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:operlog:export', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1042', '登录查询', '501', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:logininfor:query', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1043', '登录删除', '501', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:logininfor:remove', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1044', '日志导出', '501', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:logininfor:export', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1045', '账户解锁', '501', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:logininfor:unlock', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1046', '在线查询', '109', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1047', '批量强退', '109', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1048', '单条强退', '109', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1049', '任务查询', '110', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1050', '任务新增', '110', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1051', '任务修改', '110', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1052', '任务删除', '110', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:remove', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1053', '状态修改', '110', '5', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:changeStatus', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1054', '任务导出', '110', '6', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:export', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1055', '生成查询', '115', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1056', '生成修改', '115', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1057', '生成删除', '115', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:remove', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1058', '导入代码', '115', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:import', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1059', '预览代码', '115', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2026-03-02 22:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('1060', '生成代码', '115', '5', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:code', '#', 'admin', '2026-03-02 22:42:16', '', null, '');
INSERT INTO `sys_menu` VALUES ('3028', '教学成果管理', '0', '1', 'achievement', 'achievement/achievement/index', null, '', '1', '0', 'C', '0', '0', 'achievement:achievement:list', 'system', 'admin', '2026-03-03 14:08:45', 'admin', '2026-03-03 15:45:34', '教学成果管理菜单');
INSERT INTO `sys_menu` VALUES ('3029', '教学成果管理查询', '3028', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'achievement:achievement:query', '#', 'admin', '2026-03-03 14:08:45', '', null, '');
INSERT INTO `sys_menu` VALUES ('3030', '教学成果管理新增', '3028', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'achievement:achievement:add', '#', 'admin', '2026-03-03 14:08:45', '', null, '');
INSERT INTO `sys_menu` VALUES ('3031', '教学成果管理修改', '3028', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'achievement:achievement:edit', '#', 'admin', '2026-03-03 14:08:45', '', null, '');
INSERT INTO `sys_menu` VALUES ('3032', '教学成果管理删除', '3028', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'achievement:achievement:remove', '#', 'admin', '2026-03-03 14:08:45', '', null, '');
INSERT INTO `sys_menu` VALUES ('3033', '教学成果管理导出', '3028', '5', '#', '', null, '', '1', '0', 'F', '0', '0', 'achievement:achievement:export', '#', 'admin', '2026-03-03 14:08:45', '', null, '');
INSERT INTO `sys_menu` VALUES ('3034', '成果申报', '0', '1', 'myAchievement', 'achievement/myAchievement/index', null, 'MyAchievement', '1', '0', 'C', '0', '0', '', 'upload', 'admin', '2026-03-03 14:55:04', 'admin', '2026-03-03 15:46:35', '');
INSERT INTO `sys_menu` VALUES ('3035', '教师新增', '3034', '1', '', null, null, '', '1', '0', 'F', '0', '0', 'achievement:achievement:teacherAdd', '#', 'admin', '2026-03-03 15:25:57', 'admin', '2026-03-10 15:32:12', '');
INSERT INTO `sys_menu` VALUES ('3036', '查询部门列表', '3034', '2', '', null, null, '', '1', '0', 'F', '0', '0', 'system:dept:list', '#', 'admin', '2026-03-03 15:47:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('3037', '成果管理', '0', '2', 'myAchievementList', 'achievement/myAchievementList/index', null, 'teacherListAchievement', '1', '0', 'C', '0', '0', 'achievement:achievement:teacherList', 'education', 'admin', '2026-03-03 16:43:36', 'admin', '2026-03-10 15:47:28', '');
INSERT INTO `sys_menu` VALUES ('3041', '教师修改', '3037', '1', '', null, null, '', '1', '0', 'F', '0', '0', 'achievement:achievement:teacherEdit', '#', 'admin', '2026-03-10 14:47:02', 'admin', '2026-03-10 15:32:46', '');
INSERT INTO `sys_menu` VALUES ('3042', '教师查询', '3037', '2', '', null, null, '', '1', '0', 'F', '0', '0', 'achievement:achievement:teacherQuery', '#', 'admin', '2026-03-10 14:47:42', 'admin', '2026-03-10 15:33:11', '');
INSERT INTO `sys_menu` VALUES ('3043', '教师删除', '3037', '3', '', null, null, '', '1', '0', 'F', '0', '0', 'achievement:achievement:teacherRemove', '#', 'admin', '2026-03-10 15:51:44', 'admin', '2026-03-10 15:52:08', '');
INSERT INTO `sys_menu` VALUES ('3044', '教师成果详情', '3037', '4', '', null, null, '', '1', '0', 'F', '0', '0', 'achievement:achievement:teacherGet', '#', 'admin', '2026-03-10 15:53:06', '', null, '');
INSERT INTO `sys_menu` VALUES ('3045', '院级审核工作台', '0', '2', 'collegeAudit', 'achievement/collegeAudit/index', null, 'collegeAudit', '1', '0', 'C', '0', '0', null, 'form', 'admin', '2026-03-15 17:15:16', '', null, '');
INSERT INTO `sys_menu` VALUES ('3046', '校级审核工作台', '0', '2', 'schoolAudit', 'achievement/schoolAudit/index', null, 'schoolAudit', '1', '0', 'C', '0', '0', '', 'form', 'admin', '2026-03-15 17:15:47', 'admin', '2026-03-15 17:16:53', '');
INSERT INTO `sys_menu` VALUES ('3047', '审核记录', '0', '3', 'auditRecord', 'achievement/auditRecord/index', null, 'auditRecord', '1', '0', 'C', '0', '0', null, 'log', 'admin', '2026-03-15 17:17:41', '', null, '');
