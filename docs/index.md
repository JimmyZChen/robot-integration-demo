# 项目概览（Executive Summary）

> 统一接入多厂商 OpenAPI，前置治理（鉴权/限流）、异步化抗洪、可观测闭环与一键交付。

![封面/概览图](assets/01-overview.png) <!-- TODO: 把你的第1页图片命名为 01-overview.png -->

- 统一管理：机器人状态 / 地图分区 / 临时任务  
- 治理前置：Gateway 鉴权/审计，限流前置  
- 异步化：RabbitMQ（Topic→Queue→DLQ）、手动 ack、幂等  
- 可观测：SkyWalking（拓扑、Trace、指标）  
- 交付：Docker Compose 一键部署

**快速入口**：  
[快速开始](quickstart.md) · [架构演进](architecture.md) · [异步化](mq-async.md)
