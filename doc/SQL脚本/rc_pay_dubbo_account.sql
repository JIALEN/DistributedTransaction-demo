﻿# Host: 192.168.1.215  (Version 5.6.26-log)
# Date: 2016-09-19 00:27:35
# Generator: MySQL-Front 5.3  (Build 7.5)

/*!40101 SET NAMES utf8 */;

#
# Structure for table "rp_account"
#

DROP TABLE IF EXISTS `rp_account`;
CREATE TABLE `rp_account` (
  `id` varchar(50) NOT NULL,
  `create_time` datetime NOT NULL,
  `edit_time` datetime DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  `remark` varchar(200) DEFAULT NULL,
  `account_no` varchar(50) NOT NULL,
  `balance` decimal(20,6) NOT NULL,
  `unbalance` decimal(20,6) NOT NULL,
  `security_money` decimal(20,6) NOT NULL,
  `status` varchar(36) NOT NULL,
  `total_income` decimal(20,6) NOT NULL,
  `total_expend` decimal(20,6) NOT NULL,
  `today_income` decimal(20,6) NOT NULL,
  `today_expend` decimal(20,6) NOT NULL,
  `account_type` varchar(50) NOT NULL,
  `sett_amount` decimal(20,6) NOT NULL,
  `user_no` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='资金账户表';

#
# Data for table "rp_account"
#

INSERT INTO `rp_account` VALUES ('1715f68276d54027b405bd99d4641cc6','2016-07-21 01:06:51','2016-09-17 17:54:12',0,NULL,'99992016072100000004',0.000000,0.000000,0.000000,'ACTIVE',0.000000,0.000000,0.000000,0.000000,'0',0.000000,'88882016072100000004'),('20b7c1ce75644da6b044437db217ee59','2016-07-19 16:55:09','2016-09-17 17:54:12',0,NULL,'99992016071900000003',0.000000,0.000000,0.000000,'ACTIVE',0.000000,0.000000,0.000000,0.000000,'0',0.000000,'88882016071900000003'),('77842f563a6b4b918b6955b5f07b583c','2016-07-24 20:21:45','2016-09-17 17:54:12',0,NULL,'99992016072400000007',0.000000,0.000000,0.000000,'ACTIVE',0.000000,0.000000,0.000000,0.000000,'0',0.000000,'88882016072400000007'),('a34b5b788c884dadbd640c52b4d7be61','2016-07-24 20:20:26','2016-09-17 17:54:12',0,NULL,'99992016072400000006',0.000000,0.000000,0.000000,'ACTIVE',0.000000,0.000000,0.000000,0.000000,'0',0.000000,'88882016072400000006'),('f79ae95111f6405887b41d000e793552','2016-07-22 15:56:26','2016-09-17 17:54:12',0,NULL,'99992016072200000005',0.000000,0.000000,0.000000,'ACTIVE',0.000000,0.000000,0.000000,0.000000,'0',0.000000,'88882016072200000005');

#
# Structure for table "rp_account_history"
#

DROP TABLE IF EXISTS `rp_account_history`;
CREATE TABLE `rp_account_history` (
  `id` varchar(50) NOT NULL,
  `create_time` datetime NOT NULL,
  `edit_time` datetime DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  `remark` varchar(200) DEFAULT NULL,
  `account_no` varchar(50) NOT NULL,
  `amount` decimal(20,6) NOT NULL,
  `balance` decimal(20,6) NOT NULL,
  `fund_direction` varchar(36) NOT NULL,
  `is_allow_sett` varchar(36) NOT NULL,
  `is_complete_sett` varchar(36) NOT NULL,
  `request_no` varchar(36) NOT NULL,
  `bank_trx_no` varchar(50) DEFAULT NULL,
  `trx_type` varchar(36) NOT NULL,
  `risk_day` int(11) DEFAULT NULL,
  `user_no` varchar(50) DEFAULT NULL,
  `status` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='资金账户流水表';

#
# Data for table "rp_account_history"
#

