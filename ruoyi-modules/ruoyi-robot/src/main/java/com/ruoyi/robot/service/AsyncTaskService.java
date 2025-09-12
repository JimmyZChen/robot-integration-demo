package com.ruoyi.robot.service;

import com.ruoyi.robot.api.mq.TaskMessage;

public interface AsyncTaskService {
    String initTask(String requestId, String type);
    void markSuccess(String taskId, Object result);
    void markFail(String taskId, String errMsg);
    Object query(String taskId);
    boolean tryIdem(String requestId, long ttlSeconds);
}
