package com.ruoyi.robot.openapi;


import com.ruoyi.framework.dto.external.GsRobotListResp;
import com.ruoyi.framework.dto.external.GxSubAreaDto;
import com.ruoyi.framework.dto.external.GsTempTaskDto;
import java.util.List;
import java.util.Map;

/**
 * 高仙开放平台 API 调用
 */
public interface GsOpenApiService {

    /** 查询机器人列表 */
    List<GsRobotListResp.RobotInfo> listRobots();

    //查询地图列表（原始返回），data字段下是地图信息数组
    Map<String, Object> postListRobotMap(String robotSn);

    // 根据 mapId 查询所有地图分区（subareas）
    List<GxSubAreaDto> listSubareas(String mapId, String robotSn);

    //列出机器人指令
    List<Map<String, Object>> listRobotCommands(String robotSn, int page, int pageSize);

    //创建无站点临时任务
    String sendTempTask(GsTempTaskDto dto);

    //查询机器人状态
    String getRobotStatus(String robotSn);

}
