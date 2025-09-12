package com.ruoyi.robot.mq;

import com.ruoyi.robot.api.mq.TaskMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${robot.mq.exchange:robot.task.topic}")
    private String exchange;
    @Value("${robot.mq.routing-key:robot.task.dispatch}")
    private String routingKey;

    /**
     * 发送任务消息
     */
    @Trace // 生产端本地 span
    public void send(TaskMessage<?> msg) {
        // 可观测标签
        ActiveSpan.tag("mq.exchange", exchange);
        ActiveSpan.tag("mq.routingKey", routingKey);
        ActiveSpan.tag("task.id", msg.getTaskId());
        ActiveSpan.tag("task.type", String.valueOf(msg.getType()));

        // 关联 confirm（需要 yml 开启 publisher-confirm-type: correlated）
        CorrelationData cd = new CorrelationData(msg.getTaskId());

        rabbitTemplate.convertAndSend(exchange, routingKey, msg, m -> {
            // —— 标准头 —— //
            m.getMessageProperties().setMessageId(msg.getTaskId());
            m.getMessageProperties().setTimestamp(new Date());
            m.getMessageProperties().setContentType("application/json");
            m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);

            // —— 业务头（消费者也会兜底从 body 取）—— //
            m.getMessageProperties().setHeader("type", msg.getType());
            m.getMessageProperties().setHeader("requestId", msg.getRequestId());

            return m;
        }, cd);

        log.info("MQ sent: exchange={}, rk={}, taskId={}, type={}",
                exchange, routingKey, msg.getTaskId(), msg.getType());
    }
}
