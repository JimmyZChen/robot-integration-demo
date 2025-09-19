# What / Why 机器人管理平台 — Robot Management Platform

> **一句话**：统一管理机器人状态、地图/区域与临时任务，聚合多厂商 OpenAPI，向业务系统提供一致的接口与入口。  
> **One line**: Unified platform for robot status/maps/zones/ad-hoc tasks; aggregates vendor OpenAPIs and exposes a consistent gateway to business apps.

---

## What（在做什么）

- **统一管理**：机器人状态、地图/区域、临时任务  
  _Manage robot status, maps/zones, and ad-hoc tasks in one place._

- **聚合开放接口**：多厂商 OpenAPI（如 Gaussian），对外提供一致接口  
  _Aggregate vendor OpenAPIs (e.g., Gaussian) behind a consistent API._

- **统一入口与治理**：Gateway 鉴权、审计、限流前置  
  _Single entry via Gateway for auth, audit, and rate limiting._

---

## Why（为什么从单体演进）

- **接口变化快、能力差异大**：先把“接口聚合/任务下发”独立，可快速迭代与回滚  
  _Fast-changing vendor APIs → split aggregation/dispatch for safer iteration and rollback._

- **峰值与抖动**：需要限流/熔断与故障隔离，避免拖垮核心链路  
  _Peaks & vendor hiccups → rate limiting/circuit breaking and isolation._

- **多环境协作与交付**：Docker 一键交付，服务独立发布/扩缩容  
  _Multi-env delivery → one-click Docker; independent deploy/scale._

---

_演进路线见「架构与演进（Architecture & Evolution）」页面。_
