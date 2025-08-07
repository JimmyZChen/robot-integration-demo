import request from '@/utils/request'

// 1. 获取机器人列表（GET，robotSn 可选，不填查全部）
export function getRobotList(params = {}) {
  return request({
    url: '/external/gs/robots',
    method: 'get',
    params // 如需筛选 robotSn，可传 { robotSn: 'xxx' }
  })
}

// 2. 获取地图列表（POST，robotSn 必填）
export function getMapList(robotSn) {
  return request({
    url: '/external/gs/map/robotMap/list',
    method: 'post',
    data: { robotSn } // 必须传 { robotSn: 'xxx' }
  })
}

// 3. 获取分区列表（POST，mapId、robotSn 必填）
export function getSubareaList(mapId, robotSn) {
  return request({
    url: '/external/gs/map/subareas',
    method: 'post',
    data: { mapId, robotSn } // 必须传 { mapId: 'xxx', robotSn: 'xxx' }
  })
}

// 4. 查询机器人状态
// src/api/gsrobot.js
/**
 * 获取机器人状态
 * RuoYi 的 request 拦截器会直接返回 response.data，
 * 也就是 { code:200, msg:'...', data: '<jsonString>' }
 */
export function getRobotStatus(robotSn) {
  return request({
    url: `/external/gs/status/${robotSn}`,
    method: 'get'
  }).then(wrapper => {
    // wrapper 形如 { code:200, msg:"{…}", … }
    const text = wrapper.msg || '{}';
    try {
      return JSON.parse(text);
    } catch (e) {
      console.error('解析机器人状态失败', e, text);
      return {};
    }
  });
}


// 5. 创建无站点临时任务
export function sendTempTask(payload) {
  return request({
    url: '/external/gs/robot/command/tempTask',
    method: 'post',
    data: {
      productId: payload.productId,
      tempTaskCommand: {
        taskName:     payload.taskName,
        cleaningMode: payload.cleaningMode,
        loop:         payload.loop,
        loopCount:    payload.loopCount,
        mapName:      payload.mapName,
        startParam:   payload.startParam
      }
    }
  })
    .then(wrapper => {
      console.log('sendTempTask raw wrapper:', wrapper);
      // 这里 wrapper = { code:200, msg:'{ "productId":"...", "requestId":"..." }' }
      if (!wrapper || typeof wrapper.msg !== 'string') {
        throw new Error('返回值格式错误：没有 msg 字符串');
      }
      try {
        const parsed = JSON.parse(wrapper.msg);
        console.log('sendTempTask parsed:', parsed);
        return parsed;
      } catch (e) {
        throw new Error('解析 msg 失败：' + e.message);
      }
    });
}




