# Issues · Root Cause · Fixes

> Environment: RuoYi-Cloud (ruoyi-modules-robot / ruoyi-api-robot), Gateway unified entry `/external/gs/**`;  
> Nacos registry & config, Sentinel dynamic rules, SkyWalking full-link, optional RabbitMQ async; Docker Compose one-click delivery.

---

## 0. Go-live Definitions & Load-test Baseline

- **Ambiguous definitions & baseline**  
  **Symptom**: Different teammates use inconsistent definitions for “success/failure” and whether 429 counts, causing metric misinterpretation.  
  **Fix**: Standardize definitions & baseline:  
  - **Dry-Run / consumers disabled** load-test baseline: 100 QPS × 3 minutes; count 200/429 as success (to validate gateway rate-limit/stability).  
  - **Observed metrics**: P95/P99, Throughput, Error%, queue backlog, SkyWalking Success Rate.  
  - **Sample result**: 18,035 samples **P95 = 6 ms**, **P99 ≈ 10 ms**, **Error = 0**, Throughput ≈ **100.2/s** (consistent across rounds).

- **Rollback & cold-start not quantifiable**  
  **Fix**: Persist to a “Change Log”:  
  - **Cold start**: Docker Compose + initial SQL → **≤ 12 minutes** to bring up a new env;  
  - **Rollback**: after microservices split **≤ 5 minutes** (revert image/config).

---

## 1. Gateway / Nacos / Sentinel

- **Nacos stuck at STARTING**  
  **Cause**: Persistent **MySQL version/charset mismatch** or init SQL not imported.  
  **Fix**: Complete `spring.datasource.*`, import official/project init scripts; align versions & images (recommend **2.2.x**).

- **Gateway 404 / Swagger “Try it out” fails**  
  **Cause**: `StripPrefix/Path` inconsistent with downstream **`context-path`**; route not updated after prefix change.  
  **Fix**: Unify path strategy; if needed **remove** `context-path` or adjust Strip rules accordingly; deprecate old prefixes after gray rollout.

- **Rate-limit paths inconsistent, rules not matched**  
  **Cause**: `@SentinelResource` **scattered in Controllers**, inconsistent resource names.  
  **Fix**: Consolidate into **`GsOpenApiServiceImpl`**; standardize resource naming and map one-to-one with Nacos rules  
  (e.g., `ruoyi-gateway-gw-api-defs/gw-flow/gw-degrade`).

- **Sentinel rules not applied / empty group**  
  **Cause**: Missing `sentinel-datasource-nacos` dependency, or **DataId/Group vs JSON** mismatch.  
  **Fix**: Add dependency; verify the triple `{AppName, DataId, Group}` and JSON structure; before load test raise gateway thresholds to avoid premature 429 blocking.

- **CORS preflight blocked**  
  **Cause**: **OPTIONS** not allowed or response headers missing.  
  **Fix**: Configure global CORS: allow required `Origin/Headers/Methods`, expose necessary headers; explicitly allow **OPTIONS**.

---

## 2. Observability & Infrastructure

- **No Gateway spans / logs missing `traceId`**  
  **Cause**: **SCG/WebFlux plugin** not loaded; `-javaagent` not injected; Logback lacks MDC.  
  **Fix**:  
  - Put `apm-spring-cloud-gateway-3.x-plugin` into **plugins**;  
  - Start with `-javaagent:/path/to/agent/agent.jar`;  
  - Add `%X{traceId}` to Logback pattern; raise sampling rate during load tests, reduce noise in production per QPS.

- **Docker: MySQL port conflict / no data on first run**  
  **Cause**: Host occupying `3306` or init SQL not imported.  
  **Fix**: Adjust port mapping or stop host MySQL; **pre-import SQL in compose**; add `healthcheck` and use `depends_on.condition=service_healthy` upstream.

- **In-container access fails (misusing host `localhost`)**  
  **Cause**: Containers treat host address as local.  
  **Fix**: Container-to-container calls use **serviceName:port**; to access host on Win/Mac, use `host.docker.internal`.

- **Dependency/plugin conflicts (e.g., Reactor Netty)**  
  **Cause**: Spring Boot/Cloud matrix misaligned with dependencies.  
  **Fix**: Lock a version matrix; include only needed gateway/webflux/skywalking plugins and verify compatibility.

---

## 3. Asynchrony & Idempotency

- **Queue keeps growing during load test / duplicate consumption**  
  **Cause**: Demo env **consumers disabled (Dry-Run)** or missing **prefetch/manual ack**; retries without upper bound.  
  **Fix**:  
  - Load-test definition: with consumers off, only validate ingress & observability; backlog is acceptable;  
  - Production: enable consumers, set `prefetch = n`, **manual ack**, failures go to **DLQ**;  
  - For write operations add **idempotency keys**; timeouts/retries must **not amplify failures**.

- **RestTemplate pile-up / retries amplify issues**  
  **Cause**: No timeouts/connection pool; automatic retries enabled.  
  **Fix**: Set `connect/read` timeouts, pool size; disable automatic retries; enforce idempotency on critical writes.

---

## 4. Code & Frontend (RuoYi + Vue)

- **MyBatis `Invalid bound statement`**  
  **Cause**: `mapper-locations` or **XML `namespace` ≠ interface FQN**; XML not packaged.  
  **Fix**: Align `@MapperScan + namespace`; ensure XML are scanned and packaged.

- **Frontend CORS / port mismatch**  
  **Cause**: Proxy not updated when switching environments.  
  **Fix**: Unify proxy in `vue.config.js`; ports/targets switch with env variables.

- **Minor UI issues (from phase summary)**  
  **Symptom**: Area/map names wrap, status doesn’t auto-refresh, response structures inconsistent, etc.  
  **Fix**:  
  - Adjust table column width + `tooltip`;  
  - Add “manual refresh” (add timed refresh later);  
  - **Robust parsing** for API responses (“string/object”) to avoid misleading task dispatch hints;  
  - Missing `areaId` ↔ coordinates mapping → align with vendor data table, include in later milestones.

---

## 5. Conventions & Governance (Unified Naming & Traceability)

- **Inconsistent resource names / log fields → hard to troubleshoot**  
  **Fix**:  
  - Standardize resource naming (aligned with Sentinel rules) and unify exception bodies;  
  - Inject `traceId`, `requestId`, `taskId`, `resource` into logs;  
  - Postmortem process: **deliver first, evolve later**; leave traces for changes & rollbacks; codify “Issue → Cause → Fix” into docs.

---

### Appendix: Reusable Checklist (Pre-Go-Live Self-Check)
- [ ] `gateway` routes/prefixes consistent with downstream paths;  
- [ ] Nacos rules (DataId/Group) aligned with application resource names;  
- [ ] SkyWalking `-javaagent` injected, Gateway/WebFlux plugins in place;  
- [ ] Docker `healthcheck` complete; initial SQL auto-imported at first boot;  
- [ ] Load-test definitions when consumers are off; for production enable consumers with DLQ;  
- [ ] CORS, timeouts, connection pool, idempotency & retry policies in place;  
- [ ] Logs contain trace fields; support **trace ↔ log** cross-checks.

> Metrics source: phase summary & demo load-test records (baseline 100 QPS, P95 ≈ 6 ms, P99 ≈ 9–10 ms, Error = 0).
