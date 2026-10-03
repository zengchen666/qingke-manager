-- ============================================================
-- 轻客管家（qk）数据库初始化脚本
-- 执行方式（MySQL 8）：
--   mysql -uroot -p123456 < sql/qk.sql
-- 或在 MySQL 客户端中：source 绝对路径/qk.sql
--
-- 相对原文档的修正（原脚本会执行失败或产生脏数据的地方）：
--   1. role 表：remark 原定义为 varchar(10) not null unique，
--      但初始化数据里多行 remark 都是空串 ''，会违反唯一约束 → 去掉 unique/not null，长度放宽到 100
--   2. role 表 INSERT：原语句列名处写了两个连续逗号 `(id, name, ,label,remark, ...)` → 语法错误，已修正
--   3. role 表数据：原数据第 3 行 create_time 写成 '26-04-26 ...'（缺少年份） → 修正为 2026
--   4. 脚本末尾额外提供「测试账号密码重置」语句，方便联调登录
-- ============================================================

CREATE DATABASE IF NOT EXISTS qk DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE qk;

-- ----------------------------
-- 部门表
-- ----------------------------
DROP TABLE IF EXISTS dept;
create table dept (
    id int unsigned primary key auto_increment comment '部门id，主键',
    name varchar(10) not null unique comment '部门名称',
    status tinyint unsigned not null default 1 comment '状态：0-停用，1-正常',
    create_time datetime not null comment '创建时间',
    update_time datetime not null comment '修改时间'
) comment '部门信息表';

INSERT INTO dept (id, name, status, create_time, update_time) VALUES
(1,'研发部',1,'2025-04-26 15:45:31','2025-07-26 15:27:40'),
(2,'市场部',1,'2025-04-26 15:45:31','2025-07-26 15:27:40'),
(3,'销售一部',1,'2025-04-26 15:45:31','2025-07-26 15:27:46'),
(4,'人力资源部',1,'2025-04-26 15:45:31','2025-07-26 15:27:46'),
(5,'财务部',1,'2025-04-26 15:45:31','2025-07-26 15:28:04'),
(6,'客服部',0,'2025-04-26 15:45:31','2025-07-26 15:28:04'),
(7,'技术支持部',1,'2025-04-26 15:45:31','2025-07-26 15:28:08'),
(8,'产品部',1,'2025-04-26 15:45:31','2025-07-26 15:28:08'),
(9,'运营部',0,'2025-04-26 15:45:31','2025-07-26 15:28:13'),
(10,'采购部',1,'2025-04-26 15:45:31','2025-07-26 15:28:13'),
(11,'法务部',0,'2025-04-26 15:45:31','2025-07-26 15:28:18'),
(12,'设计部',1,'2025-04-26 15:45:31','2025-07-26 15:28:18'),
(13,'公关部',0,'2025-04-26 15:45:31','2025-07-26 15:28:22'),
(14,'质量部',1,'2025-04-26 15:45:31','2025-07-26 15:28:22'),
(15,'战略部',1,'2025-04-26 15:45:31','2025-07-26 15:28:25'),
(16,'市场一部',1,'2025-07-09 11:32:42','2025-07-26 15:28:25');

-- ----------------------------
-- 角色表
-- ----------------------------
DROP TABLE IF EXISTS role;
create table role (
    id int unsigned primary key auto_increment comment '角色id，主键',
    name varchar(10) not null unique comment '角色名称',
    label varchar(20) not null unique comment '角色标识',
    remark varchar(100) comment '角色描述',
    create_time datetime not null comment '创建时间',
    update_time datetime not null comment '修改时间'
) comment '角色信息表';

INSERT INTO role (id, name, label, remark, create_time, update_time) VALUES
(1,'管理员','admin','系统管理员','2026-04-26 15:45:31','2026-07-26 15:27:40'),
(2,'线索专员','line','负责线索跟进','2026-04-26 15:45:31','2026-07-26 15:27:40'),
(3,'高级专员','high','负责商机跟进','2026-04-26 15:45:31','2026-07-26 15:27:46'),
(4,'商机专员','business','负责商机转化','2026-04-26 15:45:31','2026-07-26 15:27:46'),
(5,'普通员工','normal','普通员工','2026-04-26 15:45:31','2026-07-26 15:28:04');

