package com.ruoyi.robot.controller.robot;

import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.robot.domain.Robot;
import com.ruoyi.robot.service.IRobotService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;


//增删查改，暂时用不到
/**
 * 机器人管理 Controller
 */
@Api(tags = "机器人管理")
@RestController
@RequestMapping("/robot")
public class RobotController extends BaseController {

    private final IRobotService robotService;

    public RobotController(IRobotService robotService) {
        this.robotService = robotService;
    }

    /**
     * 查询机器人列表
     */
    @ApiOperation("查询机器人列表")
    @GetMapping("/list")
    public TableDataInfo list(Robot filter) {
        startPage();
        List<Robot> list = robotService.selectRobotList(filter);
        return getDataTable(list);
    }

    /**
     * 获取机器人详细信息
     */
    @ApiOperation("获取机器人详细信息")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return AjaxResult.success(robotService.selectRobotById(id));
    }

    /**
     * 新增机器人
     */
    @ApiOperation("新增机器人")
    @Log(title = "机器人", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Robot robot) {
        return toAjax(robotService.insertRobot(robot));
    }

    /**
     * 修改机器人
     */
    @ApiOperation("修改机器人")
    @Log(title = "机器人", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Robot robot) {
        return toAjax(robotService.updateRobot(robot));
    }

    /**
     * 删除机器人
     */
    @ApiOperation("删除机器人")
    @Log(title = "机器人", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(robotService.deleteRobotByIds(ids));
    }
}
