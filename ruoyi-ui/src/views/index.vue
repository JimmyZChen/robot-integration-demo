<template>
  <div class="app-container home">
    <el-row :gutter="16">

      <!-- 左侧主列 -->
      <el-col :span="16">
        <!-- 顶部信息条 -->
        <div class="home-hero section">
          <div>
            <div class="home-hero__title">{{ title }}</div>
            <div class="home-hero__desc">
              面向多厂商（宇树、高仙等）的统一机器人接入、编排与调度平台。
              通过适配层屏蔽协议差异，提供任务编排、地图分区、状态监控与可观测性。
            </div>
          </div>
          <div class="home-hero__actions">
            <el-button type="primary" @click="go(ROUTES.robotList)">进入机器人管理</el-button>
            <el-button @click="go(ROUTES.robotList)">查看任务调度</el-button>
          </div>
        </div>

        <!-- 核心能力（3列） -->
        <el-card shadow="never" class="section">
          <div class="section-title">核心能力</div>
          <div class="grid-3">
            <div class="feature" v-for="f in features" :key="f.title" @click="featureJump(f)">
              <div class="feature-title">{{ f.title }}</div>
              <div class="feature-desc">{{ f.desc }}</div>
            </div>
          </div>
        </el-card>

        <!-- 技术栈 -->
        <el-card shadow="never" class="section">
          <div class="section-title">技术栈</div>
          <el-row :gutter="12">
            <el-col :span="12" class="stack-col">
              <div class="stack-title">后端 / 平台</div>
              <ul class="stack-list">
                <li>Spring Boot、Spring MVC、MyBatis、Swagger / OpenAPI</li>
                <li>RuoYi-Cloud、Spring Cloud Gateway、Nacos（注册/配置）</li>
                <li>Sentinel（网关 + 服务内：限流 / 熔断 / 降级，Nacos 规则下发）</li>
                <li>Apache SkyWalking（全链路追踪，日志注入 traceId / spanId）</li>
                <li>MySQL、Redis、RabbitMQ（手动 ack、DLX/DLQ、幂等）</li>
                <li>Docker / Docker Compose、Nginx（静态托管与反代）</li>
              </ul>
            </el-col>

            <el-col :span="12" class="stack-col">
              <div class="stack-title">前端 / 工具</div>
              <ul class="stack-list">
                <li>Vue 2、Vuex、Vue Router、Element UI</li>
                <li>Axios、ECharts、RuoYi-UI 组件与权限体系</li>
                <li>JMeter（压测基线 / 并发验证）、Postman（联调）</li>
                <li>对接：高仙（Gaussian）OpenAPI（状态 / 地图 / 分区 / 任务）</li>
              </ul>
            </el-col>
          </el-row>

          <!-- Tag 墙（可选） -->
          <div class="tech-tags section">
            <el-tag effect="plain">Spring Boot</el-tag>
            <el-tag effect="plain">Gateway</el-tag>
            <el-tag effect="plain">Nacos</el-tag>
            <el-tag effect="plain">Sentinel</el-tag>
            <el-tag effect="plain">SkyWalking</el-tag>
            <el-tag effect="plain">MySQL</el-tag>
            <el-tag effect="plain">Redis</el-tag>
            <el-tag effect="plain">RabbitMQ</el-tag>
            <el-tag effect="plain">Vue</el-tag>
            <el-tag effect="plain">Element UI</el-tag>
            <el-tag effect="plain">Axios</el-tag>
            <el-tag effect="plain">ECharts</el-tag>
            <el-tag effect="plain">Docker</el-tag>
            <el-tag effect="plain">Nginx</el-tag>
            <el-tag effect="plain">JMeter</el-tag>
          </div>
        </el-card>



      </el-col>
      <!-- 右侧侧栏 -->
      <el-col :span="8">

        <!-- 厂商 -->
        <el-card shadow="never" class="section">
          <div class="section-title">已接入厂商</div>
          <div class="vendor-wall vendor-wall--sidebar">
            <div class="vendor" v-for="v in vendorsReady" :key="v.name">
              <img class="logo" :src="v.logo" :alt="v.name" loading="lazy" @error="onLogoErr($event)">
              <span class="name">{{ v.name }}</span>
            </div>
          </div>

          <div class="section-subtitle section">规划接入厂商（完成中）</div>
          <div class="vendor-wall vendor-wall--sidebar">
            <div class="vendor is-planned" v-for="v in vendorsPlanned" :key="v.name">
              <img class="logo" :src="v.logo" :alt="v.name" loading="lazy" @error="onLogoErr($event)">
              <span class="name">{{ v.name }}</span>
            </div>
          </div>
        </el-card>

        <!-- 快捷入口 -->
        <el-card shadow="never" class="section">
          <div class="section-title">快捷入口</div>
          <div class="home-quick">
            <div class="quick-item quick-item--primary" @click="go(ROUTES.robotList)">
              <i class="quick-icon el-icon-s-platform"></i>
              <div>
                <div class="quick-title">机器人列表</div>
                <div class="quick-desc">统一管理、状态一览</div>
              </div>
            </div>
            <div class="quick-item" @click="go(ROUTES.robotList)">
              <i class="quick-icon el-icon-location"></i>
              <div>
                <div class="quick-title">地图 / 分区</div>
                <div class="quick-desc">地标与区域维护</div>
              </div>
            </div>

            <!-- “园区驾驶舱” -->
            <div class="quick-item" @click="openCockpit">
              <i class="quick-icon el-icon-data-analysis"></i>
              <div>
                <div class="quick-title">园区驾驶舱</div>
                <div class="quick-desc">大屏看板 / 运营总览</div>
              </div>
            </div>

            <div class="quick-item" @click="openServices">
              <i class="quick-icon el-icon-link"></i>
              <div>
                <div class="quick-title">链路追踪</div>
                <div class="quick-desc">SkyWalking 查看</div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 系统信息 -->
        <el-card shadow="never" class="section">
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
// 你的资源目录里目前有这四个 PNG（见截图）
import logoGaussian from '@/assets/vendors/gaussian.png'
import logoUnitree  from '@/assets/vendors/unitree.png'
import logoEcovacs  from '@/assets/vendors/ecovacs.png'
import logoNinebot  from '@/assets/vendors/ninebot.png'
import logoFudan    from '@/assets/vendors/fudanmicro.jpg'