-- ----------------------------
-- 用户表
-- ----------------------------
DROP TABLE IF EXISTS user;
create table user (
    id int unsigned primary key auto_increment comment 'id, 主键',
    username varchar(20) not null unique comment '用户名，唯一',
    password varchar(64) not null comment '密码',
    name varchar(20) not null comment '姓名',
    phone char(11) not null unique comment '手机号，唯一',
    email varchar(50) not null unique comment '邮箱，唯一',
    gender tinyint unsigned not null comment '性别，1: 男，2: 女',
    status tinyint unsigned not null comment '状态，1: 正常，0: 停用',
    dept_id int unsigned comment '部门id，关联部门表主键',
    role_id int unsigned comment '角色id，关联角色表主键',
    image varchar(255) comment '头像url',
    remark varchar(50) comment '备注，50字以内',
    create_time datetime not null comment '创建时间',
    update_time datetime not null comment '修改时间'
) comment '用户表';

INSERT INTO user (id, username, password, name, phone, email, gender, status, dept_id, role_id, image, remark, create_time, update_time) VALUES
    (1,'zhangsan','4e7bdb88640b376ac6646b8f1ecfb558','张三','13800138001','admin@example.com',1,1,6,1,'https://web2025-test.oss-cn-beijing.aliyuncs.com/ee089310-479b-4672-a74a-dc680fa6a18a.png','系统管理员','2025-05-12 11:39:03','2025-06-21 15:25:55'),
    (2,'lisi','c3cb6d12c40908943b64bc0681af47db','李四','13800138002','editor1@example.com',1,1,4,2,'https://web2025-test.oss-cn-beijing.aliyuncs.com/62fcb582-074c-489a-8ea8-721bc8bba042.png','内容编辑','2025-05-12 11:39:03','2025-06-21 15:26:00'),
    (3,'sunwuji','368a7fccd730d807f41e2161798e13ca','孙无忌','18809091111','sunwuji@163.com',1,1,5,2,'https://web2025-test.oss-cn-beijing.aliyuncs.com/e65781f3-c87d-46e2-9d53-cf4329ce64e6.png','孙无忌刚入职，先在销售部磨炼磨炼','2025-05-12 16:46:21','2025-07-14 18:13:45'),
    (4,'songjiang','a91b1cfe31e6b534537ab992e06380f8','宋江','13800000001','songjiang@example.com',1,1,5,2,'https://web2025-test.oss-cn-beijing.aliyuncs.com/0bdd90db-17e1-407c-b922-27d987c9cc45.png','Remark 1','2025-05-14 15:49:31','2025-07-14 18:13:45'),
    (5,'lujunyi','ef73dd11149e096116f755a171eb3fec','卢俊义','13800000002','lujunyi@example.com',1,1,3,3,'https://web2025-test.oss-cn-beijing.aliyuncs.com/1564f4d9-7d07-4ea4-9e23-ecebe39d3520.png','Remark 2','2025-05-14 15:49:31','2025-07-14 18:13:45'),
    (6,'wuyong','d74bcf7b7f805922e4ad42315f3a8cde','吴用','13800000003','wuyong@example.com',1,0,7,2,'https://web2025-test.oss-cn-beijing.aliyuncs.com/cb074a33-4f30-4b3e-b9f1-bf8bf9d0f4ff.png','Remark 3','2025-05-14 15:49:31','2025-07-14 18:13:45'),
    (7,'gongsunsheng','ee3a229f2f3d2bd7c9dd3eeb4eda02b3','公孙胜','13800000004','gongsunsheng@example.com',1,1,5,2,'https://web2025-test.oss-cn-beijing.aliyuncs.com/284efce8-0b0c-4076-bbf6-3e104446ce70.png','Remark 4','2025-05-14 15:49:31','2025-07-14 18:13:45'),
    (8,'linchong','786271307576f1ee762341173a2caa48','林冲','13800000006','linchong@example.com',1,1,4,3,'https://web2025-test.oss-cn-beijing.aliyuncs.com/d2fbc991-c6de-4871-bd91-4a7d186b1f90.png','Remark 6','2025-05-14 15:49:31','2025-07-14 18:13:45'),
    (9,'qinming','23b9c4ea1f4d1844acd20a5314da8553','秦明','13800000007','qinming@example.com',1,1,5,2,'https://web2025-test.oss-cn-beijing.aliyuncs.com/d87de2cc-79a4-4a56-af9c-eb6aca52db66.png','Remark 7','2025-05-14 15:49:31','2025-07-14 18:13:45'),
    (10,'huarong','19d59a4e1e63552228b73b2cb2b5fcdd','花荣','13800000008','huayong@example.com',1,1,6,1,'https://web2025-test.oss-cn-beijing.aliyuncs.com/4775c1a5-4944-4aec-9753-9fa622dbbe75.png','Remark 8','2025-05-14 15:49:31','2025-07-14 18:13:45'),
    (11,'huyan','fbb39f3019cc4f5444dbd6b623f71a1a','呼延灼','13800000009','huyan@example.com',1,0,8,2,'https://web2025-test.oss-cn-beijing.aliyuncs.com/446aa6db-85b0-451f-bc11-92a52a518192.png','Remark 9','2025-05-14 15:49:31','2025-07-14 18:13:45'),
    (12,'huaqian','1007f13423a57bc0390d9510f96d0322','花千朵','13800000010','huaqian@example.com',2,1,10,3,'https://web2025-test.oss-cn-beijing.aliyuncs.com/63137d09-0734-4910-9502-b9b8d063b132.png','Remark 10','2025-05-14 15:49:31','2025-07-14 18:13:45'),
    (13,'shaqianmo','29d6fb85ac49d6d82b9b6dfd5f62939a','杀阡陌','13509091456','shaqianmo@163.com',0,1,5,2,'https://web2025-test.oss-cn-beijing.aliyuncs.com/ff8646a3-629b-4124-a2cb-f0365f9075c2.png','','2025-06-19 17:48:39','2025-07-14 18:13:45'),
    (14,'baizihua','f1659bd8a56022d817d6bd124c6181e8','白子画','13609091206','baizihua@163.com',1,1,3,3,'https://web2025-test.oss-cn-beijing.aliyuncs.com/421c0945-6f25-405c-b439-9b8b997338aa.png','系统研发','2025-06-22 14:33:49','2025-07-14 18:13:45'),
    (15,'huaqiangu','be6485aab12634baf922b3dbe407fcda','花千骨','15809092819','huaqiangu@163.com',0,1,6,3,'https://web2025-test.oss-cn-beijing.aliyuncs.com/735320dc-ac08-4b73-bdd7-7f0cf054274f.png','花千骨是一个普通用户','2025-06-24 10:40:20','2025-07-14 18:13:45'),
    (16,'kuangyetian','5e3c47c0d266439d7ce704972c92086a','旷野天','13689281921','kuangyetian@163.com',1,1,9,3,'https://web2025-test.oss-cn-beijing.aliyuncs.com/5d1f8e0f-2227-4469-8c90-7a02867fbe70.png','新入职的技术员旷野天','2025-06-27 19:38:34','2025-07-14 18:13:45');

