<template>
  <div class="app-container home">
    <el-row :gutter="16">
      <!-- 主信息区 -->
      <el-col :span="16">
        <!-- Hero -->
        <el-card shadow="never" class="hero">
          <div class="hero-title">{{ title }}</div>
          <div class="hero-sub">
            面向多厂商（宇树、高仙、……）的统一机器人接入、编排与调度平台。
            通过适配层屏蔽协议差异，提供任务编排、地图分区、状态监控与可观测性。
          </div>
          <div class="hero-actions">
            <el-button type="primary" @click="go(ROUTES.robotList)">进入机器人管理</el-button>
            <el-button @click="go(ROUTES.robotList)">查看任务调度</el-button>
          </div>
        </el-card>

        <!-- 核心能力 (三列等高) -->
        <el-card shadow="never" class="card" style="margin-top:16px">
          <div class="section-title">核心能力</div>
          <div class="features-grid">
            <div class="feature" v-for="f in features" :key="f.title" @click="featureJump(f)">
              <div class="feature-title">{{ f.title }}</div>
              <div class="feature-desc">{{ f.desc }}</div>
            </div>
          </div>
        </el-card>

        <!-- 技术栈 -->
        <el-card shadow="never" class="card" style="margin-top:16px">
          <div class="section-title">技术栈</div>
          <el-row>
            <el-col :span="12" class="stack-col">
              <div class="stack-title">后端</div>
              <ul class="stack-list">
                <li>Spring Boot / Spring Cloud Alibaba（Nacos、Gateway、OpenFeign）</li>
                <li>Sentinel（网关+服务双层限流、熔断降级）</li>
                <li>SkyWalking（链路 ↔ 日志联动、拓扑、指标）</li>
                <li>MySQL、Redis、分布式任务（XXL-JOB/Quartz 可选）</li>
              </ul>
            </el-col>
            <el-col :span="12" class="stack-col">
              <div class="stack-title">前端</div>
              <ul class="stack-list">
                <li>Vue 2、Vuex、Vue Router、Element UI</li>
                <li>Axios、ECharts、RuoYi-UI 权限与组件体系</li>
              </ul>
            </el-col>
          </el-row>
        </el-card>
      </el-col>

      <!-- 侧栏 -->
      <el-col :span="8">
        <!-- 已接入厂商：只保留高仙 -->
        <el-card shadow="never" class="card">
          <div class="section-title">已接入厂商</div>
          <ul class="vendor-list">
            <li>高仙 Gaussian（GS / S 系列）</li>
          </ul>

          <div class="section-subtitle">规划接入厂商（完成中）</div>
          <ul class="vendor-list vendor-pending">
            <li>宇树 Unitree（A1 / Go2 …）</li>
            <li>科沃斯、优地、九号、复旦微米等</li>
          </ul>
        </el-card>

        <!-- 快捷入口：全部跳到机器人管理 -->
        <el-card shadow="never" class="card" style="margin-top:16px">
          <div class="section-title">快捷入口</div>
          <div class="quick-links">
            <el-button plain @click="go(ROUTES.robotList)">机器人列表</el-button>
            <el-button plain @click="go(ROUTES.robotList)">地图 / 分区</el-button>
            <el-button plain @click="go(ROUTES.robotList)">任务模板</el-button>
            <el-button plain @click="go(ROUTES.robotList)">链路追踪</el-button>
          </div>
          <div class="quick-hint">（当前仅开通机器人管理，其他入口先统一跳转，后续接好路由再分开）</div>
        </el-card>

        <!-- 系统信息 -->
        <el-card shadow="never" class="card" style="margin-top:16px">
          <div class="section-title">系统信息</div>
          <ul class="sysinfo">
            <li>系统：{{ title }}</li>
            <li>版本：v{{ version }}</li>
            <li>构建：{{ buildTime }}</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
// 顶部 <script> 里
const ROUTES = {
  robotList: '/gsrobot'   // ← 这里改成你的真实路由
}


export default {
  name: 'Home',
  data() {
    return {
      ROUTES,
      title: process.env.VUE_APP_TITLE || '机器人集成调度系统',
      version: '1.0.0',
      buildTime: new Date().toLocaleString(),
      // 恰好 6 条，三列对齐
      features: [
        { title: '多厂商适配层', desc: '统一封装高仙/宇树等 OpenAPI，抽象设备模型与指令，隐藏协议差异。', jump: 'robot' },
        { title: '任务编排与调度', desc: '临时/周期任务、分区清扫、串并行编排、重试与限流。', jump: 'robot' },
        { title: '地图与定位', desc: '地图/分区管理、坐标转换、轨迹可视化。', jump: 'robot' },
        { title: '网关与安全', desc: '统一鉴权、签名校验、黑白名单、速率限制与 IP 保护。', jump: 'robot' },
        { title: '可观测性', desc: 'SkyWalking 全链路 + 日志关联，异常追踪与指标看板。', jump: 'robot' },
        { title: '高可用与扩展', desc: 'Sentinel 熔断降级、降载保护，插件式接入新型号。', jump: 'robot' }
      ]
    }
  },
  methods: {
    go(path) {
      this.$router.push(path).catch(() => {});
    },
    // 点击“核心能力”任意卡片，也跳转到机器人管理
    featureJump(f) {
      if (f.jump === 'robot') this.go(this.ROUTES.robotList)
    }
  }
}
</script>

<style scoped>
.home .hero {
  background: linear-gradient(135deg, #ecfff2, #e8f7ee);
  border: 1px solid #e6f0e8;
}
.hero-title { font-size: 22px; font-weight: 600; }
.hero-sub { margin-top: 8px; color: #606266; line-height: 1.6; }
.hero-actions { margin-top: 12px; }

.section-title { font-weight: 600; margin-bottom: 8px; }
.card { min-height: 120px; }

/* 三列等高栅格，避免掉行不齐 */
.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-gap: 16px;
}
.feature {
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 12px;
  min-height: 96px;
  cursor: pointer;
  transition: all .15s ease;
}
.feature:hover { border-color: #67c23a; box-shadow: 0 1px 6px rgba(0,0,0,.06); }
.feature-title { font-weight: 600; }
.feature-desc { color: #606266; font-size: 13px; margin-top: 4px; line-height: 1.6; }

.stack-col { padding-right: 8px; }
.stack-title { font-weight: 600; margin-bottom: 6px; }
.stack-list, .vendor-list, .sysinfo { padding-left: 18px; line-height: 26px; margin: 0; }

.quick-links .el-button { margin: 4px 6px 0 0; }
.quick-hint { color: #909399; font-size: 12px; margin-top: 6px; }
.section-subtitle { margin-top: 8px; color: #909399; font-weight: 600; }
.vendor-pending { color: #909399; }

</style>
