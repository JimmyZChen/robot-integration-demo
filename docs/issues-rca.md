# 问题—原因—修复 Issues 

> 环境说明：RuoYi-Cloud（ruoyi-modules-robot / ruoyi-api-robot），Gateway 统一入口 `/external/gs/**`；  
> Nacos 注册与配置，Sentinel 动态规则，SkyWalking 全链路，可选 RabbitMQ 异步；Docker Compose 一键交付。

---

## 0.上线口径与压测基线

- **口径与基线不清**  
  **表现**：不同同学对“成功/失败”“是否算 429”口径不一致，导致指标解读偏差。  
  **修复**：统一口径与基线：  
  - **Dry-Run/关消费**压测基线：100 QPS × 3 分钟，视 200/429 为成功（验证网关限流/稳定性）。  
  - **观测指标**：P95/P99、Throughput、Error%、队列累积、SkyWalking Success Rate。  
  - **结果样例**：18035 样本 **P95=6ms**、**P99≈10ms**、**Error=0**、Throughput≈**100.2/s**（多轮结果一致）。

- **回滚与冷启动不可量化**  
  **修复**：落库到《变更记录》：  
  - **冷启动**：Docker Compose + 首启 SQL → **≤ 12 分钟** 新环境就绪；  
  - **回滚**：微服务化后 **≤ 5 分钟**（回滚镜像/配置） 。

---

## 1. Gateway / Nacos / Sentinel

- **Nacos 启动卡在 STARTING**  
  **原因**：持久化 **MySQL 版本/字符集不匹配** 或未导入初始化 SQL。  
  **修复**：补齐 `spring.datasource.*`，导入官方/项目初始化脚本；版本与镜像对齐（建议 **2.2.x**）。

- **网关 404 / Swagger “Try it out” 失败**  
  **原因**：`StripPrefix/Path` 与 **下游 `context-path`** 不一致；服务前缀改动未更新路由。  
  **修复**：统一路径策略；必要时**移除** `context-path` 或同步调整 Strip 规则；灰度后统一回收旧前缀。

- **限流路径不一致，规则无法命中**  
  **原因**：`@SentinelResource` **分散在 Controller**，资源名不统一。  
  **修复**：统一下沉到 **`GsOpenApiServiceImpl`**；规范资源命名，并与 Nacos 规则一一对应  
  （如：`ruoyi-gateway-gw-api-defs/gw-flow/gw-degrade`）。

- **Sentinel 规则不生效/分组空白**  
  **原因**：缺 `sentinel-datasource-nacos` 依赖，或 **DataId/Group 与 JSON** 不一致。  
  **修复**：补依赖；校验三元组 `{AppName, DataId, Group}` 与 JSON 结构；压测前提高网关阈值避免被 429 抢先挡掉。

- **CORS 预检被拦**  
  **原因**：未放行 **OPTIONS** 或响应头缺失。  
  **修复**：配置全局 CORS：允许所需 `Origin/Headers/Methods`，暴露必要响应头；显式放行 **OPTIONS**。

---

## 2. 可观测性与基础设施

- **无 Gateway 链路 / 日志无 `traceId`**  
  **原因**：未加载 **SCG/WebFlux 插件**；`-javaagent` 未注入；Logback 无 MDC。  
  **修复**：  
  - 将 `apm-spring-cloud-gateway-3.x-plugin` 放入 **plugins**；  
  - 启动加入 `-javaagent:/path/to/agent/agent.jar`；  
  - Logback Pattern 增加 `%X{traceId}`；压测期适当提高采样率，生产按 QPS 降噪。

- **Docker：MySQL 端口冲突/首次无数据**  
  **原因**：宿主占用 `3306` 或未导入 SQL。  
  **修复**：调整端口映射或停宿主 MySQL；**compose 预导入 SQL**；增加 `healthcheck` 并让上游依赖 `depends_on.condition=service_healthy`。

- **容器内访问失败（误用宿主 `localhost`）**  
  **原因**：容器将宿主地址当本地。  
  **修复**：容器间互调用 **服务名:端口**；访问宿主在 Win/Mac 使用 `host.docker.internal`。

- **依赖/插件冲突（Reactor Netty 等）**  
  **原因**：Spring Boot/Cloud 与依赖矩阵不一致。  
  **修复**：锁定版本矩阵；仅按需引入 gateway/webflux/skywalking 插件并校验兼容性。

---

## 3. 异步化与幂等

- **压测时队列无限累积/消息重复消费**  
  **原因**：演示环境 **关消费（Dry-Run）** 或缺少 **prefetch/手动 ack**；失败重试无上限。  
  **修复**：  
  - 压测口径：关消费只校验入口与可观测，允许累积；  
  - 正式：开启消费者，设 `prefetch= n`、**手动 ack**，失败进入 **DLQ**；  
  - 写操作引入**幂等键**，超时/重试**禁止放大故障**。

- **RestTemplate 堆积/重试放大**  
  **原因**：未设置超时与连接池，自动重试开启。  
  **修复**：设置 `connect/read` 超时、连接池大小；禁用自动重试；关键写操作幂等化。

---

## 4. 代码与前端（RuoYi + Vue）

- **MyBatis `Invalid bound statement`**  
  **原因**：`mapper-locations` 或 **XML `namespace` ≠ 接口 FQN**；XML 未打包。  
  **修复**：校对 `@MapperScan + namespace`；确保 XML 被扫描与打包。

- **前端跨域/端口不一致**  
  **原因**：环境切换时 proxy 未同步。  
  **修复**：`vue.config.js` 统一代理，端口/目标随环境变量切换。

- **UI 小问题（来自阶段总结）**  
  **表现**：分区/地图名换行、状态未自动刷新、接口结果结构不一致等。  
  **修复**：  
  - 表格列宽 + `tooltip` 优化；  
  - 增加“手动刷新”（后续再上定时刷新）；  
  - API 返回做“字符串/对象”**健壮解析**，避免任务下发提示错误；  
  - `areaId` 与坐标映射缺数据 → 与厂商对齐数据表，纳入后续里程碑。

---

## 5. 规约与治理（统一命名 & 回溯）

- **资源命名/日志字段不统一，排障困难**  
  **修复**：  
  - 统一资源命名（与 Sentinel 规则一致），统一异常体；  
  - 日志注入 `traceId`、`requestId`、`taskId`、`resource`；  
  - 复盘流程：**先交付后演进**，变更与回滚留痕，按“问题→原因→修复”固化到文档。

---

### 附：可复用清单（上线前自检）
- [ ] `gateway` 路由/前缀 与下游路径一致；  
- [ ] Nacos 规则（DataId/Group）与应用资源名一致；  
- [ ] SkyWalking `-javaagent` 与 Gateway/WebFlux 插件到位；  
- [ ] Docker `healthcheck` 完整，首次启动 SQL 自动导入；  
- [ ] 关消费压测口径/观测指标一致，正式环境开启消费者并配好 DLQ；  
- [ ] CORS、超时、连接池、幂等与重试策略到位；  
- [ ] 日志包含 trace 字段，可实现 **trace ↔ log** 互查。

> 指标来源：阶段总结与演示压测记录（基线 100 QPS，P95≈6ms，P99≈9–10ms，Error=0）。
