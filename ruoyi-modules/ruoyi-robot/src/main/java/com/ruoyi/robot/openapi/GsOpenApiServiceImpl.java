package com.ruoyi.robot.openapi;


import com.ruoyi.framework.config.GsOpenApiProperties;
import com.ruoyi.framework.dto.external.*;
import com.ruoyi.web.service.robot.GsOpenApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.framework.dto.external.GsTempTaskDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


@Slf4j
@Service
public class GsOpenApiServiceImpl implements GsOpenApiService {

    private final RestTemplate restTemplate;
    private final GsOpenApiProperties props;

    private String cachedToken;
    private long expiresAtMillis = 0L;

    public GsOpenApiServiceImpl(RestTemplate restTemplate, GsOpenApiProperties props) {
        this.restTemplate = restTemplate;
        this.props = props;
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
    public List<GsRobotListResp.RobotInfo> listRobots() {

        //2. 拼接请求 URL
        String url = props.getBaseUrl() + "/v1alpha1/robots?page=1&pageSize=10";
        log.debug("[listRobots] URL: {}", url);

        //3. 构造 HTTP 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        //4. 构造请求实体对象
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        //5. 发起 HTTP 请求
        ResponseEntity<GsRobotListResp> resp =
                restTemplate.exchange(url, HttpMethod.GET, entity, GsRobotListResp.class);
        //6. 处理响应体
        GsRobotListResp body = resp.getBody();
        log.debug("[listRobots] 高仙返回: {}", body);

        //7. 结果判空和返回
        return (body != null && body.getRobots() != null)
                ? body.getRobots()
                : Collections.emptyList();
    }


    // 查询地图列表接口
    @Override
    public Map<String, Object> postListRobotMap(String robotSn) {
        String url = props.getBaseUrl() + "/openapi/v1/map/robotMap/list";
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
        return resp.getBody() != null ? resp.getBody() : Collections.emptyMap();
    }

    //获取地图分区接口
    @Override
    public List<GxSubAreaDto> listSubareas(String mapId, String robotSn) {
        //1. 构造请求地址与参数
        String url = "https://openapi.gs-robot.com/openapi/v1/map/subareas/get";
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
        System.out.println("高仙返回原始json：" + respJson);

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

        //6. 返回结果
        return result;
    }

    //创建机器人无站点临时任务
    @Override
    public String sendTempTask(GsTempTaskDto dto) {
        // 基础 URL 来自配置：openapi.gs.base-url=https://openapi.gs-robot.com
        String apiUrl = props.getBaseUrl() + "/openapi/v2alpha1/robotCommand/tempTask:send";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 使用已有的 getToken() 方法获取并设置 Bearer Token
        headers.setBearerAuth(getToken());

        HttpEntity<GsTempTaskDto> entity = new HttpEntity<>(dto, headers);
        ResponseEntity<String> resp = restTemplate.postForEntity(apiUrl, entity, String.class);

        // 这里直接返回原始 JSON，调用方可以自行解析 requestId/code 等字段
        return resp.getBody();
    }

    //获取机器人状态
    @Override
    public String getRobotStatus(String robotSn) {
        // 注意：此处使用 “/s/robots/…/status” 路径，与 tempTask 不同
        String apiUrl = props.getBaseUrl() + "/openapi/v2alpha1/s/robots/" + robotSn + "/status";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> resp = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
        return resp.getBody();
    }


    //机器人指令列表
    @Override
    public List<Map<String, Object>> listRobotCommands(String robotSn, int page, int pageSize) {
        String url = props.getBaseUrl()
                + "/v1alpha1/robots/" + robotSn
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
        return list;
    }








}
