package com.ruoyi.robot.openapi;

import com.ruoyi.robot.config.GsOpenApiProperties;
import com.ruoyi.robot.api.dto.*;
import com.ruoyi.robot.api.dto.GsTempTaskDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.concurrent.TimeUnit;



@Slf4j
@Service
public class GsOpenApiServiceImpl implements GsOpenApiService {

    private final RestTemplate restTemplate;
    private final GsOpenApiProperties props;

    private final StringRedisTemplate redis;          // <--- 新增
    private static final ObjectMapper OM = new ObjectMapper();

    private String cachedToken;
    private long expiresAtMillis = 0L;


    // 构造器：新增 redis 参数
    public GsOpenApiServiceImpl(RestTemplate restTemplate, GsOpenApiProperties props,
                                StringRedisTemplate redis) {
        this.restTemplate = restTemplate;
        this.props = props;
        this.redis = redis;
    }

    // 统一的缓存 key
    private static String kRobotStatus(String sn) { return "robot:status:" + sn; }
    private static String kRobotList() { return "robot:list"; }

    // JSON 序列化/反序列化
    private String toJson(Object o) {
        try { return OM.writeValueAsString(o); } catch (Exception e) { return "{}"; }
    }
    private <T> T fromJson(String s, Class<T> t) {
        try { return OM.readValue(s, t); } catch (Exception e) { return null; }
    }
    // JSON 反序列化（对泛型友好，如 List<RobotInfo>）
    private <T> T fromJson(String s, TypeReference<T> typeRef) {
        try { return OM.readValue(s, typeRef); } catch (Exception e) { return null; }
    }

    // 统一从缓存读机器人列表
    private List<GsRobotListResp.RobotInfo> readRobotsCacheOrEmpty() {
        String json = redis.opsForValue().get(kRobotList());
        if (json == null) return Collections.emptyList();
        List<GsRobotListResp.RobotInfo> cached =
                fromJson(json, new TypeReference<List<GsRobotListResp.RobotInfo>>() {});
        return (cached != null) ? cached : Collections.emptyList();
    }

    // ====== 统一的缓存 key ======
    private static String kRobotMapList(String sn) { return "robot:map:list:" + sn; }
    private static String kSubareas(String mapId, String sn) { return "robot:map:subareas:" + mapId + ":" + sn; }
    private static String kRobotCmds(String sn, int page, int pageSize) { return "robot:cmds:" + sn + ":" + page + ":" + pageSize; }

    // ====== 从 JSON 读出 Map / List<Map> 的小工具 ======
    private Map<String, Object> fromJsonToMap(String json) {
        try { return OM.readValue(json, new TypeReference<Map<String, Object>>(){}); } catch (Exception e) { return null; }
    }
    private List<Map<String, Object>> fromJsonToListOfMap(String json) {
        try { return OM.readValue(json, new TypeReference<List<Map<String,Object>>>(){}); } catch (Exception e) { return null; }
    }



    //获取OAuth令牌Token
    //1. 方法声明和线程安全
    private synchronized String getToken() {
        //2. 判断本地缓存的 token 是否可用
        long now = System.currentTimeMillis();
        if (cachedToken != null && now < expiresAtMillis - 60_000L) {
            return cachedToken;
        }
        //3. 组装 token 请求
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = new HashMap<>();
        body.put("grant_type",      "urn:gaussian:params:oauth:grant-type:open-access-token");
        body.put("client_id",       props.getClientId());
        body.put("client_secret",   props.getClientSecret());
        body.put("open_access_key", props.getOpenAccessKey());
        HttpEntity<Map<String, String>> req = new HttpEntity<>(body, headers);
        //4. 发送 HTTP POST 请求获取 token
        ResponseEntity<OAuthTokenResp> resp =
                restTemplate.postForEntity(props.getCredentialUrl(), req, OAuthTokenResp.class);
        //5. 校验响应并抛出异常
        OAuthTokenResp data = resp.getBody();
        if (data == null || data.getAccessToken() == null) {
            throw new RuntimeException("获取 Token 失败，HTTP Status=" + resp.getStatusCode());
        }
        //6. 缓存 token 与过期时间
        cachedToken     = data.getAccessToken();
        expiresAtMillis = now + data.getExpiresIn() * 1000;
        return cachedToken;
    }

