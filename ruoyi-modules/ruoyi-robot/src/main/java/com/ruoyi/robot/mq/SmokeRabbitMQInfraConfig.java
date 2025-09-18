package com.ruoyi.robot.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 仅为压测链路补“基础设施”，不覆盖你已有配置：
 * - 队列/交换机/绑定声明（若你已有别的声明，RabbitAdmin 会幂等处理）；
 * - JSON 转换器（若你已有，则不创建）；
 * - 手动ACK工厂（若你已有名为 manualAckContainerFactory 的，则不创建）。
 */
@Configuration
public class SmokeRabbitMQInfraConfig {

    // 从 Nacos ruoyi-robot-mq.yml 读取（带默认值，避免空指针）
    @Value("${mq.task.exchange:robot.tempTask.x}")
    private String taskExchange;

    @Value("${mq.task.routing-key:robot.tempTask.rk}")
    private String taskRoutingKey;

    @Value("${mq.task.queue:robot.tempTask.q}")
    private String taskQueue;

    @Value("${mq.task.dlx:robot.dlx.x}")
    private String dlx;

    @Value("${mq.task.dlq:robot.tempTask.dlq}")
    private String dlq;

    /** 若工程中还没有 MessageConverter，则补一个 JSON 转换器 */
    @Bean
    @ConditionalOnMissingBean(MessageConverter.class)
    public MessageConverter smokeMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 声明 Exchange/Queue/Binding + DLX/DLQ（Bean 名不同于你原来的，避免冲突）
     * 注意：RabbitAdmin 对相同资源的声明是幂等的，若已存在会跳过。
     */
    @Bean
    public Declarables smokeTaskInfraDeclarables() {
        DirectExchange x = new DirectExchange(taskExchange, true, false);
        DirectExchange deadX = new DirectExchange(dlx, true, false);
        Queue q = QueueBuilder.durable(taskQueue)
                .withArgument("x-dead-letter-exchange", dlx)
                .withArgument("x-dead-letter-routing-key", dlq)
                .build();
        Queue deadQ = QueueBuilder.durable(dlq).build();
        return new Declarables(
                x, deadX, q, deadQ,
                BindingBuilder.bind(q).to(x).with(taskRoutingKey),
                BindingBuilder.bind(deadQ).to(deadX).with(dlq)
        );
    }

    /** 若你已有同名工厂就不创建；没有则提供一个基础版手动 ACK 工厂 */
    @Bean(name = "manualAckContainerFactory")
    @ConditionalOnMissingBean(name = "manualAckContainerFactory")
    public SimpleRabbitListenerContainerFactory manualAckContainerFactory(
            ConnectionFactory cf, MessageConverter mc) {
        SimpleRabbitListenerContainerFactory f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(cf);
        f.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        f.setMessageConverter(mc);
        return f;
    }
}
