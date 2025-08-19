package com.ruoyi.robot.controller.external;

import com.ruoyi.robot.openapi.GsOpenApiService;
import com.ruoyi.common.core.web.domain.AjaxResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.Map;
import com.ruoyi.robot.api.dto.GsTempTaskDto;


@Api(tags = "高仙机器人指令管理")
@RestController
@RequestMapping("/external/xxx/xxxx/command")
public class GsRobotCommandController {
    @Autowired
    private GsOpenApiService gsOpenApiService;

    //列出机器人指令，只限M型号，S型号不行
    @ApiOperation("查询机器人指令列表")
    @GetMapping("/listCommands")
    public AjaxResult listCommands(
            @RequestParam String robotSn,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<Map<String, Object>> list = gsOpenApiService.listRobotCommands(robotSn, page, pageSize);
        return AjaxResult.success(list);
    }

    /**
     * 发送无站点临时清扫任务
     */
    @ApiOperation("发送无站点临时清扫任务")
    @PostMapping("/tempTask")
    public AjaxResult sendTempTask(@RequestBody GsTempTaskDto dto) {
        String resp = gsOpenApiService.sendTempTask(dto);
        return AjaxResult.success(resp);
    }


}
