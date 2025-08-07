<template>
  <div class="main-wrap">
    <!-- 机器人列表 -->
    <el-card>
      <template #header>
        <div style="display:flex; justify-content: space-between; align-items: center;">
          <div style="display:flex; align-items:center;">
            <i class="el-icon-s-custom" style="font-size: 20px; color: #409EFF; margin-right:8px;"></i>
            <span class="card-title">机器人列表</span>
          </div>

          <!-- 新增/替换部分 START -->
          <el-button type="primary" icon="el-icon-refresh" size="medium" @click="refreshAll">刷新</el-button>
          <!-- 新增/替换部分 END -->

        </div>
      </template>
      <el-table
        :data="robotList"
        style="width: 100%;"
        v-loading="loading"
        @row-click="handleRobotRowClick"
        highlight-current-row
      >
        <el-table-column prop="serialNumber" label="ID" width="180" />
        <el-table-column prop="displayName" label="名称" />

        <!-- 新增/替换部分 START -->
        <el-table-column prop="online" label="状态" width="100">
          <template v-slot="scope">
            <div class="status-flex">
              <span class="dot" :style="{ background: scope.row.online ? '#67C23A' : '#aaa' }"></span>
              <span :style="{ color: scope.row.online ? '#67C23A' : '#aaa', fontWeight: 'bold' }">
        {{ scope.row.online ? '在线' : '离线' }}
      </span>
            </div>
          </template>
        </el-table-column>

        <!-- 新增/替换部分 END -->


      </el-table>
      <!-- 新增/替换部分 START -->
      <div
        v-if="!loading && !robotList.length"
        class="empty-hint"
      >
        <i class="el-icon-document" style="font-size: 32px; color: #ccc; display:block; margin-bottom:8px;" />
        暂无数据
      </div>
      <!-- 新增/替换部分 END -->

    </el-card>

    <!-- 地图列表 -->
    <el-card style="margin-top: 32px;">
      <template #header>
        <div style="display:flex; align-items:center;">
          <i class="el-icon-map-location" style="font-size: 20px; color: #67C23A; margin-right:8px;"></i>
          <span class="card-title">地图列表</span>
          <span v-if="selectedRobot" style="margin-left: 24px; color: #888;">
            当前机器人：{{ selectedRobot.displayName || selectedRobot.serialNumber }}
          </span>
        </div>
      </template>

      <!-- 未选机器人时只显示提示 -->
      <div v-if="!selectedRobot" style="padding: 32px 0; color: #aaa; text-align:center;">
        请先选择机器人查看地图列表
      </div>

      <!-- 选中机器人后正常显示表格和数据 -->
      <div v-else>
        <el-table
          :data="mapList"
          style="width: 100%;"
          v-loading="mapLoading"
        >
          <el-table-column
            prop="mapId"
            label="MapID"
            width="360"
            show-overflow-tooltip
          />
          <el-table-column prop="mapName" label="地图名称" />
          <el-table-column label="查看分区" width="120">
            <template v-slot="scope">
              <el-button type="text" @click="handleViewSubarea(scope.row)">
                查看分区
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 新增/替换部分 START -->
        <div
          v-if="!loading && !robotList.length"
          class="empty-hint"
        >
          <i class="el-icon-document" style="font-size: 32px; color: #ccc; display:block; margin-bottom:8px;" />
          暂无数据
        </div>
        <!-- 新增/替换部分 END -->

      </div>
    </el-card>

    <!-- 分区列表对话框 -->
    <el-dialog
      :visible.sync="subareaDialogVisible"
      width="600px"
      title="分区列表"
    >
      <el-table :data="subareaList" style="width: 100%;">
        <el-table-column prop="id" label="分区ID" width="80" />
        <el-table-column prop="name" label="分区名称" />
      </el-table>

      <!-- 新增/替换部分 START -->
      <div
        v-if="!loading && !robotList.length"
        class="empty-hint"
      >
        <i class="el-icon-document" style="font-size: 32px; color: #ccc; display:block; margin-bottom:8px;" />
        暂无数据
      </div>
      <!-- 新增/替换部分 END -->

    </el-dialog>

    <!-- 机器人状态 -->
    <el-card style="margin-top: 32px;">
      <template #header>
        <div style="display:flex; align-items:center;">
          <i class="el-icon-info" style="font-size: 20px; color: #E6A23C; margin-right:8px;"></i>
          <span class="card-title">机器人状态</span>
          <el-select
            v-model="selectedRobotSn"
            placeholder="切换机器人"
            @change="onRobotSnChange"
            size="small"
            style="margin-left: 16px; width: 300px;"
          >
            <el-option
              v-for="r in robotList"
              :key="r.serialNumber"
              :label="r.displayName || r.serialNumber"
              :value="r.serialNumber"
            />
          </el-select>
        </div>
      </template>
      <el-row :gutter="20" style="margin-top: 16px;">
        <el-col :span="4">
          <!-- 任务状态显示处 -->
          <div class="robot-status-line">
            <strong>任务状态：</strong>
            <el-tag
              class="status-tag"
              :type="statusTagType(statusSummary.taskState)"
              size="mini"
              effect="plain"
            >
              {{ selectedRobot ? taskStateText(statusSummary.taskState) : '-' }}
            </el-tag>
          </div>


        </el-col>

        <el-col :span="4">
          <div>
            <strong>电量：</strong>
            {{ selectedRobot ? (statusSummary.powerPercentage !== null && statusSummary.powerPercentage !== undefined ? statusSummary.powerPercentage : '-') : '-' }}%
          </div>
        </el-col>
        <el-col :span="6">
          <div>
            <strong>地图：</strong>
            {{ selectedRobot ? (statusSummary.mapName || '-') : '-' }}
          </div>
        </el-col>
        <el-col :span="6">
          <div>
            <strong>位置：</strong>
            x={{ selectedRobot ? (statusSummary.x !== null && statusSummary.x !== undefined ? statusSummary.x : '-') : '-' }},
            y={{ selectedRobot ? (statusSummary.y !== null && statusSummary.y !== undefined ? statusSummary.y : '-') : '-' }}
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 创建无站点临时清扫任务 -->
    <el-card style="margin-top: 32px;">
      <template #header>
        <div style="display:flex; align-items:center;">
          <i class="el-icon-brush" style="font-size: 20px; color: #409EFF; margin-right:8px;"></i>
          <span class="card-title">创建无站点临时清扫任务</span>
        </div>
      </template>
      <el-form :model="tempTaskForm" label-width="100px" inline>
        <el-form-item label="任务名称">
          <el-input
            v-model="tempTaskForm.taskName"
            placeholder="输入任务名称"
          />
        </el-form-item>
        <el-form-item label="地图">
          <el-select
            v-model="selectedMap"
            placeholder="请选择地图"
            @change="handleMapChange"
            filterable
          >
            <el-option
              v-for="map in mapList"
              :key="map.mapId"
              :label="map.mapName"
              :value="map"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="分区">
          <el-select
            v-model="selectedAreaId"
            placeholder="请选择分区"
            filterable
          >
            <el-option
              v-for="sub in subareaList"
              :key="sub.id"
              :label="sub.name"
              :value="sub.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="循环">
          <el-switch v-model="tempTaskForm.loop" />
        </el-form-item>
        <el-form-item v-if="tempTaskForm.loop" label="次数">
          <el-input-number v-model="tempTaskForm.loopCount" :min="1" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitTempTask"
                     :loading="submitLoading"
          >
            创建任务
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>


