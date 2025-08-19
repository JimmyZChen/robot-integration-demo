package com.ruoyi.robot.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 高仙平台 “获取机器人列表” 接口返回 DTO
 */
@Data
public class GsRobotListResp {
    /** 机器人数组 */
    private List<RobotInfo> robots;

    private int page;
    private int pageSize;
    private long total;

    @Data
    public static class RobotInfo {
        /** JSON 字段 "serialNumber" → Java 字段 robotId */
        @JsonProperty("serialNumber")
        private String robotId;
        private String name;
        private String displayName;
        private String modelFamilyCode;
        private String modelTypeCode;
        private boolean online;
        private String softwareVersion;
    }
}
