/*
Navicat MySQL Data Transfer

Source Server         : BS
Source Server Version : 80033
Source Host           : localhost:3306
Source Database       : edu_system

Target Server Type    : MYSQL
Target Server Version : 80033
File Encoding         : 65001

Date: 2026-03-16 14:08:16
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb3 COMMENT='字典类型表';

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES ('1', '用户性别', 'sys_user_sex', '0', 'admin', '2026-03-02 22:42:17', '', null, '用户性别列表');
INSERT INTO `sys_dict_type` VALUES ('2', '菜单状态', 'sys_show_hide', '0', 'admin', '2026-03-02 22:42:17', '', null, '菜单状态列表');
INSERT INTO `sys_dict_type` VALUES ('3', '系统开关', 'sys_normal_disable', '0', 'admin', '2026-03-02 22:42:17', '', null, '系统开关列表');
INSERT INTO `sys_dict_type` VALUES ('4', '任务状态', 'sys_job_status', '0', 'admin', '2026-03-02 22:42:17', '', null, '任务状态列表');
INSERT INTO `sys_dict_type` VALUES ('5', '任务分组', 'sys_job_group', '0', 'admin', '2026-03-02 22:42:17', '', null, '任务分组列表');
INSERT INTO `sys_dict_type` VALUES ('6', '系统是否', 'sys_yes_no', '0', 'admin', '2026-03-02 22:42:17', '', null, '系统是否列表');
INSERT INTO `sys_dict_type` VALUES ('7', '通知类型', 'sys_notice_type', '0', 'admin', '2026-03-02 22:42:17', '', null, '通知类型列表');
INSERT INTO `sys_dict_type` VALUES ('8', '通知状态', 'sys_notice_status', '0', 'admin', '2026-03-02 22:42:17', '', null, '通知状态列表');
INSERT INTO `sys_dict_type` VALUES ('9', '操作类型', 'sys_oper_type', '0', 'admin', '2026-03-02 22:42:17', '', null, '操作类型列表');
INSERT INTO `sys_dict_type` VALUES ('10', '系统状态', 'sys_common_status', '0', 'admin', '2026-03-02 22:42:17', '', null, '登录状态列表');
INSERT INTO `sys_dict_type` VALUES ('100', '教学成果状态', 'achievement_status', '0', 'admin', '2026-03-03 13:45:12', '', null, '审核状态');
INSERT INTO `sys_dict_type` VALUES ('101', '审核结果', 'audit_result', '0', 'admin', '2026-03-03 13:45:41', '', null, '审核通过/驳回');
INSERT INTO `sys_dict_type` VALUES ('102', '学院', 'college', '0', 'admin', '2026-03-03 13:53:29', '', null, null);
INSERT INTO `sys_dict_type` VALUES ('103', '成果类型', 'edu_achievement_category', '0', 'admin', '2026-03-03 16:07:17', '', null, null);
