package com.ruoyi.robot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

/** RestTemplate 超时配置，避免外呼长时间阻塞 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(2000);   // 2s 连接超时
        f.setReadTimeout(3000);      // 3s 读取超时
        return new RestTemplate(f);
    }
}
