# Gaussian OpenAPI 封装与稳定性治理（OpenAPI & Stability）

## 背景 / 痛点（Background / Pain points）
- 厂商 API RT/错误不稳，调用方易被拖慢。  
  _Vendor API latency/errors were unstable; callers easily got dragged down._

## 目标（Goals）
- 监权/重试/幂等/Trace 统一；**双层保护**（网关 + 方法级）；**读多写少**的读路径支持**缓存兜底**。  
  _Unified auth/retry/idempotency/trace; dual protection (gateway + method level); read-heavy paths use cache fallback._

## 关键设计（Key design）
- `GsOpenApiServiceImpl` + `@SentinelResource`（**资源名与 Nacos 规则一致**）  
- Nacos **下发** `Flow/Degrade` 规则  
- `RestTemplate`：**超时/连接池**设置，**禁用自动重试**  
  _GsOpenApiServiceImpl + @SentinelResource (resource names aligned with Nacos rules); Nacos-pushed Flow/Degrade; RestTemplate with timeouts/connection pool and auto-retry disabled._

## 难点与取舍（Challenges & trade-offs）
- 网关 vs 应用**谁先挡**：压测时需放宽网关阈值，观察熔断位置。  
  _Gateway vs application—who throttles first; during load tests, loosen gateway thresholds to observe circuit breaking._

## 指标与结果（Metrics & results）
- 突发下 **429 快速失败**；**p95** 〈数值〉 ms；读接口**降级**返回**最近数据**。  
  _Under bursts, HTTP **429** fast-fail; **p95** <value> ms; read APIs degrade to **most recent data**._

## 复盘（Retrospective）
- **限流优先于熔断**；统一**异常体/日志字段**便于排障。  
  _Rate limiting before circuit breaking; standardized error body/log fields to ease troubleshooting._

> 深入细节：见 [异步化（RabbitMQ）](../mq-async.md) · [稳定性（Sentinel）](../stability-sentinel.md)
