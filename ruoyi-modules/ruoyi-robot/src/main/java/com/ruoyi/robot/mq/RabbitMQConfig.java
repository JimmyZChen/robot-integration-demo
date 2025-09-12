package com.ruoyi.robot.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




@Configuration
public class RabbitMQConfig {
    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Value("${robot.mq.exchange:robot.task.topic}")
    private String exchangeName;
    @Value("${robot.mq.queue:robot.task.q}")
    private String queueName;
    @Value("${robot.mq.routing-key:robot.task.dispatch}")
    private String routingKey;

    @Value("${robot.mq.dlx:robot.task.dlx}")
    private String dlxName;
    @Value("${robot.mq.dlq:robot.task.dlq}")
    private String dlqName;
    @Value("${robot.mq.dlrk:#}")
    private String dlRoutingKey;

    /** JSON 序列化 */
    // ① 使用 Spring 管理的 ObjectMapper（已经自动注册了 JavaTimeModule 等）
    // ② 保险起见再显式注册一次并禁用时间戳写法
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(com.fasterxml.jackson.databind.ObjectMapper om) {
        om.findAndRegisterModules(); // 确保支持 java.time.Instant 等
        om.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return new Jackson2JsonMessageConverter(om);
    }


    /** 生产者模板（启用 confirm/return） */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate tpl = new RabbitTemplate(cf);
        tpl.setMessageConverter(converter);
        tpl.setExchange(exchangeName);   // 作为默认交换机（可保留）
        tpl.setMandatory(true);          // return 回调需要 mandatory=true

        // —— 就把这两段加在这里 —— //
        tpl.setConfirmCallback((cd, ack, cause) -> {
            String id = (cd != null ? cd.getId() : null);
            if (ack) {
                log.info("MQ confirm ok, taskId={}", id);
            } else {
                log.error("MQ confirm failed, taskId={}, cause={}", id, cause);
            }
        });

        tpl.setReturnsCallback(r -> {
            log.warn("MQ returned, exch={}, rk={}, replyCode={}, replyText={}, body={}",
                    r.getExchange(), r.getRoutingKey(), r.getReplyCode(), r.getReplyText(),
                    new String(r.getMessage().getBody()));
        });
        // —— 到此为止，下面保持不变 —— //

        return tpl;
    }



    /** 手动 ack 的监听容器工厂 */
    @org.springframework.cloud.context.config.annotation.RefreshScope
    @Bean(name = "mqListenerFactory")
    public SimpleRabbitListenerContainerFactory mqListenerFactory(
            ConnectionFactory cf, Jackson2JsonMessageConverter converter,
            @Value("${spring.rabbitmq.listener.simple.concurrency:1}") int concurrency,
            @Value("${spring.rabbitmq.listener.simple.max-concurrency:1}") int maxConcurrency,
            @Value("${spring.rabbitmq.listener.simple.prefetch:1}") int prefetch) {
        SimpleRabbitListenerContainerFactory f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(cf);
        f.setMessageConverter(converter);
        f.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        f.setConcurrentConsumers(concurrency);
        f.setMaxConcurrentConsumers(maxConcurrency);
        f.setPrefetchCount(prefetch);
        return f;
    }


    /** 声明 Topic 交换机、业务队列、死信交换机&队列 */
    @Bean
    public Declarables declare() {
        // 业务队列绑定死信交换机
        Queue q = QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", dlxName)
                .withArgument("x-dead-letter-routing-key", dlRoutingKey)
                .build();

        TopicExchange bizEx = ExchangeBuilder.topicExchange(exchangeName).durable(true).build();
        Binding bizBind = BindingBuilder.bind(q).to(bizEx).with(routingKey);

        TopicExchange dlx = ExchangeBuilder.topicExchange(dlxName).durable(true).build();
        Queue dlq = QueueBuilder.durable(dlqName).build();
        Binding dlBind = BindingBuilder.bind(dlq).to(dlx).with(dlRoutingKey);

        return new Declarables(bizEx, q, bizBind, dlx, dlq, dlBind);
    }
}