-- ----------------------------
-- 线索表
-- ----------------------------
DROP TABLE IF EXISTS clue;
CREATE TABLE clue (
    id int unsigned PRIMARY KEY AUTO_INCREMENT COMMENT '线索ID, 主键',
    phone char(11) NOT NULL UNIQUE COMMENT '手机号',
    channel tinyint unsigned NOT NULL COMMENT '渠道来源，1:线上活动, 2:推广介绍',
    activity_id int unsigned COMMENT '活动信息，关联活动的ID',
    name varchar(20) COMMENT '客户姓名',
    gender tinyint unsigned COMMENT '性别，1:男, 2:女',
    age tinyint unsigned COMMENT '年龄',
    wechat varchar(50) COMMENT '微信号',
    qq varchar(20) COMMENT 'QQ号',
    user_id int unsigned COMMENT '归属人ID，关联用户ID',
    status tinyint unsigned NOT NULL DEFAULT '1' COMMENT '线索状态，1:待分配, 2:待跟进, 3:跟进中, 4:伪线索, 5:转为商机',
    subject tinyint unsigned COMMENT '意向学科，1:AI智能应用开发(Java), 2:AI大模型开发(Python)，3:AI鸿蒙开发，4:AI大数据，5:AI嵌入式，6:AI测试，7:AI运维',
    level tinyint unsigned COMMENT '意向等级, 1:近期学习、2:打算学习(考虑中)、3:进行了解、4:打酱油',
    next_time datetime COMMENT '下次跟进时间',
    create_time datetime not null comment '创建时间',
    update_time datetime not null comment '修改时间'
) COMMENT '线索表';

