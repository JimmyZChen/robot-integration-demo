# Stability (Sentinel)

> **Goals**: **Flow limiting** and **Circuit breaking / Degrade**.  
> Rules are centrally managed and delivered via **Nacos**.  
> Reads use short-TTL cache; writes **fail fast** (429/503) to avoid cascading failures.

---

## 1. Centralized Rule Governance

[![](/assets/sentinel-rules-flow.png){ .img-90 }](/assets/sentinel-rules-flow.png)
[![](/assets/sentinel-rules-degrade.png){ .img-90 }](/assets/sentinel-rules-degrade.png)

- **Rule source**: delivered uniformly from Nacos Config Center; **resource names match code** for easy troubleshooting and rollback.  
- **Guidance**: apply short-TTL cache for read APIs; set **fast-fail** thresholds for write APIs to shorten recovery time.

---

## 2. Rate Limiting: Trigger Logs

[![](/assets/sentinel-rate-limit-logs.png){ .img-90 }](/assets/sentinel-rate-limit-logs.png)

- When `FlowException` is triggered, log a clear **blocked** record including **resource name / trigger origin**, etc.  
- Gateway/core services can provide different fallbacks based on the resource name.

---

## 3. Rate Limiting: Test Results

[![](/assets/sentinel-rate-limit-result.png){ .img-90 }](/assets/sentinel-rate-limit-result.png)

- Observe pass/reject **QPS**, **concurrency**, **avg RT**, and **per-minute pass/reject** metrics.  
- Verify that the load test method and thresholds meet expectations (**don’t drag down the main flow**).

---

## 4. Circuit Breaking / Degrade: Trigger Logs

[![](/assets/sentinel-degrade-logs.png){ .img-90 }](/assets/sentinel-degrade-logs.png)

- Record the reason when `DegradeException` is triggered or a fallback executes to quickly locate the source.  
- **Recommendation**: isolate rollback/retry paths from the main flow to avoid avalanche effects.

---

## 5. Circuit Breaking: Test Results

[![](/assets/sentinel-degrade-result.png){ .img-90 }](/assets/sentinel-degrade-result.png)

- For key resources, monitor **degrade count/duration**, **avg RT**, and **success rate** to ensure **no spread, no amplification**.

---

- Convention: **Fallback/BlockHandler** for **integration / task-dispatch** APIs are aggregated in the **Service layer** (thin Controller).  
- Resource names follow a unified convention (consistent with rules); inject **`traceId`** into logs to enable **trace ↔ log** cross-checks.
