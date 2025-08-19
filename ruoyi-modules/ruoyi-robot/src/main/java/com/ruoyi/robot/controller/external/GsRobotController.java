package com.ruoyi.robot.controller.external;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.robot.api.dto.GsRobotListResp;
import com.ruoyi.robot.openapi.GsOpenApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 高仙机器人相关接口
 */
@Api(tags = "高仙平台机器人对接")
@Slf4j
@RestController
@RequestMapping("/external/xxx")
public class GsRobotController extends BaseController {

    @Autowired
    private GsOpenApiService gsOpenApiService;

    /**
     * 获取高仙平台机器人列表（支持 robotSn 过滤）
     */
    @ApiOperation("获取高仙平台机器人列表")
    @GetMapping("/robots")
    public AjaxResult listRobots(
            @RequestParam(value = "robotSn", required = false) String robotSn) {
        List<GsRobotListResp.RobotInfo> robots = gsOpenApiService.listRobots();
        if (robotSn == null || robotSn.isEmpty()) {
            return AjaxResult.success(robots);
        }
        List<GsRobotListResp.RobotInfo> filtered =
                robots.stream()
                        .filter(r -> robotSn.equals(r.getRobotId()))
                        .collect(Collectors.toList());
        return AjaxResult.success(filtered);
    }

    /**
     * 查询指定机器人当前状态
     */
    @ApiOperation("查询指定机器人当前状态")
    @GetMapping("/xxxxx/{robotSn}")
    public AjaxResult getRobotStatus(@PathVariable("robotSn") String robotSn) {
        String resp = gsOpenApiService.getRobotStatus(robotSn);
        return AjaxResult.success(resp);
    }
}
