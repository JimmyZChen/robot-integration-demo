# Overview

> This chapter gives the overall path and reading order.  
> Use the mini-TOC on the right; click any item in “Recommended Reading Order” to jump to its section.

## 0. Recommended Reading Order

1. [Robot Management Platform (What / Why)](#sec-what-why) — background & goals  
2. [From Monolith to Microservices](#sec-mono-to-micro) — migration strategy & boundaries  
3. [Architecture Overview](#sec-architecture) — components & trade-offs  
4. [Project Phases & Milestones](#sec-phases) — break big goals into small steps  
5. [OpenAPI Wrapping & Stability Governance](#sec-openapi-stability) — interface governance & resilience  
6. [Service Level Objectives (SLO)](#sec-slo) — target metrics & continuous improvement

---

## 1. Robot Management Platform (What / Why) {#sec-what-why}

**What**  
- A unified entry for multiple vendors’ robots (cleaning, patrol, delivery, etc.).  
- Normalize vendor OpenAPIs; provide orchestration, scheduling, monitoring, and safety guards.

**Why**  
- **Integration pain**: different auth/signing, request/response formats, error semantics.  
- **Stability**: rate-limits, retries, backoff, circuit-breakers, graceful degradation.  
- **Ops**: full-link tracing, metrics, logs; issue → root cause → fix loop.  
- **Delivery**: one-click multi-env delivery with Docker Compose.

---

## 2. From Monolith to Microservices {#sec-mono-to-micro}

**Migration Rationale**  
- Clear module boundaries (gateway / auth / robot-adapter / business).  
- Independent scaling for traffic hotspots (gateway / robot-adapter).  
- Observability at service granularity for faster RCA.

**Strategy**  
- Keep contracts stable; split services along domain boundaries.  
- Side-by-side verification; gray rollout; quick rollback path.

**Key Takeaways**  
- Start with the **gateway + adapter** as the first split.  
- Make observability & rate-limiting **first-class** from day one.

---

## 3. Architecture Overview {#sec-architecture}

> Shape: RuoYi-Cloud microservices (Gateway + business), Nacos for registry/config, RabbitMQ for async (see the Async page), MySQL/Redis for data & cache, SkyWalking for observability loop, Nginx for static frontends.

### 1) System Diagram

![Robot Management Platform Architecture](/assets/04-architecture.png)

**Relations**
- **Ingress**: `User/Browser → Nginx → Spring Cloud Gateway` (`/api/**` routes)  
- **Business**: `RuoYi-System` (Auth/ACL) and `RuoYi-Robot Adapter` (OpenAPI aggregation)  
- **Registry/Config**: via **Nacos** (multi-environment)  
- **Data**: `MySQL` (business data) and `Redis` (cache)  
- **Async**: `RabbitMQ` (topic→queue→DLQ, manual-ack, idempotency)  
- **Observability**: `SkyWalking` (trace/log/metrics stitched by `traceId`)  
- **Delivery**: Docker Compose for multi-env one-click bring-up

---

## 4. Project Phases & Milestones {#sec-phases}

- Phase-1… (keep your original list; content unchanged, English wording aligned)  
- Emphasize increments: gateway governance → async → observability → stability → delivery.

---

## 5. OpenAPI Wrapping & Stability Governance {#sec-openapi-stability}

**Goals**
- Unify vendor OpenAPIs; normalize auth/signature; consistent error model.  
- Shield upstream from vendor quirks with adapters and fallback policies.

**Design**
- Adapter layer for request/response mapping; consistent `code/message`.  
- Retry/backoff where safe; **rate-limit first**, circuit-break later; idempotency for commands.  
- Degrade reads to last good data when vendor services flap.

**Observability**
- Inject `traceId` end-to-end; correlate APM/Logs/Metrics.  
- Dashboards for success rate, latency (p95/p99), 429 ratio, DLQ burn-down.

---

## 6. SLO (Service Level Objectives) {#sec-slo}

**Scope**: Gateway + Robot Service; **Window**: 28 days (monthly).  
**Success**: HTTP non-5xx **and** business `code==0`; **policy 429 (rate-limit)** not counted as failure (tracked separately for capacity tuning).  
**Latency**: measured from **Gateway ingress → response out**; track p95/p99 by API groups.

**Review**
- Use 429 ratio + DLQ trends + retry outcomes to tune thresholds and capacities.  
- Postmortems for breaches; feed learnings into rate-limit and retry policies.

---

### Links

- Live Demo: [live-demo.md](live-demo.md)  
- Observability (SkyWalking): [observability.md](observability.md)  
- Stability (Sentinel): [stability-sentinel.md](stability-sentinel.md)  
- Async (RabbitMQ): [mq-async.md](mq-async.md)  
- Load Test (JMeter): [loadtest-jmeter.md](loadtest-jmeter.md)
