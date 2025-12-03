-- 创建数据库
CREATE DATABASE IF NOT EXISTS `campus_second_hand` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `campus_second_hand`;

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `mobile` varchar(20) DEFAULT NULL COMMENT '手机号',
  `school_name` varchar(100) DEFAULT NULL COMMENT '学校',
  `student_id_img` varchar(255) DEFAULT NULL COMMENT '学生证/校园卡照片URL',
  `is_verified` tinyint(1) DEFAULT '0' COMMENT '认证状态 (0:未认证, 1:审核中, 2:已认证, 3:驳回)',
  `dormitory` varchar(50) DEFAULT NULL COMMENT '宿舍楼',
  `address` varchar(255) DEFAULT NULL COMMENT '详细地址',
  `status` tinyint(1) DEFAULT '1' COMMENT '账号状态 (1:正常, 0:禁用)',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除 (0:未删除, 1:已删除)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商品分类表
CREATE TABLE IF NOT EXISTS `biz_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序权重',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态 (1:启用, 0:禁用)',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除 (0:未删除, 1:已删除)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品主表
CREATE TABLE IF NOT EXISTS `biz_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `seller_id` bigint(20) NOT NULL COMMENT '卖家ID',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `title` varchar(100) NOT NULL COMMENT '标题',
  `description` text COMMENT '商品详情描述',
  `price` decimal(10,2) NOT NULL COMMENT '售价',
  `original_price` decimal(10,2) DEFAULT '0.00' COMMENT '原价',
  `location` varchar(100) DEFAULT NULL COMMENT '交易地点',
  `tags` varchar(500) DEFAULT NULL COMMENT '标签JSON',
  `images` text DEFAULT NULL COMMENT '图片JSON',
  `delivery_type` tinyint(1) DEFAULT '1' COMMENT '交易方式 (1:自提, 2:送货上门, 3:邮寄)',
  `view_count` int(11) DEFAULT '0' COMMENT '浏览量',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态 (0:在售, 1:交易中, 2:已售出, 3:下架)',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除 (0:未删除, 1:已删除)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_seller` (`seller_id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 商品图片表
CREATE TABLE IF NOT EXISTS `biz_goods_image` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `goods_id` bigint(20) NOT NULL COMMENT '商品ID',
  `url` varchar(255) NOT NULL COMMENT '图片地址',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  PRIMARY KEY (`id`),
  KEY `idx_goods` (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图片表';

-- 交易订单表
CREATE TABLE IF NOT EXISTS `biz_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `goods_id` bigint(20) NOT NULL COMMENT '商品ID',
  `buyer_id` bigint(20) NOT NULL COMMENT '买家ID',
  `seller_id` bigint(20) NOT NULL COMMENT '卖家ID',
  `amount` decimal(10,2) NOT NULL COMMENT '成交金额',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态 (0:待交易, 1:已完成, 2:已取消)',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_buyer` (`buyer_id`),
  KEY `idx_seller` (`seller_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易订单表';

-- 互动留言表
CREATE TABLE IF NOT EXISTS `biz_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `goods_id` bigint(20) NOT NULL COMMENT '商品ID',
  `user_id` bigint(20) NOT NULL COMMENT '留言用户ID',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父留言ID',
  `reply_user_id` bigint(20) DEFAULT NULL COMMENT '被回复用户ID',
  `content` varchar(500) NOT NULL COMMENT '留言内容',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除 (0:未删除, 1:已删除)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '留言时间',
  PRIMARY KEY (`id`),
  KEY `idx_goods` (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='互动留言表';

-- 收藏表
CREATE TABLE IF NOT EXISTS `biz_favorite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `goods_id` bigint(20) NOT NULL COMMENT '商品ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 私聊消息表
CREATE TABLE IF NOT EXISTS `biz_chat` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sender_id` bigint(20) NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint(20) NOT NULL COMMENT '接收者ID',
  `content` varchar(1000) DEFAULT NULL COMMENT '消息内容',
  `msg_type` tinyint(4) DEFAULT '0' COMMENT '消息类型 0:文本 1:图片',
  `is_read` tinyint(1) DEFAULT '0' COMMENT '是否已读',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`),
  KEY `idx_sender_receiver` (`sender_id`,`receiver_id`),
  KEY `idx_receiver_sender` (`receiver_id`,`sender_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='私聊消息表';

-- 插入一些测试分类数据
INSERT INTO `biz_category` (`name`, `icon`, `sort_order`) VALUES 
('图书教材', 'book', 1),
('数码产品', 'device-tablet', 2),
('校园代步', 'bicycle', 3),
('生活日用', 'coffee', 4),
('美妆护肤', 'sparkles', 5),
('运动健身', 'trophy', 6);


-- 管理员表
CREATE TABLE IF NOT EXISTS `sys_admin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态 (1:正常, 0:禁用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 插入默认管理员 (密码: 123456)
INSERT INTO `sys_admin` (`username`, `password`, `nickname`) VALUES 
('admin', '111111', '超级管理员');
