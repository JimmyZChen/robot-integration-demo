package com.ruoyi.robot.api.dto;

import lombok.Data;
import java.util.List;

/**
 * 无站点临时任务请求体
 */
@Data
public class GsTempTaskDto {
    /** 机器人型号（等同于 productId） */
    private String productId;
    /** 临时任务命令体 */
    private TempTaskCommand tempTaskCommand;

    @Data
    public static class TempTaskCommand {
        private String taskName;
        private String cleaningMode;
        private boolean loop;
        private Integer loopCount;
        private String mapName;
        private List<StartParam> startParam;
    }

    @Data
    public static class StartParam {
        private String mapId;
        private String areaId;
    }
}
