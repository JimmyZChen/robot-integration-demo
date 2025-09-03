<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">
  Smart Park Robot Platform · Code Structure Demo (RuoYi-Cloud v3.6.6)
</h1>
<h4 align="center">
  A vendor-agnostic robot integration, orchestration & scheduling platform (read-only sample, non-runnable by default)
</h4>
<p align="center">
  <a href="https://gitee.com/y_project/RuoYi-Cloud">
    <img src="https://img.shields.io/badge/RuoYi-v3.6.6-brightgreen.svg">
  </a>
  <img src="https://img.shields.io/badge/Java-17-blue">
  <img src="https://img.shields.io/badge/Vue-2.x-4fc08d">
  <img src="https://img.shields.io/badge/SpringCloud-Alibaba-blueviolet">
  <img src="https://img.shields.io/badge/Status-Read--only-lightgrey">
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-Apache--2.0-informational"></a>
</p>

> **What**: A **code structure & design showcase** for a multi-vendor robot management platform.  
> **Why**: Publicly runnable builds may trigger real devices or leak sensitive data, so this repo is **non-runnable by default**.  
> **Who**: Backend / platform / architecture readers (gateway governance, resilience, and observability).

## 📌 Important Notes
- This repository is **non-runnable by default** and focuses on **structure & design**.  
- It **does not include Nacos configuration**, any secrets/credentials, or usable external endpoints; all third-party parameters are removed or replaced by placeholders.  
- The project integrates real robots/production APIs in private environments. Publicly exposing runnable configs could **trigger real devices** or cause data leaks.  
- UI and flows are demonstrated via **Slides / Baidu Netdisk** (see below). They do **not** require this repository to connect to external services.

## 🎥 Demo
- **Google Slides (flows & UI mock)**:  
  https://docs.google.com/presentation/d/1I7oIYdUIYdgaCM-MY_42yEG9jm_DSXGXnCeFv1YzYWM/
- **Baidu Netdisk (redacted screenshots)**:  
  https://pan.baidu.com/s/11KPn1tRsMa1jslKZIbxPTA?pwd=xgbp

> Slides/Netdisk include robot list, status monitoring, map/partition views, and temporary task dispatch flows (all screenshots redacted/anonymized).

## 🧾 Overview
Built on **RuoYi-Cloud v3.6.6**, this sample illustrates a **multi-vendor robot orchestration platform**: unified OpenAPI integration (e.g., **Gaussian Robotics**), task orchestration, map/partition management, status monitoring, and observability.  
For safety & compliance, the repository focuses on **service decomposition, gateway/service layering, rate limit & circuit ideas, and observability touchpoints**, without shipping runnable configuration.

### Capabilities (focus of the sample)
- **Vendor adapter layer**: wraps third-party OpenAPIs (e.g., Gaussian), abstracts device & command models, hides protocol differences.  
- **Gateway governance**: unified routing; examples of rate-limit / circuit-break / degrade rules (Sentinel), allow/deny lists, and auth hooks.  
- **Robot management (`/gsrobot`)**: controller/service skeletons for list/online status, maps/partitions, and temporary task orchestration.  
- **Observability touchpoints**: trace propagation, log–trace correlation (example instrumentation & notes).

---
## 📂 Project Layout (sample)
~~~
com.ruoyi
├── ruoyi-ui              // Frontend framework [80]
├── ruoyi-gateway         // API Gateway module [8080]
├── ruoyi-auth            // Authentication center [9200]
├── ruoyi-api             // API modules
│       └── ruoyi-api-system                          // System APIs
│       └── ruoyi-api-robot                           // Robot APIs
├── ruoyi-common          // Common modules
│       └── ruoyi-common-core                         // Core module
│       └── ruoyi-common-datascope                    // Data scope
│       └── ruoyi-common-datasource                   // Multi-datasource
│       └── ruoyi-common-log                          // Logging
│       └── ruoyi-common-redis                        // Cache service
│       └── ruoyi-common-seata                        // Distributed transactions
│       └── ruoyi-common-security                     // Security module
│       └── ruoyi-common-sensitive                    // Data masking
│       └── ruoyi-common-swagger                      // Swagger/OpenAPI support
├── ruoyi-modules         // Business modules
│       └── ruoyi-robot-gs                            // Robot management
│       └── ruoyi-system                              // System service [9201]
│       └── ruoyi-gen                                 // Code generator [9202]
│       └── ruoyi-job                                 // Scheduled jobs [9203]
│       └── ruoyi-file                                // File service [9300]
├── ruoyi-visual          // Visual management modules
│       └── ruoyi-visual-monitor                      // Monitoring center [9100]
├── pom.xml               // Parent POM / common dependencies
~~~

