package com.ruoyi.robot.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 高仙机器人控制指令下发响应
 */
@Data
@ApiModel("高仙机器人控制指令响应")
public class GsRobotCommandResp {
    @ApiModelProperty("状态码，200 表示请求成功")
    private Integer code;

    @ApiModelProperty("提示信息，成功一般为“操作成功”")
    private String msg;

    @ApiModelProperty("具体返回数据，根据高仙文档可能为任务 ID 或其他")
    private Object data;
}
