/*
Navicat MySQL Data Transfer

Source Server         : BS
Source Server Version : 80033
Source Host           : localhost:3306
Source Database       : edu_system

Target Server Type    : MYSQL
Target Server Version : 80033
File Encoding         : 65001

Date: 2026-03-16 14:08:11
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=utf8mb3 COMMENT='字典数据表';

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES ('1', '1', '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2026-03-02 22:42:17', '', null, '性别男');
INSERT INTO `sys_dict_data` VALUES ('2', '2', '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2026-03-02 22:42:17', '', null, '性别女');
INSERT INTO `sys_dict_data` VALUES ('3', '3', '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2026-03-02 22:42:17', '', null, '性别未知');
INSERT INTO `sys_dict_data` VALUES ('4', '1', '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2026-03-02 22:42:17', '', null, '显示菜单');
INSERT INTO `sys_dict_data` VALUES ('5', '2', '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2026-03-02 22:42:17', '', null, '隐藏菜单');
INSERT INTO `sys_dict_data` VALUES ('6', '1', '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2026-03-02 22:42:17', '', null, '正常状态');
INSERT INTO `sys_dict_data` VALUES ('7', '2', '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2026-03-02 22:42:17', '', null, '停用状态');
INSERT INTO `sys_dict_data` VALUES ('8', '1', '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2026-03-02 22:42:17', '', null, '正常状态');
INSERT INTO `sys_dict_data` VALUES ('9', '2', '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2026-03-02 22:42:17', '', null, '停用状态');
INSERT INTO `sys_dict_data` VALUES ('10', '1', '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2026-03-02 22:42:17', '', null, '默认分组');
INSERT INTO `sys_dict_data` VALUES ('11', '2', '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2026-03-02 22:42:17', '', null, '系统分组');
INSERT INTO `sys_dict_data` VALUES ('12', '1', '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2026-03-02 22:42:18', '', null, '系统默认是');
INSERT INTO `sys_dict_data` VALUES ('13', '2', '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2026-03-02 22:42:18', '', null, '系统默认否');
INSERT INTO `sys_dict_data` VALUES ('14', '1', '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2026-03-02 22:42:18', '', null, '通知');
INSERT INTO `sys_dict_data` VALUES ('15', '2', '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2026-03-02 22:42:18', '', null, '公告');
INSERT INTO `sys_dict_data` VALUES ('16', '1', '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2026-03-02 22:42:18', '', null, '正常状态');
INSERT INTO `sys_dict_data` VALUES ('17', '2', '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2026-03-02 22:42:18', '', null, '关闭状态');
INSERT INTO `sys_dict_data` VALUES ('18', '99', '其他', '0', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2026-03-02 22:42:18', '', null, '其他操作');
INSERT INTO `sys_dict_data` VALUES ('19', '1', '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2026-03-02 22:42:18', '', null, '新增操作');
INSERT INTO `sys_dict_data` VALUES ('20', '2', '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2026-03-02 22:42:18', '', null, '修改操作');
INSERT INTO `sys_dict_data` VALUES ('21', '3', '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2026-03-02 22:42:18', '', null, '删除操作');
INSERT INTO `sys_dict_data` VALUES ('22', '4', '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2026-03-02 22:42:18', '', null, '授权操作');
INSERT INTO `sys_dict_data` VALUES ('23', '5', '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2026-03-02 22:42:18', '', null, '导出操作');
INSERT INTO `sys_dict_data` VALUES ('24', '6', '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2026-03-02 22:42:18', '', null, '导入操作');
INSERT INTO `sys_dict_data` VALUES ('25', '7', '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2026-03-02 22:42:18', '', null, '强退操作');
INSERT INTO `sys_dict_data` VALUES ('26', '8', '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2026-03-02 22:42:18', '', null, '生成操作');
INSERT INTO `sys_dict_data` VALUES ('27', '9', '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2026-03-02 22:42:18', '', null, '清空操作');
INSERT INTO `sys_dict_data` VALUES ('28', '1', '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2026-03-02 22:42:18', '', null, '正常状态');
INSERT INTO `sys_dict_data` VALUES ('29', '2', '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2026-03-02 22:42:18', '', null, '停用状态');
INSERT INTO `sys_dict_data` VALUES ('100', '1', '草稿', '0', 'achievement_status', 'default', 'default', 'N', '0', 'admin', '2026-03-03 13:46:36', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('101', '2', '院级审核中', '1', 'achievement_status', 'warning', 'default', 'N', '0', 'admin', '2026-03-03 13:47:00', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('102', '3', '校级审核中', '2', 'achievement_status', 'info', 'default', 'N', '0', 'admin', '2026-03-03 13:47:22', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('103', '4', '已通过', '3', 'achievement_status', 'success', 'default', 'N', '0', 'admin', '2026-03-03 13:47:38', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('104', '5', '已驳回', '4', 'achievement_status', 'danger', 'default', 'N', '0', 'admin', '2026-03-03 13:47:49', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('105', '1', '通过', '0', 'audit_result', 'success', 'default', 'N', '0', 'admin', '2026-03-03 13:48:11', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('106', '2', '驳回', '1', 'audit_result', 'danger', 'default', 'N', '0', 'admin', '2026-03-03 13:48:26', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('107', '1', '科研指导', '1', 'edu_achievement_category', null, 'default', 'N', '0', 'admin', '2026-03-03 16:08:13', 'admin', '2026-03-10 16:12:30', '期刊论文、专著等');
INSERT INTO `sys_dict_data` VALUES ('108', '2', '教材建设', '2', 'edu_achievement_category', null, 'default', 'N', '0', 'admin', '2026-03-03 16:08:35', '', null, '规划教材、校本教材');
INSERT INTO `sys_dict_data` VALUES ('109', '3', '竞赛指导', '3', 'edu_achievement_category', null, 'default', 'N', '0', 'admin', '2026-03-03 16:08:48', 'admin', '2026-03-10 16:12:41', '指导学生获奖或教师比赛');
INSERT INTO `sys_dict_data` VALUES ('110', '4', '教学改革', '4', 'edu_achievement_category', null, 'default', 'N', '0', 'admin', '2026-03-03 16:09:00', 'admin', '2026-03-10 16:13:05', '各级教学改革研究项目');
INSERT INTO `sys_dict_data` VALUES ('111', '5', '教学评估', '5', 'edu_achievement_category', null, 'default', 'N', '0', 'admin', '2026-03-03 19:21:57', 'admin', '2026-03-10 16:13:14', '教学评估，质量检测，监控工作等\n');
INSERT INTO `sys_dict_data` VALUES ('112', '6', '优秀课程', '6', 'edu_achievement_category', null, 'default', 'N', '0', 'admin', '2026-03-03 19:23:46', '', null, null);