<script>

import {
  getRobotList,
  getMapList,
  getSubareaList,
  getRobotStatus,
  sendTempTask
} from '@/api/gsrobot'
import { debounce } from 'lodash';


export default {
  name: 'GsRobotList',
  data() {
    return {
      robotList: [],
      selectedRobotSn: '',
      selectedRobot: null,
      loading: false,
      mapList: [],
      mapLoading: false,
      subareaDialogVisible: false,
      subareaList: [],
      statusSummary: {
        taskState: '',
        powerPercentage: null,
        mapName: '',
        x: null,
        y: null
      },
      tempTaskForm: {
        productId: '',
        taskName: '',
        cleaningMode: '清扫',
        loop: true,
        loopCount: 1,
        mapName: '',
        startParam: []
      },
      selectedMap: null,
      selectedAreaId: null,
      refreshTimer: null,
      mapRefreshTimer: null,
      errorMsgHandler: null,
      robotStatusLoading: false, // 请求锁
      submitLoading: false,

    }
  },
  created() {
    this.refreshAll();
    this.startAutoRefresh();
    this.startMapAutoRefresh();
    // 用 throttle 包 showError，每1秒只允许弹一次
    this.showError = throttle(this._showError, 1000, { trailing: false });
  },
  beforeDestroy() {
    this.stopAutoRefresh();
    this.stopMapAutoRefresh();
  },
  methods: {

    async refreshAll() {
      this.loading = true;
      try {
        const res = await getRobotList();
        this.loading = false;
        this.robotList = Array.isArray(res.data) ? res.data : [];
        if (this.robotList.length) {
          this.selectedRobotSn = '';
          this.selectedRobot = null;
          this.selectedMap = null;
          this.selectedAreaId = null;
          this.subareaList = [];
          this.statusSummary = {
            taskState: '',
            powerPercentage: null,
            mapName: '',
            x: null,
            y: null
          };
          await this.fetchMapList();


        } else {
          this.selectedRobotSn = '';
          this.selectedRobot = null;
          this.mapList = [];
          this.selectedMap = null;
          this.selectedAreaId = null;
          this.subareaList = [];
          this.statusSummary = {
            taskState: '',
            powerPercentage: null,
            mapName: '',
            x: null,
            y: null
          }
        }
      } catch (e) {
        this.loading = false;
      }
    },




    // 加载地图
    // ① 地图刷新方法
    async fetchMapList() {
      if (!this.selectedRobot) {
        this.mapList = [];
        return;
      }
      this.mapLoading = true;
      try {
        const res = await getMapList(this.selectedRobot.serialNumber);
        this.mapLoading = false;
        const arr = res.data && res.data.data ? res.data.data : [];
        this.mapList = Array.isArray(arr) ? arr : [];
      } catch (e) {
        this.mapLoading = false;
      }
    },

    startMapAutoRefresh() {
      this.stopMapAutoRefresh();
      this.refreshTimer = setInterval(() => {
        if (this.selectedRobot && this.selectedRobotSn) {
          this.fetchRobotStatus();
        }
      }, 10000);
      // 10秒自动刷新地图
    },
    stopMapAutoRefresh() {
      if (this.mapRefreshTimer) {
        clearInterval(this.mapRefreshTimer);
        this.mapRefreshTimer = null;
      }
    },


    async onRobotSnChange(sn) {
      const robot = this.robotList.find(r => r.serialNumber === sn)
      if (!robot) return
      this.selectedRobot = robot
      await this.fetchMapList()
      this.fetchRobotStatus(); // 不要用防抖/节流
    },


    showError(msg) {
      if (this.errorMsgHandler) {
        // 关闭上一个
        this.errorMsgHandler.close && this.errorMsgHandler.close();
        this.errorMsgHandler = null;
      }
      this.errorMsgHandler = this.$message.error({
        message: msg,
        duration: 2000,
        onClose: () => { this.errorMsgHandler = null }
      });
    },

    sortPartitions(partitions) {
      return partitions.slice().sort((a, b) => {
        const aMatch = a.name.match(/^办公(\d+)/);
        const bMatch = b.name.match(/^办公(\d+)/);

        if (aMatch && bMatch) {
          return parseInt(aMatch[1]) - parseInt(bMatch[1]);
        } else if (aMatch) {
          return 1; // a是办公，b不是，b排前面
        } else if (bMatch) {
          return -1; // b是办公，a不是，a排前面
        } else {
          return 0; // 都不是办公，保持原顺序（或者用 a.name.localeCompare(b.name) 排名字序）
        }
      });
    },



    // 查看分区
    handleViewSubarea(row) {
      if (!this.selectedRobot) {
        this.$message.warning('请先选择机器人')
        return
      }
      this.subareaDialogVisible = true

      getSubareaList(row.mapId, this.selectedRobot.serialNumber)
        .then(res => {
          const rawList = Array.isArray(res.data) ? res.data : [];
          this.subareaList = this.sortPartitions(rawList);
        })
        .catch(() => {
          this.subareaList = []
        })
    },

    // Ajax实现“异步获取数据”,不刷新页面拉取状态
    async fetchRobotStatus() {
      // 如果正在请求，直接弹一次，return，不再继续下发请求
      if (this.robotStatusLoading) {

        return;
      }
      this.robotStatusLoading = true;
      try {
        const data = await getRobotStatus(this.selectedRobot.serialNumber);

        // ... 处理 data ...
        this.statusSummary = {
          taskState: data.taskState || '—',
          powerPercentage: data.battery?.powerPercentage ?? '—',
          mapName: data.localizationInfo?.map?.name || '—',
          x: data.localizationInfo?.mapPosition?.x ?? '—',
          y: data.localizationInfo?.mapPosition?.y ?? '—'
        }
      } catch (e) {
        this.$message.error('获取机器人状态失败')
        this.statusSummary = {taskState: '—', powerPercentage: '—', mapName: '—', x: '—', y: '—'}
      }
      this.robotStatusLoading = false;
    },

    taskStateText(state) {
      switch(state) {
        case 'IDLE': return '空闲';
        case 'RUNNING': return '进行中';
        case 'PAUSED': return '暂停';
        case 'ERROR': return '异常';
        default: return state || '-';
      }
    },
    statusTagType(state) {
      switch(state) {
        case 'IDLE': return 'success';   // 绿色
        case 'RUNNING': return 'warning';// 橙色
        case 'PAUSED': return 'info';    // 蓝灰色
        case 'ERROR': return 'danger';   // 红色
        default: return 'info';
      }
    },

    // 点击表格行也切换
    handleRobotRowClick(row) {
      this.selectedRobotSn = row.serialNumber
      this.onRobotSnChange(row.serialNumber)
    },

    // 地图下拉 → 拉分区
    handleMapChange(map) {
      this.selectedMap = map
      this.tempTaskForm.mapName = map.mapName
      this.tempTaskForm.startParam = []
      this.selectedAreaId = null

      getSubareaList(map.mapId, this.selectedRobot.serialNumber)
        .then(res => {
          const rawList = Array.isArray(res.data) ? res.data : [];
          this.subareaList = this.sortPartitions(rawList);
        })
        .catch(() => {
          this.subareaList = []
        })
    },

    submitTempTask() {
      if(this.submitLoading) return;     // 防止多次点击
      this.submitLoading = true;         // 开始 loading

      this.tempTaskForm.productId = this.selectedRobot.serialNumber;
      // 组装 startParam
      if (this.selectedAreaId && this.selectedMap) {
        this.tempTaskForm.startParam = [{
          mapId: this.selectedMap.mapId,
          areaId: this.selectedAreaId
        }];
      }

      sendTempTask(this.tempTaskForm)
        .then(data => {
          if (data && data.requestId) {
            this.$message.success(`任务创建成功，Request ID: ${data.requestId}`);
          } else {
            this.$message.warning('任务已发送，但未返回 requestId');
          }
        })
        .catch(err => {
          console.error('submitTempTask error:', err);
          this.$message.error('任务创建失败: ' + (err.message || err));
        })
        .finally(() => {
          this.submitLoading = false; // 不管成功失败都关闭 loading
        });
    },


    startAutoRefresh() {
      this.refreshTimer = setInterval(() => {
        if (this.selectedRobot) {
          this.fetchRobotStatus()
        }
      }, 10000) // 每10秒刷新一次，可以根据需要改时间
    },

    stopAutoRefresh() {
      if (this.refreshTimer) {
        clearInterval(this.refreshTimer)
        this.refreshTimer = null
      }
    },

  }
}


