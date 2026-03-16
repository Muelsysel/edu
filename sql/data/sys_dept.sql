/*
Navicat MySQL Data Transfer

Source Server         : BS
Source Server Version : 80033
Source Host           : localhost:3306
Source Database       : edu_system

Target Server Type    : MYSQL
Target Server Version : 80033
File Encoding         : 65001

Date: 2026-03-16 14:08:06
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) DEFAULT '' COMMENT '部门名称',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` char(1) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb3 COMMENT='部门表';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES ('100', '0', '0', '郑州轻工业大学', '0', 'zpz', '13733732086', '2426481498@qq.com', '0', '0', 'admin', '2026-03-02 22:42:14', 'admin', '2026-03-03 16:17:22');
INSERT INTO `sys_dept` VALUES ('101', '100', '0,100', '计算机学院', '1', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2026-03-02 22:42:14', 'admin', '2026-03-03 16:19:32');
INSERT INTO `sys_dept` VALUES ('102', '100', '0,100', '化学学院', '2', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2026-03-02 22:42:14', 'admin', '2026-03-03 16:19:52');
INSERT INTO `sys_dept` VALUES ('103', '100', '0,100', '成果管理院', '0', '1', '13733732086', '2426481498@qq.com', '0', '0', 'admin', '2026-03-02 22:42:14', 'admin', '2026-03-10 13:14:18');
INSERT INTO `sys_dept` VALUES ('104', '101', '0,100,101', '市场部门', '2', '若依', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2026-03-02 22:42:14', '', null);
INSERT INTO `sys_dept` VALUES ('105', '100', '0,100', '软件学院', '3', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2026-03-02 22:42:14', 'admin', '2026-03-03 16:19:38');
INSERT INTO `sys_dept` VALUES ('106', '101', '0,100,101', '财务部门', '4', '若依', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2026-03-02 22:42:14', '', null);
INSERT INTO `sys_dept` VALUES ('107', '101', '0,100,101', '运维部门', '5', '若依', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2026-03-02 22:42:14', '', null);
INSERT INTO `sys_dept` VALUES ('108', '102', '0,100,102', '市场部门', '1', '若依', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2026-03-02 22:42:14', '', null);
INSERT INTO `sys_dept` VALUES ('109', '102', '0,100,102', '财务部门', '2', '若依', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2026-03-02 22:42:14', '', null);
