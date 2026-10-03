# 轻客管家 · QingKe Manager

> 一个轻量级的客户线索管理系统：把「线索获取 → 分配 → 跟进 → 转商机」这条业务链路做成可追溯的闭环，
> 配套部门 / 用户的系统管理能力，以及首页经营概览。

后端 Java 技术栈 + 前端 Vue 全栈单仓库，非商业用途的课程项目复现。

---

## 目录

- [技术栈](#技术栈)
- [快速开始](#快速开始)
- [功能一览](#功能一览)
- [值得一看的实现](#值得一看的实现)
- [项目结构](#项目结构)
- [环境变量](#环境变量)
- [已知限制](#已知限制)
- [说明与免责](#说明与免责)

---

## 技术栈

| 层次 | 选型 | 说明 |
|---|---|---|
| 后端框架 | Spring Boot 4.0.8（Spring Framework 7.0.9） | 首次维护该大版本，踩过的坑见「已知限制」 |
| 持久层 | MyBatis-Plus 3.5.17 + PageHelper 2.1.0 | MP 负责单表 CRUD 与分页，PageHelper 负责条件分页 |
| 数据库 | MySQL 8.0 | 6 张业务表 |
| 鉴权 | JJWT 0.12.6 | HS256，对称加密，12 小时有效期 |
| 构建 | Maven 多模块 | 父工程统一管理依赖版本，子模块按职责拆分 |
| 前端框架 | Vue 3.5 + Vite 6.4 | 组合式 API + `<script setup>` |
| UI 组件 | Element Plus 2.14 | 深色侧边栏 + 卡片式后台布局 |
| 路由 / 请求 | vue-router 4.6（hash 模式）/ axios 1.20 | 静态部署不需要服务端路由规则 |
| JDK | 编译目标 17（实测运行于 JDK 25） | |

---

## 快速开始

### 1. 初始化数据库

```sql
-- 执行 backend/sql/qk.sql（建库 qk + 6 张表 + 初始化数据）
```

脚本里已内置 16 个部门、5 个角色、16 个用户和 12 条线索，无需自己造测试数据。

### 2. 启动后端

```bash
cd backend
mvn -pl qk-management install -DskipTests
java -jar qk-management/target/qk-management-1.0-SNAPSHOT.jar
```

看到 `Tomcat started on port 8080` 即启动成功。

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

访问 **http://localhost:90**

### 4. 测试账号

| 用户名 | 密码 | 角色 |
|---|---|---|
| `zhangsan` | `zhangsan123` | 管理员（可看全部） |
| `lisi` | `lisi123` | 线索专员（分配线索时会用到） |

> 新建用户的初始密码规则为「用户名 + 123」，由后端 MD5 加密后入库（MD5 只能加密不能解密，故密码不可反查）。

### 5. 生产部署时的反向代理

前端是静态资源，用 Nginx 托管并把 `/api/` 转发给后端即可，参考 `backend/nginx.conf`：

```nginx
location ^~ /api/ {
    rewrite ^/api/(.*)$ /$1 break;
    proxy_pass http://localhost:8080;
}
```

> 开发期不需要 Nginx —— Vite 已内置同样的代理（`vite.config.js` 的 `server.proxy`）。

---

## 功能一览

| 模块 | 能力 |
|---|---|
| 登录认证 | 用户名 + 密码 + 账号状态三重校验，签发 JWT；请求头带 `token` 放行 |
| 首页概览 | 线索 6 状态 + 商机 6 状态计数，卡片式展示 |
| 线索管理 | 条件 + 分页列表、详情（跟进记录时间线）、分配、跟进、转商机 |
| 部门管理 | 条件分页、增删改查（更新为动态 SQL） |
| 用户管理 | 联表查询（含部门名 / 角色名）、新增（密码加密）、批量删除、按角色标识查人 |

线索状态流转：`待分配 → 待跟进 → 跟进中`，另有 `伪线索` 与 `转为商机` 两个终态。

---

## 值得一看的实现

写在这里的每一条都对应仓库里真实存在的代码，且都遇到过具体问题。

### 1. 鉴权链路：JWT + ThreadLocal

```
TokenFilter ──解析 token──> 取 userId ──> ThreadLocal
     ↓
Controller / Service ──CurrentUserHolder.getCurrentUser()──> 拿到当前登录人
     ↓
finally ──removeCurrentUser()──> 防线程复用串号
```

**为什么要有 ThreadLocal**：跟进线索时必须记录「是谁跟进的」，而 JWT 里带的是用户信息，
但把用户 ID 一路当参数往下传会让每个方法签名都多一个参数。写入 ThreadLocal 后，
业务层随时可取，代价是**必须在请求结束时清理** —— Tomcat 线程池会复用线程，
不清理就会把上一个请求的用户带给下一个请求。因此清理动作放在 `finally` 里，而不是 `doFilter` 末尾。

### 2. 跟进操作用事务保证一致性

「跟进线索」实际是两次数据库操作：更新线索基本信息 + 新增一条跟进记录。
用 `@Transactional(rollbackFor = Exception.class)` 包住，避免第一步成功第二步失败导致数据不一致。

### 3. 动态 SQL：更新不能写死字段

部门更新如果用固定字段的 `UPDATE`，前端只传了名称没传状态，状态就会被覆盖成 `null`。
改用 `<set>` 动态标签，只拼接真正传入的字段。

### 4. SQL 注入防护

线索列表支持按手机号模糊查询。最初的写法是：

```xml
<!-- 错误示范：${} 是字符串直接拼接 -->
and c.phone like '%${clueQueryDto.phone}%'
```

`${}` 不做预编译，用户输入里带一个 `'` 就能注入。正确写法：

```xml
and c.phone like concat('%', #{clueQueryDto.phone}, '%')
```

`#{}` 走 PreparedStatement 占位符，`concat` 负责拼接通配符，两者缺一不可。

### 5. 一对多映射用 resultMap

线索详情需要同时返回线索本身和它的跟进记录列表。`resultType` 无法映射集合属性，
必须用 `resultMap` + `<collection>`，并注意两个表的主键字段同名（`id`）会互相覆盖，
所以跟进记录的列全部加了别名前缀（`tr_id`、`tr_clue_id` …）。

### 6. 前端 axios 拦截器统一处理

```js
// 请求拦截：统一注入 token，业务代码不必每次手动传
config.headers.token = getToken()

// 响应拦截：统一处理业务失败与 401
if (res.code === 1) return res          // 业务代码只关心 data
if (status === 401) { 清登录态; 跳登录页 }  // 不用每个页面各写一遍
```

好处是新增页面时不需要重复写鉴权逻辑和错误弹窗。

### 7. 敏感配置外置

数据库凭据和 JWT 签名密钥都从环境变量读取，**同时保留默认值**：

```yaml
password: ${DB_PASSWORD:123456}
```

```java
// 环境变量 JWT_SECRET → 系统属性 → 开发默认值
envOrDefault("JWT_SECRET", "qingke-guanjia-jwt-secret-key-...")
```

这样本地开发一条命令就能跑起来，同时代码库里不留下明文凭据 —— 公开仓库尤其要注意这点。

---

## 项目结构

```
qingke-manager/
├── backend/                          # 后端（Maven 多模块）
│   ├── qk-common/                    # 统一响应、分页封装、当前登录用户上下文
│   ├── qk-entity/                    # 实体、DTO、VO
│   ├── qk-management/                # 业务：controller / service / mapper
│   │   ├── config/                   # 分页插件、过滤器注册
│   │   ├── controller/               # 登录、部门、用户、线索、报表
│   │   ├── exception/                # 全局异常处理
│   │   ├── filter/ interceptor/      # 令牌校验的两种实现
│   │   ├── mapper/                   # 4 个 Mapper + 4 个 XML
│   │   └── utils/JwtUtils.java
│   ├── sql/qk.sql                    # 建表 + 初始化数据
│   └── nginx.conf                    # 生产环境反向代理参考
└── frontend/                         # 前端（Vite + Vue 3）
    └── src/
        ├── api/                      # 按模块拆分的接口封装
        ├── layout/MainLayout.vue     # 侧边栏 + 顶栏
        ├── router/index.js           # 路由 + 登录守卫
        ├── utils/auth.js             # 登录态存取
        └── views/                    # 登录 / 首页 / 线索 / 部门 / 用户
```

---

## 环境变量

全部可选，不设置时使用括号内的默认值。

| 变量 | 默认值 | 作用 |
|---|---|---|
| `DB_HOST` | `localhost` | 数据库地址 |
| `DB_PORT` | `3306` | 数据库端口 |
| `DB_NAME` | `qk` | 数据库名 |
| `DB_USERNAME` | `root` | 数据库账号 |
| `DB_PASSWORD` | `123456` | 数据库密码 |
| `JWT_SECRET` | 内置开发密钥 | JWT 签名密钥，**生产环境必须覆盖** |

Windows 示例：

```powershell
$env:DB_PASSWORD = "你的真实密码"
$env:JWT_SECRET = "换成一串足够长的随机字符串"
java -jar qk-management/target/qk-management-1.0-SNAPSHOT.jar
```

---

## 已知限制

如实记录几个尚未解决的问题，避免误解。

### 1. `server.port` 在 Spring Boot 4.0.8 下不生效

本机实测：`application.yml` 里的 `server.port` 会被一个名为 `configurationProperties`
的属性源覆盖成一个自动分配的端口（实测为 60175），导致配置 8080 / 8081 都无效，
且容易与本机其他程序撞端口。

**当前处理**：在 `WebConfig#resolvePort()` 里直接对 `WebServerFactory` 设端口兜底，
取值时跳过该属性源，因此 `application.yml` 里的配置和命令行 `--server.port=xxxx` 依然生效。
换用其他环境版本可能不会出现该现象，届时可移除这段兜底。

### 2. 分页用了两个插件

部门管理走 PageHelper，线索管理走 MyBatis-Plus 分页插件。这是复现过程中的历史遗留，
两者并存虽能正常工作，但风格不统一。后续应统一为 MyBatis-Plus。

### 3. Element Plus 未启用按需引入

当前为全量注册，主包 1.06 MB。`vite.config.js` 注释里提供了按需引入的完整启用方案与实测数据
（主包降至 150 KB，gzip 349 KB → 59 KB）。**未默认开启的原因：开发环境下未完成浏览器渲染实测，
为保证「克隆下来就能跑」不做未验证的改动。** 你自己验证通过后可以直接启用。

### 4. 商机模块只有数据表和转商机入口

商机的跟进、回收、转客户等状态流转尚未实现，目前只有从线索转入这一条路径。

---

## 说明与免责

- 本项目为**课程项目的复现实现**，用于个人学习与技术展示，**不用于任何商业用途**
- 数据均为虚构的测试数据，不含任何真实客户信息
- 账号密码为教学用的弱口令，**请勿用于任何生产环境**；正式部署务必用环境变量覆盖数据库密码与 JWT 密钥
- 界面与业务设计参考了课程资料中提供的原型

---

如果发现问题或有改进建议，欢迎提 Issue。
