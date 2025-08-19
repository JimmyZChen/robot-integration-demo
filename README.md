<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">
  园区机器人调度系统 · 基于 RuoYi-Cloud v3.6.6
</h1>
<h4 align="center">
  面向多厂商（高仙、宇树等）的机器人接入、编排与调度平台
</h4>
<p align="center">
  <a href="https://gitee.com/y_project/RuoYi-Cloud">
    <img src="https://img.shields.io/badge/RuoYi-v3.6.6-brightgreen.svg">
  </a>
  <img src="https://img.shields.io/badge/Java-17-blue">
  <img src="https://img.shields.io/badge/Vue-2.x-4fc08d">
  <img src="https://img.shields.io/badge/SpringCloud-Alibaba-blueviolet">
</p>

## 平台简介

本项目基于 **RuoYi-Cloud v3.6.6** 二次开发，目标是打造一个**多厂商机器人集成调度系统**，统一接入不同厂商的 OpenAPI，提供任务编排、地图/分区管理、状态监控与可观测性。

- 前端：RuoYi-UI（Vue2 + Element-UI）
- 后端：Spring Boot、Spring Cloud Alibaba（Nacos、Gateway、OpenFeign）
- 网关与稳定性：Sentinel（限流/熔断/降级）
- 可观测性：SkyWalking（链路追踪、拓扑、指标）
- 数据：MySQL、Redis

> RuoYi 官方介绍与文档请见其仓库；本 README 仅描述**本项目的扩展与落地**。

### 🧭 导航与主要页面

- 机器人管理：`/gsrobot`（首页所有入口统一跳转此页面）
- 首页已重构：展示「核心能力 / 已接入厂商 / 快捷入口 / 技术栈」

### 🤝 厂商接入进度

- **已接入**  
  - 高仙 Gaussian（GS / S 系列）

- **未接入（规划中）**  
  - 宇树 Unitree（A1 / Go2 …）  
  - 其他：科沃斯、优地、九号、复旦微米等

---

## 环境准备（实测可用）

| 组件 | 推荐/最低 | 实际版本 | 备注/检验 |
|---|---|---|---|
| JDK | 1.8+（推荐 17） | **openjdk 17.0.16 Temurin** | `java -version` |
| MySQL | 5.7+（推荐 8.x） | **8.0.42** | `mysql -V` |
| Redis | 3.0+ | **3.0.504（Win）** | `redis-server --version` |
| Maven | 3.0+ | **3.9.10** | `mvn -v` |
| Node.js | 12+ | **v16.20.2** | `node -v` |
| **Nacos** | **2.2.3** | **2.2.3** | Web 端口 `8848` |
| **Sentinel Dashboard** | 1.8.x | **1.8.8** | 运行 `sentinel-dashboard-1.8.8.jar` |
| **SkyWalking OAP** | 9.x | **9.7.0** | `apache-skywalking-apm-9.7.0` |
| **SkyWalking Java Agent** | 9.x | **9.4.0** | `apache-skywalking-java-agent-9.4.0` |

> 注：Nacos 2.2.3 为当前重点依赖；如需修改注册中心/配置中心地址，请同步调整网关与各微服务的 `bootstrap.yml`/`application.yml`。

---

## 系统模块（保持与 RuoYi 官方一致）


