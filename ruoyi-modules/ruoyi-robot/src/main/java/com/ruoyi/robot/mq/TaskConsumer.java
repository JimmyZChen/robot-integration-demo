package com.ruoyi.robot.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.robot.api.dto.GsTempTaskDto;
import com.ruoyi.robot.api.mq.TaskMessage;
import com.ruoyi.robot.openapi.GsOpenApiService;
import com.ruoyi.robot.service.AsyncTaskService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskConsumer {

    private final AsyncTaskService tasks;
    private final GsOpenApiService gsApi;
    private final ObjectMapper objectMapper; // 将 Map 转成目标 DTO

    /** JMeter压测使用，不用使用就注释掉 */
    @Value("${robot.dry-run:false}")
    private boolean dryRun;

    @Value("${robot.reject-non-test-when-dry-run:false}")
    private boolean rejectNonTest;

    @Value("#{'${robot.test-robot-ids:}'.empty ? null : '${robot.test-robot-ids:}'.split(',')}")
    private java.util.List<String> testIds;


    @Value("${robot.mq.idem-ttl-seconds:3600}")
    private long idemTtlSeconds;

    @Trace // SkyWalking 本地 Span

     /** 正常情况用这个 */
    // @RabbitListener(queues = "${robot.mq.queue:robot.task.q}", containerFactory = "mqListenerFactory")

    /** JMeter压测使用，不用使用就注释掉 */
    @RabbitListener(queues = "${robot.mq.queue:robot.task.q}", containerFactory = "mqListenerFactory",  autoStartup = "${robot.consumer.enabled:true}" )

    
    public void onMessage(@Payload TaskMessage<?> msg,
                          @Header(name = "type", required = false) String headerType,
                          Message raw, Channel channel) throws Exception {

        final long tag = raw.getMessageProperties().getDeliveryTag();
        final String taskId = msg.getTaskId();

        /** JMeter压测使用，不用使用就注释掉 */
        if (dryRun) {
            log.info("DRY_RUN: just ack msg, no real dispatch.");
            // ✅ 这里用 raw，别写成 message
            channel.basicAck(raw.getMessageProperties().getDeliveryTag(), false);
            return;
        }


        try {
            // 1) 统一计算任务类型：header 优先，消息体兜底
            final String effectiveType = (headerType != null && !headerType.isEmpty())
                    ? headerType
                    : (msg.getType() == null ? "" : msg.getType());

            // —— 可观测 tag —— //
            ActiveSpan.tag("mq.queue", raw.getMessageProperties().getConsumerQueue());
            ActiveSpan.tag("mq.routingKey", raw.getMessageProperties().getReceivedRoutingKey());
            ActiveSpan.tag("task.id", taskId);
            ActiveSpan.tag("task.type", effectiveType);

            // 2) 幂等：同一 requestId 仅处理一次
            String reqId = msg.getRequestId();
            if (reqId != null) {
                boolean first = tasks.tryIdem(reqId, idemTtlSeconds);
                ActiveSpan.tag("idem.dup", String.valueOf(!first));
                if (!first) {
                    log.info("Duplicate requestId={}, skip. task={}", reqId, taskId);
                    channel.basicAck(tag, false);
                    return;
                }
            }

            // 3) 烟囱测试：ECHO（不依赖高仙接口）
            if ("ECHO".equalsIgnoreCase(effectiveType)) {
                tasks.markSuccess(taskId, msg.getPayload()); // 把原样 payload 作为结果
                channel.basicAck(tag, false);
                return;
            }

            // 4) 正式分发：高仙无站点临时任务
            if ("GS_TEMP_TASK".equalsIgnoreCase(effectiveType)) {
                GsTempTaskDto dto = convert(msg.getPayload(), GsTempTaskDto.class);
                String res = gsApi.sendTempTask(dto);
                tasks.markSuccess(taskId, res);
            } else {
                tasks.markFail(taskId, "Unknown task type: " + effectiveType);
            }

            channel.basicAck(tag, false);          // 成功
        } catch (Exception ex) {
            ActiveSpan.error(ex);
            log.error("MQ consume failed, taskId={}", taskId, ex);
            channel.basicNack(tag, false, false);  // 失败：不重回队列（进 DLQ）
        }
    }

    /** 将 payload（可能是 LinkedHashMap）安全转为目标 DTO */
    private <T> T convert(Object payload, Class<T> clazz) {
        if (payload == null) return null;
        if (clazz.isInstance(payload)) return clazz.cast(payload);
        return objectMapper.convertValue(payload, clazz);
    }
}
