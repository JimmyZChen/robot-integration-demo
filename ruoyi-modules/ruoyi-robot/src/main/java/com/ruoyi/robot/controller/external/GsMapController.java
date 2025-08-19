package com.ruoyi.robot.controller.external;

import com.ruoyi.robot.openapi.GsOpenApiService;
import com.ruoyi.robot.api.dto.GxSubAreaDto;
import com.ruoyi.common.core.web.domain.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;

/**
 * 地图相关接口：列出楼层 & 点位
 */
@Api(tags = "地图相关接口")
@Slf4j // 新增
@RestController
@RequestMapping("/external/xxxx/xxxx")
public class GsMapController {

    @Autowired
    private GsOpenApiService gsOpenApiService;


    // 地图列表
    @ApiOperation("查询地图列表")
    @PostMapping("/xxxxx/list")
    public AjaxResult postRobotMapList(@RequestBody Map<String, String> param) {
        String robotSn = param.get("robotSn");
        // 调用新 service 方法（需要你在 service 里补充实现）
        Map<String, Object> result = gsOpenApiService.postListRobotMap(robotSn);
        return AjaxResult.success(result);
    }

    //地图分区
    @ApiOperation("查询地图分区")
    @PostMapping("/subareas")
    public AjaxResult listSubareas(@RequestBody Map<String, String> param) {
        String mapId = param.get("mapId");
        String robotSn = param.get("robotSn");
        List<GxSubAreaDto> subareas = gsOpenApiService.listSubareas(mapId, robotSn);
        return AjaxResult.success(subareas);
    }

}
