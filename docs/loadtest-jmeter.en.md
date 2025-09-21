# Load Test (JMeter)

> **Goal:** Verify the stability and observability of the ingress path under target throughput; ensure **no real devices** are hit using **Dry-Run** and **disabled consumers**; cross-validate via **RabbitMQ** and **SkyWalking** metrics.

---

## 1. Test Scope & Path

[![](/assets/loadtest/p01-scope-topology.png){ .img-90 }](/assets/loadtest/p01-scope-topology.png)

- Path: **Gateway → ruoyi-robot → MQ**
- Risk control:
  - **Dry-Run**: all requests carry `X-Dry-Run: true`; the gateway returns 403 on real branches.
  - **Disable consumers**: downstream consumer process/thread count = 0; only validate ingress writes and throughput.
  - **Peripheral isolation**: tasks are mock no-ops; no actual device actions are triggered.

---

## 2. Traffic Model & Method

[![](/assets/loadtest/p02-plan-thread-group.png){ .img-90 }](/assets/loadtest/p02-plan-thread-group.png)  
[![](/assets/loadtest/p02-plan-ct.png){ .img-90 }](/assets/loadtest/p02-plan-ct.png)

- Target **100 QPS** for **3 minutes**; use **Constant Throughput Timer** to control global rate.
- Assertions/post-processors: keep necessary fields (request headers, injected `traceId`, HTTP status, etc.).

---

## 3. Results · Overview

[![](/assets/loadtest/p03-agg-1.png){ .img-90 }](/assets/loadtest/p03-agg-1.png)  
[![](/assets/loadtest/p03-agg-2.png){ .img-90 }](/assets/loadtest/p03-agg-2.png)  
[![](/assets/loadtest/p03-agg-3.png){ .img-90 }](/assets/loadtest/p03-agg-3.png)

- Error rate 0%, throughput stable; P95/P99 latency within expectations.

---

## 4. Results · Details

[![](/assets/loadtest/p04-view-results.png){ .img-90 }](/assets/loadtest/p04-view-results.png)

- Response `200 OK`; key headers/tags such as `X-Which-Route`, `traceId` are present for **Trace ↔ Log** cross-checks.

---

## 5. Message-Side Verification

[![](/assets/loadtest/p05-mq-verify-a.png){ .img-90 }](/assets/loadtest/p05-mq-verify-a.png)  
[![](/assets/loadtest/p05-mq-verify-b.png){ .img-90 }](/assets/loadtest/p05-mq-verify-b.png)

- Queue: `robot.tempTask.q` (example); requests are visible as **enqueued**, no consumption (consumers disabled), DLX/DLQ rules OK.

---

## 6. Gateway Service Metrics

[![](/assets/loadtest/p06-gateway-metrics.png){ .img-90 }](/assets/loadtest/p06-gateway-metrics.png)

- Apdex, success rate, service load, instance load, endpoint load remain stable at 100 QPS, without abnormal spikes.

---

## 7. Business Service Metrics

[![](/assets/loadtest/p07-robot-metrics.png){ .img-90 }](/assets/loadtest/p07-robot-metrics.png)

- Service level: average latency, P95/P99, success rate stay stable.  
- Instance level: load distribution is balanced, no obvious skew.

---

## 8. Endpoint Load & Latency

[![](/assets/loadtest/p08-overview.png){ .img-90 }](/assets/loadtest/p08-overview.png)  
[![](/assets/loadtest/p08-endpoints.png){ .img-90 }](/assets/loadtest/p08-endpoints.png)

- Top-N endpoint load/latency visualization.  
- Troubleshooting flow: locate slow/error endpoints → correlate traces → back-reference logs by `traceId`.