---

## 🚫 What’s **not** included
- **Nacos configuration** or any exported registration/config bundles.  
- **Secrets/credentials** (e.g., `clientId/clientSecret/openAccessKey`, JWT secrets, DB/Redis accounts).  
- **Usable external endpoints** (real `baseUrl`, internal IPs/domains, device serials, map IDs, company/geo data).  
- **Implementations that could trigger real actions**: task dispatch and similar calls keep structure only; real calls are disabled.

<a id="why-non-runnable"></a>
## ℹ️ Why is it non-runnable?
- To prevent accidental calls to real robots or production APIs.  
- Public repos can’t safely host secrets/internal endpoints, so **all required runtime configuration is removed**.  
- Outbound call sites use **placeholder paths** or read from config; without private environment variables, methods fail fast or return placeholder responses.

## 🔐 Security & Compliance
- All credentials and real addresses were removed. If you spot any leftover, please open an issue or PR.  
- Do **not** commit `.env`, `application-*.yml`, `bootstrap*.yml`, or `nacos-export*`.  
- “Gaussian Robotics / 高仙” and other vendor names are third-party trademarks. This repo is a technical demo and does not include their private docs/SDK/keys.

## 🛠 Tech Stack (structure demo)
- Backend: Spring Boot · Spring Cloud Alibaba (Gateway, OpenFeign, etc.)  
- Resilience: Sentinel examples for rate limit / circuit break / degrade  
- Observability: SkyWalking integration points & sample code  
- Data: DTO/interface-driven examples; **no** runnable MySQL/Redis connections provided by default

## 🗂 Reading Guide
- Entry page: **`/gsrobot`** (frontend route sample)  
- Vendor adapters: `ruoyi-robot-gs` → `openapi/` & `service/` packages (interfaces & skeletons)  
- Gateway rules: example routes/filters in `ruoyi-gateway`  
- Rate-limit / degrade: Sentinel annotations & sample rules (redacted)

## 🧪 Private sandbox try (for you only)
> This repo does **not** include run steps. For sandbox testing, you would need to:  
> 1) Provide your own Nacos/config or environment variables;  
> 2) Use your **own** test keys and **non-production** devices;  
> 3) Run everything inside an isolated network with rate-limit/circuit-break & fallback configured.  
**Never commit any keys or usable configs back to this repo.**

## ❓ FAQ
**Q: Why is the repo non-runnable?**  
**A:** To avoid triggering real devices/production APIs; all runtime configs were removed.

**Q: Can I try it inside a private network?**  
**A:** Yes, but you must provide your own Nacos/test keys/devices and configure rate-limit & circuit-break inside an isolated sandbox.

**Q: Do you accept PRs?**  
**A:** This repo is primarily a showcase; feature PRs are not accepted for now. Documentation/security fix PRs are welcome.

## 👤 Author / Maintainer
Chen Zheng
- Linkedin：https://www.linkedin.com/in/jimmy-chen-74a8182b8/
- Github: https://github.com/JimmyZChen
- Gitee: https://gitee.com/chen-zheng-jimmy

## 📄 License & Disclaimer
- Unless otherwise stated, sample code can be licensed under a common OSS license (**Apache-2.0 / MIT** recommended). Please add a matching `LICENSE` file at the repository root.  
- This repository does not control real devices. You are responsible for legal and safe use in your own environment.

---

<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">
  园区机器人调度系统 · 代码结构演示（RuoYi-Cloud v3.6.6）
</h1>
<h4 align="center">
  面向多厂商（高仙 / Gaussian Robotics、宇树等）的机器人接入、编排与调度平台（只读示例仓库，默认不可运行）
