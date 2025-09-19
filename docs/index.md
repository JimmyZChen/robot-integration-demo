# 项目概览（Executive Summary）

> 统一接入多厂商 OpenAPI，前置治理（鉴权/限流）、异步化抗洪、可观测闭环与一键交付。

[![](assets/live/p10-dashboard.png){ .img-90 }](assets/live/p10-dashboard.png)

## 我们在做什么
- 统一管理：机器人状态 / 地图分区 / 临时任务  
- 治理前置：Gateway 鉴权/审计，限流前置  
- 异步化：RabbitMQ（Topic→Queue→DLQ）、手动 ack、幂等  
- 可观测：SkyWalking（拓扑、Trace、指标）  
- 交付：Docker Compose 一键部署

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