    /** 获取机器人列表 */
    //1. 方法签名
    @Override
    @SentinelResource(
            value = "gs.listRobots",
            blockHandler = "listRobotsBlockHandler",
            fallback = "listRobotsFallback"
    )
    public List<GsRobotListResp.RobotInfo> listRobots() {

        //1. 拼接请求 URL
        String url = props.getBaseUrl() + "/xxxxx/xxxxx?page=1&pageSize=10";
        log.debug("[listRobots] URL: {}", url);

        //2. 构造 HTTP 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        //3. 构造请求实体对象
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        //4. 发起 HTTP 请求
        ResponseEntity<GsRobotListResp> resp =
                restTemplate.exchange(url, HttpMethod.GET, entity, GsRobotListResp.class);

        //5. 处理响应体
        GsRobotListResp body = resp.getBody();
        log.debug("[listRobots] 高仙返回: {}", body);

        //7. 结果判空和返回
        List<GsRobotListResp.RobotInfo> list = (body != null && body.getRobots() != null)
                ? body.getRobots()
                : Collections.emptyList();


// 5) 成功写缓存（建议短 TTL，避免陈旧）
        try {
            redis.opsForValue().set(kRobotList(), toJson(list), 5, TimeUnit.MINUTES);
        } catch (Exception ignore) {}

        return list;

    }

    // Sentinel 命中（限流/熔断）
    public List<GsRobotListResp.RobotInfo> listRobotsBlockHandler(BlockException ex) {
        log.warn("[listRobots] blocked by sentinel: {}", ex.getClass().getSimpleName());
        return readRobotsCacheOrEmpty();
    }

    // 方法抛异常
    public List<GsRobotListResp.RobotInfo> listRobotsFallback(Throwable ex) {
        log.warn("[listRobots] fallback by throwable: {}", ex.toString());
        return readRobotsCacheOrEmpty();
    }


    //获取机器人状态
    @Override
    @SentinelResource(
            value = "gs.getRobotStatus",
            blockHandler = "getRobotStatusBlockHandler",
            fallback = "getRobotStatusFallback"
    )
    public String getRobotStatus(String robotSn) {
        // 注意：此处使用 “/xx/robots/…/status” 路径，与 tempTask 不同
        String apiUrl = props.getBaseUrl() + "/xxxxx/xxxxxx/xx/xxxx/" + robotSn + "/status";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> resp = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
        String body = resp.getBody();                  // 先存到变量，后续要用

        // 成功写缓存（短 TTL，比如 3 分钟）
        if (body != null) {
            try { redis.opsForValue().set(kRobotStatus(robotSn), body, 3, TimeUnit.MINUTES); } catch (Exception ignore) {}
        }
        return body;
    }

    // Sentinel 命中（限流/熔断）
    public String getRobotStatusBlockHandler(String robotSn, BlockException ex) {
        log.warn("[getRobotStatus] blocked by sentinel: {}, sn={}", ex.getClass().getSimpleName(), robotSn);
        String cached = redis.opsForValue().get(kRobotStatus(robotSn));
        return (cached != null) ? cached : "{\"code\":429,\"msg\":\"限流/熔断，且无缓存\"}";
    }

    // 方法抛异常
    public String getRobotStatusFallback(String robotSn, Throwable ex) {
        log.warn("[getRobotStatus] fallback by throwable: {}, sn={}", ex.toString(), robotSn);
        String cached = redis.opsForValue().get(kRobotStatus(robotSn));
        return (cached != null) ? cached : "{\"code\":503,\"msg\":\"服务异常，且无缓存\"}";
    }


    // 查询地图列表接口
    @Override
    @SentinelResource(
            value = "gs.postListRobotMap",
            blockHandler = "postListRobotMapBlock",
            fallback = "postListRobotMapFallback"
    )
    public Map<String, Object> postListRobotMap(String robotSn) {
        String url = props.getBaseUrl() + "/xxxxx/xx/xxxx/robotMap/list";
        log.debug("[postListRobotMap] URL: {}, robotSn: {}", url, robotSn);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getToken());