</h4>
<p align="center">
  <a href="https://gitee.com/y_project/RuoYi-Cloud">
    <img src="https://img.shields.io/badge/RuoYi-v3.6.6-brightgreen.svg">
  </a>
  <img src="https://img.shields.io/badge/Java-17-blue">
  <img src="https://img.shields.io/badge/Vue-2.x-4fc08d">
  <img src="https://img.shields.io/badge/SpringCloud-Alibaba-blueviolet">
  <img src="https://img.shields.io/badge/Status-Read--only-lightgrey">
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-Apache--2.0-informational"></a>
</p>

> **What**：多厂商机器人接入、编排与调度的平台**代码结构与设计示例**。  
> **Why**：避免误触发真实设备与泄露敏感信息，本仓库**默认不可运行**。  
> **Who**：面向后端/架构/平台治理读者（网关治理、稳定性与可观测性）。

## 目录
- [📌 重要声明（务必先读）](#-重要声明务必先读)
- [🎥 效果展示](#-效果展示)
- [🧾 项目简介](#-项目简介)
- [🧩 模块速览](#-模块速览)
- [📂 目录结构（示例）](#-目录结构示例)
- [🧪 关键示例](#-关键示例)
- [🚫 本仓库不包含的内容](#-本仓库不包含的内容)
- [ℹ️ 为什么默认不可运行](#ℹ️-为什么默认不可运行)
- [🔐 安全与合规](#-安全与合规)
- [🛠 技术栈（结构演示）](#-技术栈结构演示)
- [🗂 代码可读指南](#-代码可读指南)
- [🧪 私有环境试跑提示](#-私有环境试跑提示)
- [❓ FAQ](#-faq)
- [📄 License & 免责声明](#-license--免责声明)

## 📌 重要声明（务必先读）
- 本仓库为**代码结构与设计演示**，**默认不可运行**。  
- **未包含 Nacos 配置**、任何密钥/凭据与可用的外部地址；所有第三方参数均已移除或以占位符存在。  
- 原因：项目对接真实机器人/生产环境，公开可运行版本可能**误触发真实设备**或泄露隐私，因此默认禁用实际调用。  
- 运行效果已在 **百度网盘 / Slides** 展示（见下文），用于说明功能与界面，不依赖本仓库直接连外网。

## 🎥 效果展示
- **百度网盘（脱敏截图打包）**：  
  [https://pan.baidu.com/s/11KPn1tRsMa1jslKZIbxPTA?pwd=xgbp](https://pan.baidu.com/s/11KPn1tRsMa1jslKZIbxPTA?pwd=xgbp)
- **Google Slides（示意流程与界面）**：  
  [https://docs.google.com/presentation/d/1I7oIYdUIYdgaCM-MY_42yEG9jm_DSXGXnCeFv1YzYWM/](https://docs.google.com/presentation/d/1I7oIYdUIYdgaCM-MY_42yEG9jm_DSXGXnCeFv1YzYWM/)

> 网盘/Slides展示了机器人列表、状态监控、地图/分区与任务下发等界面与流程（截图均已脱敏/打码）。

## 🧾 项目简介
本项目基于 **RuoYi-Cloud v3.6.6** 二次开发，目标是打造一个**多厂商机器人集成调度系统**：统一接入不同厂商 OpenAPI（以**高仙（Gaussian Robotics）**为例），提供任务编排、地图/分区管理、状态监控与可观测性。  
为确保安全与合规，当前仓库以**示例代码结构**为主，聚焦工程拆分、网关与业务层设计、限流/降级与可观测性接入方式。

### 主要能力（示例代码侧重）
- **厂商适配层**：封装第三方 OpenAPI（以高仙为例），抽象设备模型与指令，隐藏协议差异。  
- **网关治理**：统一路由、限流/熔断/降级（Sentinel 规则示例）、黑白名单与基础鉴权位置。  
- **机器人管理（/gsrobot）**：列表/在线状态、地图与分区、临时任务编排的接口骨架。  
- **可观测性接入点**：链路透传、日志关联 TraceId（示例埋点与说明）。

## 🧩 模块速览
| 模块 | 作用 | 关键目录/类 | 推荐阅读顺序 |
|---|---|---|---|
| ruoyi-gateway | 网关路由与治理（限流/熔断/鉴权钩子） | `filters/`, `routes/` | 1 |
| ruoyi-robot-gs | 厂商适配与业务接口骨架 | `openapi/`, `service/` | 2 |
| ruoyi-common-* | 日志、Redis、数据源、Swagger 等 | `ruoyi-common-swagger`（Swagger/OpenAPI 支持） | 3 |
| ruoyi-visual-monitor | 监控台示例 | 暂未完成 | 4 |

## 📂 目录结构（示例）

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
│       └── ruoyi-robot-gs                            // 机器人管理
│       └── ruoyi-system                              // 系统模块 [9201]
│       └── ruoyi-gen                                 // 代码生成 [9202]
│       └── ruoyi-job                                 // 定时任务 [9203]
│       └── ruoyi-file                                // 文件服务 [9300]
├── ruoyi-visual          // 图形化管理模块
│       └── ruoyi-visual-monitor                      // 监控中心 [9100]
├──pom.xml                // 公共依赖
~~~
---

## 🚫 本仓库**不包含**的内容
- **Nacos 配置**与任何可用的注册/配置中心导出文件。  
- **密钥/凭据**：如第三方 `clientId/clientSecret/openAccessKey`、JWT Secret、数据库/Redis 账号等。  
- **可运行的外部地址**：真实 `baseUrl`、内网 IP/域名、设备序列号、地图 ID、公司/地理信息等。  
- **可直接触发外部动作的实现**：任务下发等方法仅保留结构，默认禁用实际调用。

## ℹ️ 为什么默认不可运行？
- 为避免误调用真实机器人或第三方生产 API。  
- 公开仓库无法安全托管密钥/内网地址，因此**移除了全部运行所需的敏感配置**。  
- 代码中涉及外呼的位置均已改为**占位路径**或通过配置读取；若未注入私有环境变量，方法将直接失败或返回占位响应。

## 🔐 安全与合规
- 已移除所有凭据与真实地址；若发现遗漏，请提 Issue 或直接 PR。  
- 请勿在公开仓库提交 `.env`、`application-*.yml`、`bootstrap*.yml`、`nacos-export*` 等文件。  
- “Gaussian Robotics / 高仙”等为第三方商标；本仓库仅为技术演示，不包含其私有文档/SDK/密钥。

## 🛠 技术栈（结构演示）
- 后端：Spring Boot · Spring Cloud Alibaba（Gateway、OpenFeign 等）
- 稳定性：Sentinel（限流/熔断/降级）规则**示例**
- 可观测性：SkyWalking 接入点说明与示例代码
- 数据：示例使用接口/DTO 结构；默认不提供可用的 MySQL/Redis 连接

## 🗂 代码可读指南
- 入口页面：`/gsrobot`（前端路由示例）
- 厂商适配：`ruoyi-robot` 模块的 `openapi/` 与 `service/` 包（接口与实现骨架）
- 网关规则：`ruoyi-gateway` 的路由与过滤器示例
- 限流/降级：Sentinel 注解与示例规则（已脱敏）

## 🧪 想在**私有环境**试跑？（仅供你自己）
> 本仓库不提供运行步骤。若需在你自己的隔离环境内测试，请自行：  
> 1) 配置私有 `Nacos` / 环境变量注入第三方参数；  
> 2) 使用你自己的测试密钥与测试设备（非生产）；  
> 3) 确保网关与下游服务均在内网沙箱中运行，并做好限流/降级与回退策略。  
**请勿将任何密钥或可用配置提交回此仓库。**

## ❓ FAQ

**Q：为什么默认不可运行？**  
**A：** 为避免误触发真实设备/生产 API，已移除运行所需配置。

**Q：能否在本地私有网试跑？**  
**A：** 可以，但需自备 Nacos/测试密钥/测试设备，并在内网沙箱中配置限流与熔断。

**Q：是否接受 PR？**  
**A：** 当前仓库以演示为主，暂不接受功能性 PR；欢迎修正文档与安全问题的 PR。

## 👤 作者
陈峥
- 领英：https://www.linkedin.com/in/jimmy-chen-74a8182b8/
- Gitee: https://gitee.com/chen-zheng-jimmy
- Github: https://github.com/JimmyZChen

## 📄 License & 免责声明
- 若未特别声明，示例代码以常见开源协议发布（建议 Apache-2.0 / MIT）。  
- 本仓库不对接真实设备，不为任何外部调用行为负责；使用者需自行保证合规与安全。


