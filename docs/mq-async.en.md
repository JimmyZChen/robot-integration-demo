# Async (RabbitMQ)

> **Goal**: decouple request vs. execution via messaging and **dispatch tasks asynchronously**.  
> - Client → Gateway → Producer persists & publishes (publisher confirm) → immediately return **202** with a task status URL  
> - Consumer uses **manual ack**, idempotency via Redis `SETNX`; failures go to **retry** (q.1m → q.5m → q.30m), then to **DLQ**  
> - Inject `traceId` at every hop to stitch **APM / Log / Metrics**

---

## 1. Queue Monitoring & Async Architecture

### 1.1 Queue Monitoring (Queue)

<a href="/assets/rabbitmq-queue.png" target="_blank" rel="noopener">
  <img src="/assets/rabbitmq-queue.png" alt="RabbitMQ Queue Monitoring">
</a>

- Watch: **ready / unacked / total**, bindings (routing key / exchange), DLX/DLQ settings, etc.  
- Gateway can set **queue depth thresholds** to enable a **429 guard** (protect the ingress).

### 1.2 Async Architecture

<a href="/assets/rabbitmq-arch.png" target="_blank" rel="noopener">
  <img src="/assets/rabbitmq-arch.png" alt="Async Architecture (task dispatch / retry / DLQ flow)">
</a>

- Producer: return **202** only after **publisher confirms**; write Redis for `status/progress/result` (with TTL).  
- Exchange/Queue: topic exchange; **robot.task.q** as work queue, **robot.retry** for multi-stage retries, finally to **DLQ**.  
- Consumer: `prefetch = N`, **manual ack**; on failure, publish to `robot.retry`.

---

## 2. Integration Design & Key Parameters

[![](/assets/rabbitmq-params-code.png){ .img-90 }](/assets/rabbitmq-params-code.png)
[![](/assets/rabbitmq-exchange.png){ .img-90 }](/assets/rabbitmq-exchange.png)

- `publisher-confirm-type: correlated` & `publisher-returns: true`, `mandatory: true` to make **returns observable**.  
- **Concurrency**: `concurrency=2`, `max-concurrency=8`, **prefetch=20** (total in-flight ≈ 40).  
- **TTL**: `message-ttl=3600s`; **task result TTL** can be 24h.  
- **No requeue on exceptions**: `default-requeue-rejected: false` to avoid “zombie” retries.

---

## 3. Consumption & Error Handling

[![](/assets/rabbitmq-dlq.png){ .img-90 }](/assets/rabbitmq-dlq.png)
[![](/assets/rabbitmq-consumer-config.png){ .img-90 }](/assets/rabbitmq-consumer-config.png)
[![](/assets/rabbitmq-declare-code.png){ .img-90 }](/assets/rabbitmq-declare-code.png)

- **Failure handling**: route to `robot.retry` first (q.1m → q.5m → q.30m); after exhausting retries → `robot.task.dlq`.  
- **Idempotency**: Redis `SETNX` on `taskId` to ensure **exactly-once business execution** at the consumer side.  
- **Observability**: stitch the chain with `traceId` across **Gateway / Producer / RabbitMQ / Consumer** for fast RCA.

---

## 4. Integration Results (Postman)

[![](/assets/rabbitmq-postman-create.png){ .img-90 }](/assets/rabbitmq-postman-create.png)
[![](/assets/rabbitmq-postman-status.png){ .img-90 }](/assets/rabbitmq-postman-status.png)

- **Create Task**: returns `202 Accepted` + `statusUrl`.  
- **Query Task**: look up Redis (or DB) by `taskId`, returns **PENDING / RUNNING / SUCCESS / FAIL**.  
- Inject **X-Request-Id** in headers to align with logs/traces.
