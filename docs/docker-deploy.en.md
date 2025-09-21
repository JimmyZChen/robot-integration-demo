# Containerized Deployment (Dockerized Deployment)

> **Goal:** one-click up/down; unified network & configs; reproduce a full “Gateway + Services + Middleware” runtime locally.

---

## 1. Runtime Overview

![Docker Dashboard](/assets/docker-dashboard.png){ width="100%" }

- **Status:** CPU / memory usage, container health, and port mappings at a glance.  
- **Services:** `mysql`, `redis`, `nacos`, `ruoyi-gateway`, `ruoyi-system`, `ruoyi-robot`, …  
- **Operations:** Start/Stop/Restart per container or in batches — convenient for joint debugging and demos.

---

## 2. Directory Layout & Compose

![Compose layout & config snippet](/assets/docker-compose.png){ width="100%" }
![Compose layout & config snippet](/assets/docker-composecode1.png){ width="100%" }
![Compose layout & config snippet](/assets/docker-composecode2.png){ width="100%" }
![Compose layout & config snippet](/assets/docker-composecode3.png){ width="100%" }

**Directory structure (example)**  
- `docker-compose.micro.yml`: one-click bring-up for gateway / services / middleware  
- `nacos_*`, `mysql_*`, `redis_data/`: persistence & init scripts  
- `SkyWalking/`: observability (optional)  
- `01-common.yaml`: common env vars (ports, DB password, etc.)

**Compose key points (conventions for easy reuse)**  
- **Unified network:** `networks: [ruoyi-net]`, intra-container communication by service name  
- **Health checks:** add `healthcheck` for `mysql`, `nacos`, etc., to ensure dependency order  
- **Startup order:** `depends_on` + `condition: service_healthy` for reliable sequencing  
- **Port mappings:** e.g., `ruoyi-gateway: 30080:8080`, `mysql: 3307:3306`  
- **Centralized env vars:** put common items in `x-common-env` (or a separate `01-common.yaml`)

> In one sentence: turn “things humans must remember” into “things written in config,” so your own machine / teammates / demo machines can boot it in seconds.