        Map<String, String> body = new HashMap<>();
        body.put("robotSn", robotSn);

        HttpEntity<Map<String, String>> req = new HttpEntity<>(body, headers);

        // 用 Map.class 接收原始响应
        ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.POST, req, Map.class);

        log.debug("[postListRobotMap] 新接口返回: {}", resp.getBody());

        Map<String, Object> result = (resp.getBody() != null) ? resp.getBody() : Collections.emptyMap();

        // 缓存 3 分钟
        try { redis.opsForValue().set(kRobotMapList(robotSn), toJson(result), 3, TimeUnit.MINUTES); } catch (Exception ignore) {}
        return result;

    }

    // 被限流/熔断
    public Map<String, Object> postListRobotMapBlock(String robotSn, BlockException ex) {
        log.warn("[postListRobotMap] blocked: {}, sn={}", ex.getClass().getSimpleName(), robotSn);
        String json = redis.opsForValue().get(kRobotMapList(robotSn));
        Map<String, Object> cached = (json != null) ? fromJsonToMap(json) : null;
        return (cached != null) ? cached : Collections.emptyMap();
    }

    // 方法异常
    public Map<String, Object> postListRobotMapFallback(String robotSn, Throwable ex) {
        log.warn("[postListRobotMap] fallback: {}, sn={}", ex.toString(), robotSn);
        String json = redis.opsForValue().get(kRobotMapList(robotSn));
        Map<String, Object> cached = (json != null) ? fromJsonToMap(json) : null;
        return (cached != null) ? cached : Collections.emptyMap();
    }


    //获取地图分区接口
    @Override
    @SentinelResource(
            value = "gs.listSubareas",
            blockHandler = "listSubareasBlock",
            fallback = "listSubareasFallback"
    )
    public List<GxSubAreaDto> listSubareas(String mapId, String robotSn) {
        //1. 构造请求地址与参数
        String url = props.getBaseUrl() + "/xxxx/xx/xxxx/subareas/get";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getToken());

        Map<String, String> body = new HashMap<>();
        body.put("mapId", mapId);
        body.put("robotSn", robotSn);

        //2. 封装请求体并发起 HTTP POST 请求
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);

        //3. 获取 JSON 字符串响应体
        String respJson = resp.getBody();
        log.debug("subareas resp: {}", respJson == null ? "null" : respJson.substring(0, Math.min(512, respJson.length())));

        //4. JSON 反序列化为 Java Bean
        GxSubAreaResp gxResp = JSON.parseObject(respJson, GxSubAreaResp.class);

        //5. 安全判空与数据抽取
        List<GxSubAreaDto> result = new ArrayList<>();
        if (gxResp != null && gxResp.getData() != null && gxResp.getData().getSubareas() != null) {
            List<GxSubAreaResp.Partition> partitions = gxResp.getData().getSubareas().getPartitions();
            if (partitions != null) {
                for (GxSubAreaResp.Partition p : partitions) {
                    GxSubAreaDto dto = new GxSubAreaDto();
                    dto.setId(p.getId());
                    dto.setName(p.getName());
                    result.add(dto);
                }
            }
        }

        // 缓存 10 分钟
        try { redis.opsForValue().set(kSubareas(mapId, robotSn), toJson(result), 10, TimeUnit.MINUTES); } catch (Exception ignore) {}
        return result;
    }

    public List<GxSubAreaDto> listSubareasBlock(String mapId, String robotSn, BlockException ex) {
        log.warn("[listSubareas] blocked: {}, mapId={}, sn={}", ex.getClass().getSimpleName(), mapId, robotSn);
        String json = redis.opsForValue().get(kSubareas(mapId, robotSn));
        List<GxSubAreaDto> cached = (json != null) ? fromJson(json, new TypeReference<List<GxSubAreaDto>>() {}) : null;
        return (cached != null) ? cached : Collections.emptyList();
    }

    public List<GxSubAreaDto> listSubareasFallback(String mapId, String robotSn, Throwable ex) {
        log.warn("[listSubareas] fallback: {}, mapId={}, sn={}", ex.toString(), mapId, robotSn);
        String json = redis.opsForValue().get(kSubareas(mapId, robotSn));
        List<GxSubAreaDto> cached = (json != null) ? fromJson(json, new TypeReference<List<GxSubAreaDto>>() {}) : null;
        return (cached != null) ? cached : Collections.emptyList();
    }


    //创建机器人无站点临时任务
    @Override
    @SentinelResource(
            value = "gs.sendTempTask",
            blockHandler = "sendTempTaskBlock",
            fallback = "sendTempTaskFallback"
    )
    public String sendTempTask(GsTempTaskDto dto) {
        String apiUrl = props.getBaseUrl() + "/xxxxx/xxxxx/xxxxxx/tempTask:send";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 使用已有的 getToken() 方法获取并设置 Bearer Token
        headers.setBearerAuth(getToken());

        HttpEntity<GsTempTaskDto> entity = new HttpEntity<>(dto, headers);
        ResponseEntity<String> resp = restTemplate.postForEntity(apiUrl, entity, String.class);

        // 这里直接返回原始 JSON，调用方可以自行解析 requestId/code 等字段
        return resp.getBody();
    }

    public String sendTempTaskBlock(GsTempTaskDto dto, BlockException ex) {
        log.warn("[sendTempTask] blocked: {}", ex.getClass().getSimpleName());
        return "{\"code\":429,\"msg\":\"限流/熔断，任务未下发\"}";
    }

    public String sendTempTaskFallback(GsTempTaskDto dto, Throwable ex) {
        log.warn("[sendTempTask] fallback: {}", ex.toString());
        return "{\"code\":503,\"msg\":\"服务异常，任务未下发\"}";
    }


    //机器人指令列表
    @Override
    @SentinelResource(
            value = "gs.listRobotCommands",
            blockHandler = "listRobotCommandsBlock",
            fallback = "listRobotCommandsFallback"
    )
    public List<Map<String, Object>> listRobotCommands(String robotSn, int page, int pageSize) {
        String url = props.getBaseUrl()
                + "/xxxxx/xxxx/" + robotSn
                + "/commands?page=" + page + "&pageSize=" + pageSize;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        log.info("高仙ListCommands返回: {}", resp.getBody());

        JSONObject obj = JSON.parseObject(resp.getBody());

        List<Map<String, Object>> list = new ArrayList<>();
        JSONArray arr = null;
        if (obj.containsKey("commands")) {
            arr = obj.getJSONArray("commands");
        } else if (obj.containsKey("data") && obj.getJSONObject("data").containsKey("commands")) {
            arr = obj.getJSONObject("data").getJSONArray("commands");
        }
        if (arr != null) {
            for (int i = 0; i < arr.size(); i++) {
                list.add(arr.getObject(i, Map.class));
            }
        } else {
            // fallback
            list.add(obj);
        }
        // 缓存 2 分钟
        try { redis.opsForValue().set(kRobotCmds(robotSn, page, pageSize), toJson(list), 2, TimeUnit.MINUTES); } catch (Exception ignore) {}
        return list;
    }
    public List<Map<String, Object>> listRobotCommandsBlock(String robotSn, int page, int pageSize, BlockException ex) {
        log.warn("[listRobotCommands] blocked: {}, sn={}, page={}, size={}", ex.getClass().getSimpleName(), robotSn, page, pageSize);
        String json = redis.opsForValue().get(kRobotCmds(robotSn, page, pageSize));
        List<Map<String, Object>> cached = (json != null) ? fromJsonToListOfMap(json) : null;
        return (cached != null) ? cached : Collections.emptyList();
    }

    public List<Map<String, Object>> listRobotCommandsFallback(String robotSn, int page, int pageSize, Throwable ex) {
        log.warn("[listRobotCommands] fallback: {}, sn={}, page={}, size={}", ex.toString(), robotSn, page, pageSize);
        String json = redis.opsForValue().get(kRobotCmds(robotSn, page, pageSize));
        List<Map<String, Object>> cached = (json != null) ? fromJsonToListOfMap(json) : null;
        return (cached != null) ? cached : Collections.emptyList();
    }

}
