package com.ruoyi.robot.service.impl;

import com.alibaba.fastjson.JSON;
import com.ruoyi.robot.service.AsyncTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AsyncTaskServiceImpl implements AsyncTaskService {

    private final StringRedisTemplate redis;

    private String keyTask(String taskId) { return "robot:task:" + taskId; }
    private String keyIdem(String reqId) { return "robot:idem:" + reqId; }

    @Override
    public String initTask(String requestId, String type) {
        String taskId = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> v = new HashMap<>();
        v.put("status", "PENDING"); v.put("type", type);
        v.put("requestId", requestId); v.put("result", null);
        redis.opsForValue().set(keyTask(taskId), JSON.toJSONString(v));
        return taskId;
    }

    @Override
    public void markSuccess(String taskId, Object result) {
        Map<String, Object> v = read(taskId);
        v.put("status", "DONE"); v.put("result", result);
        redis.opsForValue().set(keyTask(taskId), JSON.toJSONString(v));
    }

    @Override
    public void markFail(String taskId, String errMsg) {
        Map<String, Object> v = read(taskId);
        v.put("status", "FAILED"); v.put("error", errMsg);
        redis.opsForValue().set(keyTask(taskId), JSON.toJSONString(v));
    }

    @Override
    public Object query(String taskId) {
        String s = redis.opsForValue().get(keyTask(taskId));
        return s == null ? null : JSON.parse(s);
    }

    @Override
    public boolean tryIdem(String requestId, long ttlSeconds) {
        Boolean ok = redis.opsForValue().setIfAbsent(
                keyIdem(requestId), "1", Duration.ofSeconds(ttlSeconds));
        return Boolean.TRUE.equals(ok);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> read(String taskId) {
        String s = redis.opsForValue().get(keyTask(taskId));
        return s == null ? new HashMap<>() : JSON.parseObject(s, Map.class);
    }
}
