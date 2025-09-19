# 问题—原因—修复（Issues · Root Cause · Fixes）

> 下面三大类与 PPT 一一对应：  
> 1) **Gateway / Nacos / Sentinel**  
> 2) **可观测性 & 基础设施（Observability & Infrastructure）**  
> 3) **代码与客户端（Code & Client）**

---

## 1) Gateway / Nacos / Sentinel

- **Nacos 启动卡在 STARTING**  
  **原因**：持久化 **MySQL 配置/版本不匹配**，未初始化 SQL。  
  **修复**：补齐 `spring.datasource.*` 配置，导入初始化脚本；建议使用 **Nacos 2.2.3**（或与镜像一致版本）。

- **Sentinel API 分组空白/规则不生效**  
  **原因**：缺少 `sentinel-datasource-nacos` 依赖，或 **DataId/Group 与规则 JSON** 不一致。  
  **修复**：统一网关规则：`ruoyi-gateway-gw-api-defs/gw-flow/gw-degrade`，对齐 DataId/Group 与 JSON 结构。

- **网关 404 / Swagger “Try it out” 失败**  
  **原因**：`Path/StripPrefix` 与 **服务前缀/`context-path`** 不一致。  
  **修复**：统一路径策略；必要时**移除** `context-path` 或调整 Strip 规则。

- **限流路径不一致，规则无法命中**  
  **原因**：`@SentinelResource` **分散在 Controller**，资源名混乱。  
  **修复**：下沉统一到 **`GsOpenApiServiceImpl`**，规范资源命名，并与 Nacos 规则一一对应。

- **看不到熔断/降级效果**  
  **原因**：入口网关阈值过小，压测请求先被 **429** 挡住；或“**慢调用比例**”阈值设置不当。  
  **修复**：压测时**上调网关阈值**；基于业务延迟/错误率设置“慢调用/异常比例”规则，确保规则实际触发。

---

## 2) 可观测性 & 基础设施（Observability & Infrastructure）

- **SkyWalking 无 Gateway 链路/日志无 `traceId`**  
  **原因**：未加载 **SCG/WebFlux 插件**；`-javaagent` 未注入；Logback 无 MDC。  
  **修复**：  
  - 将 `apm-spring-cloud-gateway-3.x-plugin` 放入 **plugins**；  
  - 启动参数添加 **`-javaagent:/path/to/agent.jar`**；  
  - Logback Pattern 增加 **`%X{traceId}`**。

- **Docker：MySQL 端口冲突/首次无数据**  
  **原因**：`3306` 被占或未导入初始化 SQL。  
  **修复**：变更端口映射或停用宿主 MySQL；在 compose 中**预导入 SQL**；增加健康检查。

- **容器内访问失败（误用 `本地ip地址`）**  
  **原因**：容器将宿主地址当本地。  
  **修复**：容器间调用使用 **服务名:端口**；访问宿主在 **Win/Mac** 使用 `host.docker.internal`。

- **依赖/插件冲突（Reactor Netty 等）**  
  **原因**：Spring Boot/Cloud 与依赖版本矩阵不一致，网关/WebFlux 插件缺失。  
  **修复**：**统一版本并锁定**；按需引入 gateway/webflux 相关插件。

---

## 3) 代码与客户端（Code & Client）

- **MyBatis `Invalid bound statement`**  
  **原因**：`mapper-locations` 路径或 **namespace 与接口 FQN 不一致**；XML 未打包。  
  **修复**：校对 `@MapperScan + namespace`；确保 XML 被正确打包并能被扫描到。

- **`RestTemplate` 堆积/重试放大故障**  
  **原因**：**未设置超时/连接池**，启用自动重试。  
  **修复**：设置 `connect/read` 超时与连接池大小；**禁用自动重试**；关键写操作做幂等（如幂等键）。

- **CORS 预检被拦**  
  **原因**：网关未放行 **OPTIONS** 或响应头不完整。  
  **修复**：配置**全局 CORS**：允许所需 `Origin/Headers/Methods`，暴露必要响应头；显式放行 **OPTIONS**。

---

> 相关示例/配置可在「可观测性（SkyWalking）」「稳定性（Sentinel）」「容器化（Docker）」章节对照落地。