// 如果之后补：yogo.png / fudanmicro.png，再按同样方式 import 即可
// import logoYogo     from '@/assets/vendors/yogo.png'
// import logoFudan    from '@/assets/vendors/fudanmicro.png'

const ROUTES = {
  robotList: '/gsrobot'
}

export default {
  name: 'Home',
  data() {
    return {
      ROUTES,
      title: process.env.VUE_APP_TITLE || '园区机器人调度系统',
      version: '1.0.0',
      buildTime: new Date().toLocaleString(),
      features: [
        { title: '多厂商适配层', desc: '统一封装高仙/宇树等 OpenAPI，抽象设备模型与指令，隐藏协议差异。', jump: 'robot' },
        { title: '任务编排与调度', desc: '临时/周期任务、分区清扫、串并行编排、重试与限流。', jump: 'robot' },
        { title: '地图与定位',   desc: '地图/分区管理、坐标转换、轨迹可视化。',                 jump: 'robot' },
        { title: '网关与安全',   desc: '统一鉴权、签名校验、黑白名单、速率限制与 IP 保护。',      jump: 'robot' },
        { title: '可观测性',     desc: 'SkyWalking 全链路 + 日志关联，异常追踪与指标看板。',      jump: 'robot' },
        { title: '高可用与扩展', desc: 'Sentinel 熔断降级、降载保护，插件式接入新型号。',          jump: 'robot' }
      ],

      // 数据驱动的厂商清单
      vendorsReady: [
        { name: '高仙 Gaussian', logo: logoGaussian }
      ],
      vendorsPlanned: [
        { name: '宇树 Unitree',  logo: logoUnitree  },
        { name: '科沃斯 Ecovacs',  logo: logoEcovacs  },
        { name: '九号 Ninebot',  logo: logoNinebot  },
        { name: '复旦微电子 Fudan Micro', logo: logoFudan }
        // 后续补：{ name: 'YOGO ROBOT', logo: logoYogo },

      ]
    }
  },
  methods: {
    go(path) {
      this.$router.push(path).catch(() => {});
    },
    featureJump(f) {
      if (f.jump === 'robot') this.go(this.ROUTES.robotList)
    },
    openCockpit() {
      // 优先同域 /screen（后端或前端路由都行），新开标签页
      const candidates = [
        `${location.origin}/screen`,
        'http://localhost/screen',
        'http://localhost:8089/screen'
      ]
      window.open(candidates[0], '_blank') ||
      window.open(candidates[1], '_blank') ||
      window.open(candidates[2], '_blank')
    },
    openServices() {
      // 优先按当前主机拼接 8088 端口，其次回退到固定 localhost
      const candidates = [
        `${location.protocol}//${location.hostname}:8088/General-Service/Services`,
        'http://localhost:8088/General-Service/Services'
      ]
      window.open(candidates[0], '_blank') || window.open(candidates[1], '_blank')
    },
    onLogoErr(e) {
      // 没有占位图就简单隐藏坏图，保留文字
      e.target.style.display = 'none'
    }
  }
}
</script>

<!-- 首页专属样式 -->
<style lang="scss" src="@/assets/styles/home-index.scss"></style>
