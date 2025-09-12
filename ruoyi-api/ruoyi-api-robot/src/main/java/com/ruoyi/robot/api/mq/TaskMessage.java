package com.ruoyi.robot.api.mq;  // ← 改这个包名

import lombok.Data;
import java.io.Serializable;
import java.time.Instant;

@Data
public class TaskMessage<T> implements Serializable {
    private String taskId;
    private String requestId;
    private String type;       // e.g. "GS_TEMP_TASK"
    private T payload;
    private Instant createdAt = Instant.now();
}
