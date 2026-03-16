/*
Navicat MySQL Data Transfer

Source Server         : BS
Source Server Version : 80033
Source Host           : localhost:3306
Source Database       : edu_system

Target Server Type    : MYSQL
Target Server Version : 80033
File Encoding         : 65001

Date: 2026-03-16 14:07:55
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
  `file_url` varchar(1500) DEFAULT NULL COMMENT '证明材料(最多5个)',
  `teacher_id` bigint NOT NULL COMMENT '教师 ID(关联 sys_user 表)',
  `college_id` bigint NOT NULL COMMENT '学院 ID(关联 sys_dept 表)',
  `status` char(1) DEFAULT '0' COMMENT '状态 (0:草稿 1:院级审核中 2:校级审核中 3:已通过 4:已驳回)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志 (0 代表存在 2 代表删除)',
  `category` char(1) DEFAULT '1' COMMENT '成果类型（关联字典）',
  PRIMARY KEY (`achievement_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb3 COMMENT='教学成果主表';

-- ----------------------------
-- Records of edu_achievement
-- ----------------------------
INSERT INTO `edu_achievement` VALUES ('3', '成果二 （1）11', '成果二', 'http://127.0.0.1:9000/edu-achievement/2026/03/03/张鹏展简历_20260303155917A005.docx,http://127.0.0.1:9000/edu-achievement/2026/03/03/张鹏展简历_20260303155920A006.pdf', '1', '100', '1', 'admin', '2026-03-03 15:59:37', 'admin', '2026-03-10 17:50:36', '0', '1');
INSERT INTO `edu_achievement` VALUES ('4', '全国大学生竞赛三等奖', '获奖', 'http://127.0.0.1:9000/edu-achievement/2026/03/03/张鹏展简历_20260303161514A007.pdf', '1', '100', '3', 'admin', '2026-03-03 16:15:19', 'admin', '2026-03-03 18:24:35', '0', '3');
INSERT INTO `edu_achievement` VALUES ('5', '竞赛奖', '测试', 'http://127.0.0.1:9000/edu-achievement/2026/03/03/职位证明参考模板_20260303162353A010.docx', '100', '101', '2', 'teacher', '2026-03-03 16:24:01', 'admin', '2026-03-03 18:24:33', '0', '3');
INSERT INTO `edu_achievement` VALUES ('7', '2', '333', null, '1', '100', '2', 'admin', '2026-03-03 17:57:11', 'admin', '2026-03-03 18:24:43', '0', '2');
INSERT INTO `edu_achievement` VALUES ('8', '4', '2232', null, '1', '101', '2', 'admin', '2026-03-03 17:57:18', 'admin', '2026-03-03 18:24:29', '0', '1');
INSERT INTO `edu_achievement` VALUES ('9', '1', '3', null, '100', '101', '1', 'teacher', '2026-03-03 17:58:59', 'admin', '2026-03-03 18:24:50', '0', '1');
INSERT INTO `edu_achievement` VALUES ('10', '4', '123', null, '100', '102', '1', 'teacher', '2026-03-03 17:59:05', 'admin', '2026-03-03 18:24:24', '0', '2');
INSERT INTO `edu_achievement` VALUES ('11', '123', '123', null, '100', '100', '1', 'teacher', '2026-03-03 18:05:07', '', null, '0', '4');
INSERT INTO `edu_achievement` VALUES ('13', '123', '1', null, '100', '100', '1', 'teacher', '2026-03-03 19:54:16', 'teacher', '2026-03-03 19:55:19', '0', '2');
INSERT INTO `edu_achievement` VALUES ('14', '竞赛', '111', 'http://127.0.0.1:9000/edu-achievement/2026/03/03/张鹏展简历_20260303200108A012.pdf', '100', '100', '1', 'teacher', '2026-03-03 20:01:11', '', null, '0', '3');
INSERT INTO `edu_achievement` VALUES ('15', '123', '123', null, '1', '100', '1', '', '2026-03-10 15:57:45', '', null, '0', '1');
INSERT INTO `edu_achievement` VALUES ('16', '1345', 'wqe', 'http://127.0.0.1:9000/edu-achievement/2026/03/10/meme_20260310164041A004.txt', '1', '100', '1', '', '2026-03-10 16:40:45', 'admin', '2026-03-10 16:42:01', '0', '1');
INSERT INTO `edu_achievement` VALUES ('18', '123', '123', null, '1', '100', '1', '', '2026-03-10 16:57:05', '', null, '0', '2');
INSERT INTO `edu_achievement` VALUES ('19', '26315t', '1231', null, '1', '103', '4', '', '2026-03-15 16:45:45', 'admin', '2026-03-15 17:22:04', '0', '6');
INSERT INTO `edu_achievement` VALUES ('20', '3151932', 'wda', null, '100', '103', '2', 'teacher', '2026-03-15 19:32:14', 'collegeAudit', '2026-03-15 19:49:10', '0', '2');
