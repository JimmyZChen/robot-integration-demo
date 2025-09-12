package com.ruoyi.robot.controller.external;

import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.robot.api.dto.GsTempTaskDto;
import com.ruoyi.robot.mq.TaskProducer;
import com.ruoyi.robot.api.mq.TaskMessage;
import com.ruoyi.robot.service.AsyncTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "异步任务（MQ）")
@RestController
@RequestMapping("/external/gs/async")
@RequiredArgsConstructor
public class RobotAsyncTaskController {

    private final TaskProducer producer;
    private final AsyncTaskService tasks;

    @ApiOperation("无站点临时任务：改异步提交")
    @PostMapping("/robot/command/tempTask")
    public AjaxResult submitTempTask(@RequestBody GsTempTaskDto dto,
                                     HttpServletRequest req) {
        String requestId = req.getHeader("X-Request-Id");
        if (requestId == null || requestId.isEmpty()) {
            requestId = java.util.UUID.randomUUID().toString().replace("-", "");
        }
        String taskId = tasks.initTask(requestId, "GS_TEMP_TASK");

        TaskMessage<GsTempTaskDto> msg = new TaskMessage<>();
        msg.setTaskId(taskId);
        msg.setRequestId(requestId);
        msg.setType("GS_TEMP_TASK");
        msg.setPayload(dto);
        producer.send(msg);

        // 202 Accepted + 任务ID，可前端轮询查询
        return AjaxResult.success()
                .put("status", 202)
                .put("taskId", taskId)
                .put("requestId", requestId);
    }

    @ApiOperation("查询异步任务结果")
    @GetMapping("/tasks/{taskId}")
    public AjaxResult queryTask(@PathVariable String taskId) {
        Object r = tasks.query(taskId);
        return r == null ? AjaxResult.error("NOT_FOUND") : AjaxResult.success(r);
    }
}
