create database if not exists seckill_order_1 default charset = utf8mb4;

use seckill_order_1;

-- ----------------------------
-- Table structure for seckill_goods_order_0
-- ----------------------------
DROP TABLE IF EXISTS `seckill_goods_order_0`;
CREATE TABLE `seckill_goods_order_0`  (
                                          `id` bigint(20) NOT NULL COMMENT '订单id',
                                          `user_id` bigint(20) NULL DEFAULT 0 COMMENT '用户id',
                                          `goods_id` bigint(20) NULL DEFAULT 0 COMMENT '商品id',
                                          `goods_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '商品名称',
                                          `activity_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '秒杀价格',
                                          `quantity` int(10) NULL DEFAULT 0 COMMENT '下单商品数量',
                                          `order_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '订单总金额',
                                          `activity_id` bigint(20) NULL DEFAULT 0 COMMENT '活动id',
                                          `status` int(2) NULL DEFAULT 0 COMMENT '订单状态 1：已创建 2：已支付 0：已取消； -1：已删除',
                                          `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          INDEX `goods_id_index`(`goods_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '秒杀订单表';

-- ----------------------------
-- Table structure for seckill_goods_order_1
-- ----------------------------
DROP TABLE IF EXISTS `seckill_goods_order_1`;
CREATE TABLE `seckill_goods_order_1`  (
                                          `id` bigint(20) NOT NULL COMMENT '订单id',
                                          `user_id` bigint(20) NULL DEFAULT 0 COMMENT '用户id',
                                          `goods_id` bigint(20) NULL DEFAULT 0 COMMENT '商品id',
                                          `goods_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '商品名称',
                                          `activity_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '秒杀价格',
                                          `quantity` int(10) NULL DEFAULT 0 COMMENT '下单商品数量',
                                          `order_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '订单总金额',
                                          `activity_id` bigint(20) NULL DEFAULT 0 COMMENT '活动id',
                                          `status` int(2) NULL DEFAULT 0 COMMENT '订单状态 1：已创建 2：已支付 0：已取消； -1：已删除',
                                          `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          INDEX `goods_id_index`(`goods_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '秒杀订单表';

-- ----------------------------
-- Table structure for seckill_goods_order_2
-- ----------------------------
DROP TABLE IF EXISTS `seckill_goods_order_2`;
CREATE TABLE `seckill_goods_order_2`  (
                                          `id` bigint(20) NOT NULL COMMENT '订单id',
                                          `user_id` bigint(20) NULL DEFAULT 0 COMMENT '用户id',
                                          `goods_id` bigint(20) NULL DEFAULT 0 COMMENT '商品id',
                                          `goods_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '商品名称',
                                          `activity_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '秒杀价格',
                                          `quantity` int(10) NULL DEFAULT 0 COMMENT '下单商品数量',
                                          `order_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '订单总金额',
                                          `activity_id` bigint(20) NULL DEFAULT 0 COMMENT '活动id',
                                          `status` int(2) NULL DEFAULT 0 COMMENT '订单状态 1：已创建 2：已支付 0：已取消； -1：已删除',
                                          `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          INDEX `goods_id_index`(`goods_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '秒杀订单表';

-- ----------------------------
-- Table structure for seckill_user_order_0
-- ----------------------------
DROP TABLE IF EXISTS `seckill_user_order_0`;
CREATE TABLE `seckill_user_order_0`  (
                                         `id` bigint(20) NOT NULL COMMENT '订单id',
                                         `user_id` bigint(20) NULL DEFAULT 0 COMMENT '用户id',
                                         `goods_id` bigint(20) NULL DEFAULT 0 COMMENT '商品id',
                                         `goods_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '商品名称',
                                         `activity_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '秒杀价格',
                                         `quantity` int(10) NULL DEFAULT 0 COMMENT '下单商品数量',
                                         `order_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '订单总金额',
                                         `activity_id` bigint(20) NULL DEFAULT 0 COMMENT '活动id',
                                         `status` int(2) NULL DEFAULT 0 COMMENT '订单状态 1：已创建 2：已支付 0：已取消； -1：已删除',
                                         `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         INDEX `user_id_index`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '秒杀订单表';

-- ----------------------------
-- Table structure for seckill_user_order_1
-- ----------------------------
DROP TABLE IF EXISTS `seckill_user_order_1`;
CREATE TABLE `seckill_user_order_1`  (
                                         `id` bigint(20) NOT NULL COMMENT '订单id',
                                         `user_id` bigint(20) NULL DEFAULT 0 COMMENT '用户id',
                                         `goods_id` bigint(20) NULL DEFAULT 0 COMMENT '商品id',
                                         `goods_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '商品名称',
                                         `activity_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '秒杀价格',
                                         `quantity` int(10) NULL DEFAULT 0 COMMENT '下单商品数量',
                                         `order_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '订单总金额',
                                         `activity_id` bigint(20) NULL DEFAULT 0 COMMENT '活动id',
                                         `status` int(2) NULL DEFAULT 0 COMMENT '订单状态 1：已创建 2：已支付 0：已取消； -1：已删除',
                                         `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         INDEX `user_id_index`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '秒杀订单表';

-- ----------------------------
-- Table structure for seckill_user_order_2
-- ----------------------------
DROP TABLE IF EXISTS `seckill_user_order_2`;
CREATE TABLE `seckill_user_order_2`  (
                                         `id` bigint(20) NOT NULL COMMENT '订单id',
                                         `user_id` bigint(20) NULL DEFAULT 0 COMMENT '用户id',
                                         `goods_id` bigint(20) NULL DEFAULT 0 COMMENT '商品id',
                                         `goods_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '商品名称',
                                         `activity_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '秒杀价格',
                                         `quantity` int(10) NULL DEFAULT 0 COMMENT '下单商品数量',
                                         `order_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '订单总金额',
                                         `activity_id` bigint(20) NULL DEFAULT 0 COMMENT '活动id',
                                         `status` int(2) NULL DEFAULT 0 COMMENT '订单状态 1：已创建 2：已支付 0：已取消； -1：已删除',
                                         `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         INDEX `user_id_index`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '秒杀订单表';