INSERT INTO clue (id, phone, channel, activity_id, name, gender, age, wechat, qq, user_id, status, subject, level, next_time, create_time, update_time) VALUES
  (1,'14700000001',2,3,'张思',1,23,'wx2324342323','3434343423',2,3,NULL,NULL,'2025-06-25 10:00:00','2025-05-24 17:13:50','2025-06-21 15:11:34'),
  (2,'13589890912',1,2,'李久阶',1,22,'wx28392839','2323232323',NULL,1,1,NULL,NULL,'2025-05-24 17:16:03','2025-06-21 15:08:16'),
  (3,'13909120913',2,3,'李迪',1,22,'wx17327323','2456754323',NULL,1,1,NULL,NULL,'2025-05-28 11:54:02','2025-07-20 18:08:18'),
  (4,'13688889991',1,2,'钱四',1,21,'wxdjjd92922','2345643236',NULL,1,1,NULL,NULL,'2025-05-28 14:16:55','2025-06-10 21:15:03'),
  (5,'15509091231',1,4,'欧斯卡',2,22,'wx283232423','23456789657',NULL,1,NULL,NULL,NULL,'2025-05-28 19:41:46','2025-05-29 09:48:05'),
  (6,'17709092901',1,10,'奚梦',1,22,'jsdhfsf2324','3456789432',NULL,1,1,NULL,NULL,'2025-05-28 19:44:15','2025-06-10 21:04:18'),
  (7,'13589898881',2,11,'伊斯科',1,19,'wx8943895345','34343232536',NULL,1,2,NULL,NULL,'2025-05-28 19:45:06','2025-06-09 14:42:38'),
  (8,'13511110000',2,13,'齐欧式',1,30,'wxqi299232','2435676543',NULL,1,2,NULL,NULL,'2025-05-29 10:45:07','2025-06-22 14:52:08'),
  (9,'13511110001',1,2,'李秋菊',2,22,'liqiuju23874','234567865',NULL,1,1,NULL,NULL,'2025-05-29 10:47:22','2025-06-22 14:51:37'),
  (10,'13398980102',1,2,'张岱',1,22,'wx13728785434','234567543',NULL,1,1,NULL,NULL,'2025-06-16 15:31:22','2025-07-20 20:59:46'),
  (11,'15508761231',1,1,'卫丹',2,24,'wxweidan1212','8450313640',NULL,1,NULL,NULL,NULL,'2025-07-16 18:46:10','2025-07-20 15:47:32'),
  (12,'17792098192',2,3,'张吉',1,21,'wx289483544','245676856',NULL,1,NULL,NULL,NULL,'2025-07-20 15:17:56','2025-07-20 15:43:56');

-- ----------------------------
-- 线索跟进记录表
-- ----------------------------
DROP TABLE IF EXISTS clue_track_record;
CREATE TABLE clue_track_record (
    id int unsigned PRIMARY KEY AUTO_INCREMENT COMMENT '跟进记录ID, 主键',
    clue_id int unsigned NOT NULL COMMENT '线索ID，关联线索ID',
    user_id int unsigned NOT NULL COMMENT '跟进人ID，关联用户ID',
    subject tinyint unsigned COMMENT '意向学科，1:AI智能应用开发(Java), 2:AI大模型开发(Python)，3:AI鸿蒙开发，4:AI大数据，5:AI嵌入式，6:AI测试，7:AI运维',
    level tinyint unsigned COMMENT '意向等级, 1:近期学习、2:打算学习(考虑中)、3:进行了解、4:打酱油',
    record varchar(100) COMMENT '跟进记录',
    next_time datetime COMMENT '下次跟进时间',
    type tinyint unsigned COMMENT '跟进类型, 1:正常跟进、0:伪线索',
    false_reason tinyint unsigned COMMENT '伪线索原因, 1:空号、2:停机、3:竞品、4:无法联系、5:其他',
    create_time datetime not null comment '创建时间'
) COMMENT '线索跟进记录表';

