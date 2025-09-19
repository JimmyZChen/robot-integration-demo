# 单体 → RuoYi-Cloud 微服务（Monolith → Microservices）

## 背景 / 痛点（Background / Pain points）
- 先用单体快速验证；规模上涨后发布影响面大、配置分散。  
  _Started with a monolith to validate quickly; as scale grew, releases had wide blast radius and configs were scattered._

## 目标（Goals）
- 平滑迁移，前端基本无感，**分钟级回滚**。  
  _Smooth migration, **frontend largely transparent**, **minute-level rollback**._

## 关键设计（Key design）
- Gateway 路由：`/external/gs/**`  
- Nacos：注册/配置（多环境）  
- Docker Compose：**一键多容器**交付  
  _Gateway `/external/gs/**`; Nacos for service registry/config (multi-env); Docker Compose for one-click multi-container._

## 难点与取舍（Challenges & trade-offs）
- DTO **抽到** `ruoyi-api-robot`  
- Controller **变薄** → 逻辑**下沉**到 `GsOpenApiServiceImpl`  
  _Extract DTOs to `ruoyi-api-robot`; thin controllers → push logic down into `GsOpenApiServiceImpl`._

## 指标与结果（Metrics & results）
- 冷启动：**〈原〉→〈现〉**  
- 回滚：**〈小时〉→〈分钟〉**  
- **p95**：**〈数值〉 ms**  
  _Cold start **<before> → <after>**; rollback **<hours> → <minutes>**; p95 **<value>** ms._  
> 注：把尖括号里的占位改成你的真实数据。

## 复盘（Retrospective）
- **先交付后演进**；统一 **资源名 / 规则 / 异常体 / 日志字段**。  
  _Deliver first, evolve later; unify **resource names / rules / error body / log fields**._
