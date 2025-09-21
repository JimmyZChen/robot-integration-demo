---
title: Robot Scheduling Docs
hide:
  - toc
---

# Robot Scheduling Docs  
A unified gateway for multi-vendor robots with async orchestration and observability.

> **One-liner**  
> Unified access to multiple vendors’ OpenAPIs, front-gate governance (auth/audit/rate-limit), async flood-resilience (MQ), observability (traces/metrics), and one-click delivery (Docker Compose).

[Get Started ⟶](overview.md){ .md-button .md-button--primary }
[Architecture ⟶](overview.md#sec-architecture){ .md-button }
[Live Demo Guide ⟶](live-demo.md){ .md-button }

---

## What problems does it solve
- :material-source-branch: **Multi-vendor aggregation**: Unified entry and contract constraints for heterogeneous OpenAPIs  
- :material-shield-check: **Front-gate governance**: Gateway auth, audit, rate limiting, and canary/gray rollout  
- :material-rabbit: **Async decoupling**: RabbitMQ (Topic→Queue→DLQ), manual ack, idempotency checks  
- :material-eye: **Observability**: SkyWalking end-to-end traces / metrics / topology  
- :material-rocket-launch: **One-click delivery**: Docker Compose with multi-environment parameterization

> For detailed **What / Why / evolution path**, see:  
> [Overview](overview.md#sec-what-why){ .md-button }
---

<h2>Quick Links</h2>

<div class="quicklinks">

  <a class="ql" href="overview/">
    <strong>Overview</strong>
    <em>Why, architecture, evolution, and stability governance</em>
  </a>

  <a class="ql" href="live-demo/">
    <strong>Live Demo</strong>
    <em>Screenshots & notes (PPT pages 10–18)</em>
  </a>

  <a class="ql" href="mq-async/">
    <strong>Async (RabbitMQ)</strong>
    <em>Architecture, params, consumption & error handling, integration results</em>
  </a>

  <a class="ql" href="observability/">
    <strong>Observability (SkyWalking)</strong>
    <em>Service/endpoint overview, topology, traces, dashboards</em>
  </a>

  <a class="ql" href="stability-sentinel/">
    <strong>Stability (Sentinel)</strong>
    <em>Flow-control/degrade rules, trigger logs, and code snippets</em>
  </a>

  <a class="ql" href="config-nacos/">
    <strong>Config Center (Nacos)</strong>
    <em>Multi-env configs & rule management (centralized delivery / hot reload)</em>
  </a>

  <a class="ql" href="docker-deploy/">
    <strong>Containerized Deploy</strong>
    <em>Runtime overview, Compose directories & config excerpts</em>
  </a>

  <a class="ql" href="loadtest-jmeter/">
    <strong>Load Test (JMeter)</strong>
    <em>Plan, results, MQ-side verification & service metrics</em>
  </a>

  <a class="ql" href="issues-rca/">
    <strong>Issues · RCA</strong>
    <em>Gateway/Nacos/Sentinel, observability, and code-side common issues</em>
  </a>

</div>

---

!!! note "Data & API Notice"
    - Demo data are **sanitized/mocked** and **won’t hit real devices**.  
    - Gateway supports `X-Dry-Run: true` safety switch; sensitive tokens / map names are masked.  
    - The repo is for **structure & practice demonstration** and is not wired to production by default.

---

### Repos & Version
[GitHub](https://github.com/JimmyZChen/robot-integration-demo){ target=_blank } ·
[Gitee](https://gitee.com/Jimmy-chen-zheng/robot-interface-demo){ target=_blank }  
**Doc version:** v0.1 · **Last updated:** 2025/09/20

---

### Author

<div class="grid cards" markdown>
-   :material-account-circle: **Jimmy Chen (Chen Zheng)**
    
    :material-briefcase-outline: Java Backend Engineer  
    :material-map-marker: Shenzhen, China 
    
</div>