INSERT INTO clue_track_record (id, clue_id, user_id, subject, level, record, next_time, type, false_reason, create_time) VALUES
  (1,1,2,1,1,'无','2025-05-29 10:00:00',1,NULL,'2025-05-29 10:25:00'),
  (2,1,2,1,1,'有意向,目前大三','2025-05-30 10:00:00',1,NULL,'2025-05-29 10:25:00');

-- ----------------------------
-- 商机表
-- ----------------------------
DROP TABLE IF EXISTS business;
create table business (
    id int unsigned not null primary key auto_increment comment '商机id, 主键',
    name varchar(20) comment '客户姓名',
    phone char(11) not null unique comment '手机号',
    gender tinyint unsigned comment '性别，1:男, 2:女',
    age tinyint unsigned comment '年龄',
    wechat varchar(30) comment '微信号',
    qq varchar(15) comment 'qq号',
    subject tinyint unsigned comment '意向学科，1:ai智能应用开发(java), 2:ai大模型开发(python)，3:ai鸿蒙开发，4:ai大数据，5:ai嵌入式，6:ai测试，7:ai运维',
    course_id int unsigned comment '意向课程, 课程id',
    degree tinyint unsigned comment '学历, 1:高中、2:中专、3:大专、4:本科、5:硕士、6:博士、7:其他',
    job_status tinyint unsigned comment '在职情况, 1: 在职, 0: 离职',
    channel tinyint unsigned not null comment '渠道来源，1:线上活动, 2:推广介绍',
    remark varchar(50) comment '备注',
    status tinyint unsigned not null default '1' comment '商机状态，1:待分配, 2:待跟进, 3:跟进中, 4:回收, 5:转客户',
    user_id int unsigned comment '归属人id，关联用户id',
    clue_id int unsigned comment '归属线索id',
    next_time datetime comment '下次跟进时间',
    create_time datetime not null comment '创建时间',
    update_time datetime not null comment '修改时间'
) comment '商机表';

-- ----------------------------
-- 商机跟进记录表
-- ----------------------------
DROP TABLE IF EXISTS business_track_record;
create table business_track_record (
    id int unsigned primary key auto_increment comment '跟进记录id, 主键',
    business_id int unsigned not null comment '商机id，关联商机id',
    user_id int unsigned not null comment '跟进人id，关联用户id',
    track_status tinyint unsigned not null comment '跟进状态, 1:接通, 2:拒绝, 3:无人接听',
    key_items varchar(50) comment '沟通重点',
    next_time datetime comment '下次跟进时间',
    record varchar(50) comment '沟通纪要',
    create_time datetime not null comment '创建时间'
) comment '商机跟进记录表';

-- ----------------------------
-- 联调用：重置测试账号密码
-- 登录接口会把前端传的明文做 MD5 后与库中密文比对；
-- 新增用户的默认密码规则是「用户名 + 123」，这里按同样规则重置，方便直接用明文登录。
-- zhangsan → 明文 zhangsan123（角色：管理员 admin）
-- lisi     → 明文 lisi123    （角色：线索专员 line）
-- ----------------------------
UPDATE user SET password = MD5('zhangsan123') WHERE username = 'zhangsan';
UPDATE user SET password = MD5('lisi123')     WHERE username = 'lisi';
UPDATE user SET password = MD5('sunwuji123')  WHERE username = 'sunwuji';

-- 校验
SELECT id, username, name, status, role_id, password FROM user WHERE username IN ('zhangsan','lisi','sunwuji');