</script>
<style scoped>
/* 如需再调样式在这里加 */
.main-wrap {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px 0 32px 0;
}
.el-card {
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
  border-radius: 10px;
  border: 1px solid #e3e9ef;
}
.card-title {
  font-size: 18px;
  font-weight: bold;
  color: #222;
}
/* 新增 START */
.status-flex {
  display: flex;
  align-items: center;
  height: 24px;
}
.dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-right: 6px;
  vertical-align: middle;
}

.robot-status-badge .el-badge__content {
  font-size: 13px;
  margin-left: 0;
  padding: 0 4px;
}
.empty-hint {
  color: #bbb;
  font-size: 18px;
  padding: 40px 0 30px 0;
  text-align: center;
  letter-spacing: 1px;
  min-height: 60px;
}
/* 新增 END */
/* 6. 表单体验优化（建议直接复制到 style 里） */
.el-form--inline .el-form-item {
  margin-right: 32px;    /* 间隔拉大 */
  margin-bottom: 16px;   /* 上下间距更舒服 */
}
.el-form--inline .el-form-item:last-child {
  margin-right: 0;
}
.el-form-item .el-button {
  margin-left: 16px;     /* 按钮右移 */
}
/* 输入框、下拉框宽度变宽 */
.el-input, .el-select {
  min-width: 180px;
  max-width: 260px;
}
/* 任务名称输入框再加大一点（建议加在el-input上） */
.task-name-input {
  min-width: 240px;
  max-width: 350px;
}
/* 7. 响应式体验 */
@media (max-width: 1400px) {
  .main-wrap { max-width: 1000px; }
}
@media (max-width: 1100px) {
  .main-wrap { max-width: 98vw; padding: 10px 0; }
  .el-card { margin: 0 8px 32px 8px; }
}
@media (max-width: 900px) {
  .main-wrap { max-width: 100vw; padding: 4px 0; }
  .el-row, .el-form--inline { flex-direction: column; }
  .el-form-item, .el-form--inline .el-form-item {
    width: 100%;
    margin-right: 0 !important;
  }
}
/* 8. 表格高亮与交互 */
.el-table__body tr:hover > td {
  background: #f6faff !important;
  transition: background 0.2s;
}
.el-table__row.current-row > td {
  animation: row-fadein 0.3s;
}
@keyframes row-fadein {
  0% { background: #e8f4ff; }
  100% { background: #f6faff; }
}
/* 新版，直接替换 style 部分 */
.robot-status-line {
  display: flex;
  align-items: center;
  min-height: 24px;
  font-size: 15px;
}
.status-tag {
  font-size: 16px !important;
  font-weight: bold;
  padding: 0 22px !important;  /* 左右间距再大一些 */
  margin-left: 8px;            /* 跟前面“任务状态”间隔多1-2px */
  height: 32px;
  line-height: 32px;
  display: inline-flex;
  align-items: center;
  justify-content: center;     /* 水平居中内容 */
  box-sizing: border-box;
  /* 不建议加vertical-align: middle，会和flex冲突 */
}

</style>
