package com.ruoyi.robot.api.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 高仙 OAuth Token 响应 DTO
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OAuthTokenResp {
    /** 令牌类型，一般为 bearer */
    private String tokenType;
    /** 实际用于授权的访问令牌 */
    private String accessToken;
    /** 过期时间，毫秒时间戳 */
    private long expiresIn;
    /** 刷新令牌，如需刷新功能可使用 */
    private String refreshToken;
}
