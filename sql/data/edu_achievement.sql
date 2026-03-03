/*
Navicat MySQL Data Transfer

Source Server         : BS
Source Server Version : 80033
Source Host           : localhost:3306
Source Database       : edu_system

Target Server Type    : MYSQL
Target Server Version : 80033
File Encoding         : 65001

Date: 2026-03-03 15:04:09
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for edu_achievement
-- ----------------------------
DROP TABLE IF EXISTS `edu_achievement`;
CREATE TABLE `edu_achievement` (
  `achievement_id` bigint NOT NULL AUTO_INCREMENT COMMENT '成果 ID',
  `title` varchar(200) NOT NULL COMMENT '成果标题',
  `content` text COMMENT '成果内容',
  `file_url` varchar(500) DEFAULT NULL COMMENT '文件存储路径 (MinIO 路径)',
  `teacher_id` bigint NOT NULL COMMENT '教师 ID(关联 sys_user 表)',
  `college_id` bigint NOT NULL COMMENT '学院 ID(关联 sys_dept 表)',
  `status` char(1) DEFAULT '0' COMMENT '状态 (0:草稿 1:院级审核中 2:校级审核中 3:已通过 4:已驳回)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志 (0 代表存在 2 代表删除)',
  PRIMARY KEY (`achievement_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3 COMMENT='教学成果主表';

-- ----------------------------
-- Records of edu_achievement
-- ----------------------------
INSERT INTO `edu_achievement` VALUES ('1', '111', null, 'http://127.0.0.1:9000/edu-achievement/2026/03/03/张鹏展简历_20260303143855A001.docx', '123', '123', '0', '', '2026-03-03 14:39:05', '', null, '0');
