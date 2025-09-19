# 项目概览（Executive Summary）

> 统一接入多厂商 OpenAPI，前置治理（鉴权/限流）、异步化抗洪、可观测闭环与一键交付。

## 我们在做什么
- 统一管理：机器人状态 / 地图分区 / 临时任务  
- 治理前置：Gateway 鉴权/审计，限流前置  
- 异步化：RabbitMQ（Topic→Queue→DLQ）、手动 ack、幂等  
- 可观测：SkyWalking（拓扑、Trace、指标）  
- 交付：Docker Compose 一键部署

---

## 快速入口

<div class="grid cards" markdown>

-   :material-map-search: **概览 Overview**  
    一页看清 Why、架构、演进与稳定性治理。  
    [:material-arrow-right: 打开](overview/overview.md)

-   :material-play-box-outline: **运行效果 Live Demo**  
    PPT 第 10–18 页截图与备注。  
    [:material-arrow-right: 打开](live-demo.md)

-   :material-rabbit: **异步化（RabbitMQ）**  
    架构、参数、消费与错误处理、联调结果。  
    [:material-arrow-right: 打开](mq-async.md)

-   :material-eye: **可观测性（SkyWalking）**  
    服务/端点总览、拓扑、Trace、看板。  
    [:material-arrow-right: 打开](observability.md)

-   :material-shield-check: **稳定性（Sentinel）**  
    流控/降级规则、触发日志与代码示例。  
    [:material-arrow-right: 打开](stability-sentinel.md)

-   :material-cog-transfer: **配置中心（Nacos）**  
    多环境配置与规则托管（集中下发/热更新）。  
    [:material-arrow-right: 打开](config-nacos.md)

-   :material-docker: **容器化部署（Docker）**  
    运行态总览与 Compose 目录/配置片段。  
    [:material-arrow-right: 打开](docker-deploy.md)

-   :material-speedometer: **压测与证据（JMeter）**  
    计划、结果、MQ 侧验证与服务指标。  
    [:material-arrow-right: 打开](loadtest-jmeter.md)

-   :material-wrench: **问题—原因—修复（RCA）**  
    Gateway/Nacos/Sentinel、可观测与代码侧常见问题。  
    [:material-arrow-right: 打开](issues-rca.md)

</div>
