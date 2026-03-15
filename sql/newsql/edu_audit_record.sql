/*
Navicat MySQL Data Transfer

Source Server         : BS
Source Server Version : 80033
Source Host           : localhost:3306
Source Database       : edu_system

Target Server Type    : MYSQL
Target Server Version : 80033
File Encoding         : 65001

Date: 2026-03-15 17:00:00
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for edu_audit_record
-- ----------------------------
DROP TABLE IF EXISTS `edu_audit_record`;
CREATE TABLE `edu_audit_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '审核记录 ID',
  `achievement_id` bigint NOT NULL COMMENT '关联成果 ID',
  `audit_level` char(1) NOT NULL COMMENT '审核级别 (1:院级审核 2:校级审核)',
  `audit_result` char(1) NOT NULL COMMENT '审核结果 (1:通过 2:驳回)',
  `audit_opinion` varchar(500) DEFAULT '' COMMENT '审核意见',
  `auditor_id` bigint NOT NULL COMMENT '审核人 ID (关联 sys_user 表)',
  `auditor_name` varchar(64) DEFAULT '' COMMENT '审核人姓名',
  `create_time` datetime DEFAULT NULL COMMENT '审核时间',
  PRIMARY KEY (`record_id`),
  KEY `idx_achievement_id` (`achievement_id`),
  KEY `idx_auditor_id` (`auditor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3 COMMENT='审核记录表';
