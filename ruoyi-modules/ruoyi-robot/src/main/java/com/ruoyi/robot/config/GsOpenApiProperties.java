package com.ruoyi.robot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 高仙开放平台的配置，通过 application.yml 注入
 */
@Component
@ConfigurationProperties(prefix = "xxxx.xx")
public class GsOpenApiProperties {
    /** OAuth Token 获取地址 */
    private String credentialUrl;
    /** 高仙 OpenAPI URL，比如 https://xxxxxx.xx-xxxx.com */
    private String baseUrl;
    private String clientId;
    private String clientSecret;
    /** 双段格式 open-access-key：AccessKeyID,AccessKeySecret */
    private String openAccessKey;

    public String getCredentialUrl() {
        return credentialUrl;
    }
    public void setCredentialUrl(String credentialUrl) {
        this.credentialUrl = credentialUrl;
    }
    public String getBaseUrl() {
        return baseUrl;
    }
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public String getClientSecret() {
        return clientSecret;
    }
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
    public String getOpenAccessKey() {
        return openAccessKey;
    }
    public void setOpenAccessKey(String openAccessKey) {
        this.openAccessKey = openAccessKey;
    }
}
