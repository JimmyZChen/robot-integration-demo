package com.ruoyi.robot.controller;

import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.robot.mq.SmokeProducer;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;   // <— 用请求对象取头，避免“未知 HTTP 标头”规约提示
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/external/gs/async")
public class MQSmokeController {

    private final SmokeProducer producer;

    public MQSmokeController(SmokeProducer producer) {
        this.producer = producer;
    }

    /** 压测专用：仅入 MQ，不触发任何业务/实机逻辑 */
    @PostMapping("/_mqSmoke")
    public AjaxResult smoke(@RequestBody Map<String, Object> body,
                            HttpServletRequest request) {

        // 规避“未知 HTTP 标头”提示：从 request 中读取
        String dry = request.getHeader("X-Dry-Run");
        String rid = request.getHeader("X-Request-Id");

        boolean dryRunReq = "true".equalsIgnoreCase(dry);
        String requestId = StringUtils.hasText(rid) ? rid : ("rq-" + UUID.randomUUID());

        String taskId = producer.send(body, requestId, dryRunReq);

        // 兼容 Java 8：不用 Map.of(...)
        Map<String, Object> data = new HashMap<String, Object>(2);
        data.put("taskId", taskId);
        data.put("dryRun", dryRunReq);

        return AjaxResult.success(data);
    }
}
