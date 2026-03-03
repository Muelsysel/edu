/*
Navicat MySQL Data Transfer

Source Server         : BS
Source Server Version : 80033
Source Host           : localhost:3306
Source Database       : edu_system

Target Server Type    : MYSQL
Target Server Version : 80033
File Encoding         : 65001

Date: 2026-03-03 17:02:29
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
  `category` char(1) DEFAULT '1' COMMENT '成果类型（1论文 2教材 3竞赛 4教改）',
  PRIMARY KEY (`achievement_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3 COMMENT='教学成果主表';

-- ----------------------------
-- Records of edu_achievement
-- ----------------------------
INSERT INTO `edu_achievement` VALUES ('1', '111', null, 'http://127.0.0.1:9000/edu-achievement/2026/03/03/张鹏展简历_20260303143855A001.docx', '123', '123', '0', '', '2026-03-03 14:39:05', '', null, '0', '1');
INSERT INTO `edu_achievement` VALUES ('2', '成果1', '这是介绍', 'http://127.0.0.1:9000/edu-achievement/2026/03/03/张鹏展简历_20260303154953A004.pdf', '1', '100', '0', 'admin', '2026-03-03 15:53:10', '', null, '0', '1');
INSERT INTO `edu_achievement` VALUES ('3', '成果二', '成果二', 'http://127.0.0.1:9000/edu-achievement/2026/03/03/张鹏展简历_20260303155917A005.docx,http://127.0.0.1:9000/edu-achievement/2026/03/03/张鹏展简历_20260303155920A006.pdf', '1', '100', '0', 'admin', '2026-03-03 15:59:37', '', null, '0', '1');
INSERT INTO `edu_achievement` VALUES ('4', '全国大学生竞赛三等奖', '获奖', 'http://127.0.0.1:9000/edu-achievement/2026/03/03/张鹏展简历_20260303161514A007.pdf', '1', '100', '0', 'admin', '2026-03-03 16:15:19', '', null, '0', '3');
INSERT INTO `edu_achievement` VALUES ('5', '竞赛奖', '测试', 'http://127.0.0.1:9000/edu-achievement/2026/03/03/职位证明参考模板_20260303162353A010.docx', '100', '101', '0', 'teacher', '2026-03-03 16:24:01', '', null, '0', '3');
