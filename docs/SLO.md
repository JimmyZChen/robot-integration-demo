# Service Level Objectives (SLO)

> Scope: Spring Cloud Gateway + Robot Service.  
> Window: 28 days (monthly).  
> Success definition: Count as success when the HTTP status is not 5xx and the business `code==0`; **intentional 429 (rate limiting) is not counted as a failure** and is tracked separately for capacity and threshold tuning.  
> Latency: by default, measure the duration from **Gateway ingress → response sent**.

---

## 📈 SLO (English)

### 1) SLO Table
| Journey / API | SLI | Target | Notes |
|---|---|---|---|
| Status query `GET /external/gs/status/**` | Success ≥ 99.9% | Monthly | Gateway rate-limit first; single-instance stable QPS × 0.7 headroom |
|  | P95 < 300ms (P99 < 800ms) | Monthly | Client typically retries with backoff 1–2 times |
| Map list `GET /maps/list/**` | Success ≥ 99.9% | Monthly | Read-heavy; cache/replica |
|  | P95 < 400ms | Monthly | API baseline |
| Task dispatch (async acceptance) `POST /external/gs/task/**` | Acceptance success ≥ 99.5% | Monthly (~3.6h budget) | Count success only if **persisted + enqueued**; idempotency key `taskId` |
|  | Acceptance P95 < 1s | Monthly | Synchronous “accepted” only; **execution ACK not in this SLO** |
| WebSocket updates | Reconnect 99% < 3s | Monthly | Auto-reconnect; `stale` triggers alert |

### 2) SLI Definitions
- **Success rate** = (requests − HTTP 5xx − business failures) ÷ requests; business failure per unified `code`.  
- **Latency**: P50/P95/P99 from **gateway ingress to response**; add service spans if needed.  
- **Async acceptance**: HTTP 202/200 **and** persisted+enqueued = success (requires app metric).  
- **WebSocket recovery**: disconnect to “receiving again” (heartbeat/subscription ack).

### 3) Protection thresholds (aligned with Sentinel)
- **Slow-call threshold** `τ = min(1000ms, 1.2 × current baseline P95)`  
- **Window** 10s; **minimum samples** ≥ 20; **slow-call ratio** ≥ 50% → open circuit  
- **Open** 30s; **Half-open probes** 5–10  
- **Ingress rate-limit** on `/external/gs/**` at Gateway (returns 429)

### 4) Alerting & Actions
- **Error budget**: 99.9% target ⇒ 0.1% monthly  
- **Burn rate alerts** (either condition): 1h > 10% budget ⇒ P1 (auto degrade/limit); 6h > 20% ⇒ P1 escalate (rollback / remove unhealthy instance)  
- **Release guard**: within 15min after release, if P95/P99 worsens and error > threshold ⇒ pause/rollback  
- **Traffic control**: ramp Gateway quotas 5%→30%→50%→100%; if worse, staged circuit with stable fallback

### 5) Observability & Sources
SkyWalking traces/metrics; structured logs with `traceId` + 429/503/timeout fields; Nacos groups for rules with gray & rollback.

### 6) Exceptions
Execution SLA of long-running async tasks is **out of scope** here; external-network incidents are labeled for review, not forcibly excluded.

---

# Service Level Objectives (SLO) · 服务等级目标

> Scope 范围：Gateway（Spring Cloud Gateway）+ Robot Service。  
> Window 统计窗口：28 天（月度）。  
> Success 口径：HTTP 非 5xx 且业务 `code==0` 计成功；**策略性 429（限流）不计失败**，单独跟踪其比例用于容量与阈值校准。  
> Latency 延迟：默认以 **Gateway 入站→响应发出** 的时长统计。

---

## 📈 SLO（中文）

### 1) 指标表
| 用户旅程 / 接口 | SLI | 目标值 | 说明 |
|---|---|---|---|
| 机器人状态查询 `GET /external/gs/status/**` | 成功率 ≥ 99.9% | 月度达标 | 网关限流在前；单实例稳定 QPS × 0.7 预留冗余 |
|  | P95 < 300ms（P99 < 800ms） | 月度达标 | 客户端通常 1–2 次退避重试 |
| 地图列表 `GET /maps/list/**` | 成功率 ≥ 99.9% | 月度达标 | 稳定读；缓存/多副本 |
|  | P95 < 400ms | 月度达标 | 接口基线 |
| 下发任务（异步受理）`POST /external/gs/task/**` | 受理成功率 ≥ 99.5% | 月度达标（≈3.6h 预算） | 入库+入队成功计为“受理成功”；幂等键 `taskId` |
|  | 受理 P95 < 1s | 月度达标 | 同步返回“已受理”；**执行 ACK 不纳入本 SLO** |
| WebSocket 状态推送 | 断线后恢复：99% < 3s | 月度达标 | 自动重连；`stale` 触发告警 |

### 2) SLI 统计口径
- **成功率** = (总请求 − HTTP 5xx − 业务失败码) ÷ 总请求；业务失败码以统一 `code` 字段为准。  
- **延迟**：统计 P50 / P95 / P99（Gateway 入站→出站）；必要时补充服务内子跨度。  
- **受理成功率（异步）**：HTTP 202/200 且**入库+入队成功**才计成功（需服务内埋点）。  
- **WebSocket 恢复**：从断开到重新收流（心跳/订阅确认）的时长分布。

### 3) 保护阈值（与 Sentinel 规则对齐）
- **慢调用阈值** `τ = min(1000ms, 1.2 × 当前基线 P95)`  
- **统计窗口** 10s；**最小样本数** ≥ 20；**慢调占比** ≥ 50% → 打开熔断  
- **Open** 30s；**Half-Open 探测** 5–10 请求  
- **入口限流**：优先在 Gateway 对 `/external/gs/**` 做 API 组限流（命中统一 429）

### 4) 告警与处置（把 SLO 变成操作）
- **错误预算**：目标 99.9% ⇒ 月度预算 0.1%  
- **燃尽告警（任一满足即告警）**：  
  - 1 小时消耗 > 10% 预算 ⇒ P1，自动切 **降级/限流回退组**  
  - 6 小时消耗 > 20% 预算 ⇒ P1 升级，**灰度回滚**或**摘除不健康实例**  
- **发布管控**：发布后 15 分钟内若 P95/P99 恶化且错误率超阈 ⇒ 暂停灰度/回滚  
- **流量治理**：Gateway 限流配额按 5%→30%→50%→100% 梯度放大；持续恶化时分级熔断并提供稳定兜底

### 5) 观测与数据来源
- SkyWalking：Trace/指标（请求数、错误、分位延迟）  
- 统一日志：按 `traceId` 串联，结构化记录 429/503/超时字段  
- Nacos：`*-gw-api-defs` / `*-gw-flow-rules` 分组，支持灰度与一键回滚

### 6) 例外
- 异步任务的**执行结果**不计入“受理成功率”，另立“任务执行 SLA”  
- 网外网络问题在统计中标注“外部原因”维度用于复盘，不从工程侧强行剔除


<<<<<<< HEAD
=======
# Service Level Objectives (SLO)

> Scope: Spring Cloud Gateway + Robot Service.  
> Window: 28 days (monthly).  
> Success definition: Count as success when the HTTP status is not 5xx and the business `code==0`; **intentional 429 (rate limiting) is not counted as a failure** and is tracked separately for capacity and threshold tuning.  
> Latency: by default, measure the duration from **Gateway ingress → response sent**.

---

## 📈 SLO (English)
>>>>>>> ac067da2 (更新 SLO)


