---
title: 首页 Dashboard
hide:
  - toc
---

# 机器人调度系统文档  
面向多厂商机器人的统一网关，具备异步编排与可观测性。

> **一句话 / One-liner**  
> 统一接入多厂商 OpenAPI，前置治理（鉴权/审计/限流），异步抗洪峰（MQ）、可观测（Trace/指标），与一键交付（Docker Compose）。

[立即上手 ⟶](overview.md){ .md-button .md-button--primary }
[架构概览 ⟶](overview.md#sec-architecture){ .md-button }
[Live Demo 指南 ⟶](live-demo.md){ .md-button }

---

## 解决什么问题 
- :material-source-branch: **多厂商聚合**：异构 OpenAPI 统一入口与协议约束  
- :material-shield-check: **前置治理**：网关鉴权、审计、限流、灰度  
- :material-rabbit: **异步解耦**：RabbitMQ（Topic→Queue→DLQ）、手动 ack、幂等校验  
- :material-eye: **可观测性**：SkyWalking 端到端 Trace / 指标 / 拓扑  
- :material-rocket-launch: **一键交付**：Docker Compose 多环境参数化

> 详细的 **What / Why / 演进路径** 请移步：  
> [概览（Overview）](overview.md#sec-what-why){ .md-button }
---

<h2>快速入口</h2>

<div class="quicklinks">

  <a class="ql" href="overview/">
    <strong>概览 Overview</strong>
    <em>Why、架构、演进与稳定性治理</em>
  </a>

  <a class="ql" href="live-demo/">
    <strong>运行效果 Live Demo</strong>
    <em>PPT 10–18 页截图与备注</em>
  </a>

  <a class="ql" href="mq-async/">
    <strong>异步化（RabbitMQ）</strong>
    <em>架构、参数、消费与错误处理、联调结果</em>
  </a>

  <a class="ql" href="observability/">
    <strong>可观测性（SkyWalking）</strong>
    <em>服务/端点总览、拓扑、Trace、看板</em>
  </a>

  <a class="ql" href="stability-sentinel/">
    <strong>稳定性（Sentinel）</strong>
    <em>流控/降级规则、触发日志与代码示例</em>
  </a>

  <a class="ql" href="config-nacos/">
    <strong>配置中心（Nacos）</strong>
    <em>多环境配置与规则托管（集中下发/热更新）</em>
  </a>

  <a class="ql" href="docker-deploy/">
    <strong>容器化部署</strong>
    <em>运行态总览与 Compose 目录/配置片段</em>
  </a>

  <a class="ql" href="loadtest-jmeter/">
    <strong>压测与证据（JMeter）</strong>
    <em>计划、结果、MQ 侧验证与服务指标</em>
  </a>

  <a class="ql" href="issues-rca/">
    <strong>问题—原因—修复（RCA）</strong>
    <em>Gateway/Nacos/Sentinel、观测与代码侧常见问题</em>
  </a>

</div>

---

!!! note "数据与接口说明 · Data & API Notice"
    - 演示数据为**脱敏/模拟**，**不会触达真实设备**。  
    - 网关支持 `X-Dry-Run: true` 安全开关；敏感 Token/地图名已做遮罩。  
    - 仓库为**结构与实践演示**，默认不可直连生产。

---

### 仓库与版本
[GitHub](https://github.com/JimmyZChen/robot-integration-demo){ target=_blank } ·
[Gitee](https://gitee.com/Jimmy-chen-zheng/robot-interface-demo){ target=_blank }  
**Doc version:** v0.1 · **Last updated:** 2025/09/20

---

### 作者 Author

<div class="grid cards" markdown>
-   :material-account-circle: **陈峥 (Jimmy Chen)**
    
    :material-briefcase-outline: Java后端开发工程师
    :material-map-marker: 中国，深圳 
    
</div>







