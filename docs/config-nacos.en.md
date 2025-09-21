# Configuration Center (Nacos)

> Unified registry/config; multi-environment grouping; centralized management of sensitive parameters (hot reload)

[![](/assets/nacos-config.png){ .img-90 }](/assets/nacos-config.png)

**Notes**
- Centralized management for business and gateway configs (e.g., `application-*.yml`, `ruoyi-*-dev.yml`), separated by **Group/Namespace** per environment.
- Sentinel rules (Flow/Degrade, etc.) are delivered via Nacos with **resource names consistent with code**, supporting hot updates and rollback.
