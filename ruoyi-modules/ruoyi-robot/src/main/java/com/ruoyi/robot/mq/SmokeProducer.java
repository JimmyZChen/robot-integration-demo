package com.ruoyi.robot.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
public class SmokeProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public SmokeProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Value("${mq.task.exchange:robot.tempTask.x}")
    private String taskExchange;

    @Value("${mq.task.routing-key:robot.tempTask.rk}")
    private String taskRoutingKey;

    @Value("${mq.task.queue:robot.tempTask.q}")
    private String taskQueue;

    public String send(Object payload, String requestId, boolean dryRun) {
        String taskId = UUID.randomUUID().toString();

        // 手动转 JSON 字符串 ——> RabbitTemplate 只发送 String（默认转换器也OK）
        String json;
        try {
            json = (payload instanceof String) ? (String) payload : objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("payload JSON encode error", e);
        }

        MessagePostProcessor headers = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message msg) {
                msg.getMessageProperties().setContentType("application/json");
                msg.getMessageProperties().setContentEncoding(StandardCharsets.UTF_8.name());
                msg.getMessageProperties().setHeader("x-dry-run", dryRun);
                msg.getMessageProperties().setHeader("x-task-id", taskId);
                if (StringUtils.hasText(requestId)) {
                    msg.getMessageProperties().setCorrelationId(requestId);
                }
                return msg;
            }
        };

        log.info("[SMOKE] send to MQ cfg: exchange={}, routingKey={}, queue={}, requestId={}, dryRun={}",
                taskExchange, taskRoutingKey, taskQueue, requestId, dryRun);

        if (StringUtils.hasText(taskExchange) && StringUtils.hasText(taskRoutingKey)) {
            rabbitTemplate.convertAndSend(taskExchange, taskRoutingKey, json, headers);
        } else if (StringUtils.hasText(taskQueue)) {
            rabbitTemplate.convertAndSend("", taskQueue, json, headers);
        } else {
            throw new IllegalStateException("MQ routing not configured: set mq.task.exchange/routing-key or mq.task.queue");
        }

        return taskId;
    }
}