~~~
com.ruoyi     
├── ruoyi-ui              // 前端框架 [80]
├── ruoyi-gateway         // 网关模块 [8080]
├── ruoyi-auth            // 认证中心 [9200]
├── ruoyi-api             // 接口模块
│       └── ruoyi-api-system                          // 系统接口
│       └── ruoyi-api-robot                           // 机器人接口
├── ruoyi-common          // 通用模块
│       └── ruoyi-common-core                         // 核心模块
│       └── ruoyi-common-datascope                    // 权限范围
│       └── ruoyi-common-datasource                   // 多数据源
│       └── ruoyi-common-log                          // 日志记录
│       └── ruoyi-common-redis                        // 缓存服务
│       └── ruoyi-common-seata                        // 分布式事务
│       └── ruoyi-common-security                     // 安全模块
│       └── ruoyi-common-sensitive                    // 数据脱敏
│       └── ruoyi-common-swagger                      // 系统接口
├── ruoyi-modules         // 业务模块
│       └── ruoyi-file                                // 机器人管理
│       └── ruoyi-system                              // 系统模块 [9201]
│       └── ruoyi-gen                                 // 代码生成 [9202]
│       └── ruoyi-job                                 // 定时任务 [9203]
│       └── ruoyi-file                                // 文件服务 [9300]
├── ruoyi-visual          // 图形化管理模块
│       └── ruoyi-visual-monitor                      // 监控中心 [9100]
├──pom.xml                // 公共依赖
~~~


> 本项目的“机器人管理”目前以 `/gsrobot` 为前端入口；后端通过网关统一接入第三方 OpenAPI，并逐步抽象为厂商适配层。

---

## 内置功能（保持官方列表）

1. 用户管理：用户是系统操作者，该功能主要完成系统用户配置。  
2. 部门管理：配置系统组织机构（公司、部门、小组），树结构展现支持数据权限。  
3. 岗位管理：配置系统用户所属担任职务。  
4. 菜单管理：配置系统菜单，操作权限，按钮权限标识等。  
5. 角色管理：角色菜单权限分配、设置角色按机构进行数据范围权限划分。  
6. 字典管理：对系统中经常使用的一些较为固定的数据进行维护。  
7. 参数管理：对系统动态配置常用参数。  
8. 通知公告：系统通知公告信息发布维护。  
9. 操作日志：系统正常操作日志记录和查询；系统异常信息日志记录和查询。  
10. 登录日志：系统登录日志记录查询包含登录异常。  
11. 在线用户：当前系统中活跃用户状态监控。  
12. 定时任务：在线（添加、修改、删除)任务调度包含执行结果日志。  
13. 代码生成：前后端代码的生成（java、html、xml、sql）支持CRUD下载。  
14. 系统接口：根据业务代码自动生成相关的api接口文档。  
15. 服务监控：监视当前系统CPU、内存、磁盘、堆栈等相关信息。  
16. 在线构建器：拖动表单元素生成相应的HTML代码。  
17. 连接池监视：监视当前系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈。  

### 📦 本项目新增（机器人场景）

- **多厂商适配层**：封装高仙 OpenAPI，抽象设备模型与指令，隐藏协议差异。  
- **机器人管理（/gsrobot）**：机器人列表、在线状态、地图列表、位姿、任务状态面板。  
- **任务编排**：临时任务/循环任务、按地图/分区编排、串/并行、重试与限流。  
- **地图与分区**：地图/分区选择与任务绑定，坐标展示与转换（前端可视化）。  
- **网关与安全**：统一鉴权、签名校验、黑白名单、速率限制与 IP 保护。  
- **可观测性**：接入 SkyWalking，链路 ↔ 日志 TraceId 关联；Sentinel 规则治理。  

---

## 快速开始

1. **准备依赖**
   - 启动 MySQL / Redis；
   - 启动 **Nacos 2.2.3**（`8848`），导入/确认配置；
   - 运行 **Sentinel Dashboard 1.8.8**；
   - 启动 **SkyWalking OAP 9.7.0**，并在各服务 JVM 启动参数加上 `-javaagent:/path/to/skywalking-agent/skywalking-agent.jar`（Agent 9.4.0），配置 OAP 地址。

2. **启动后端**
   - 依次启动：`ruoyi-gateway` → `ruoyi-auth` → `ruoyi-system`（以及需要的 `ruoyi-job`、`ruoyi-file` 等）。

3. **启动前端**
   ```bash
   cd ruoyi-ui
   npm install
   npm run dev
