package com.ruoyi.robot.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 表示一张地图/楼层
 */
public class GsMapDto {
    @JsonProperty("mapId")
    private String mapId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("floorId")
    private String floorId;

    public String getMapId() {
        return mapId;
    }
    public void setMapId(String mapId) {
        this.mapId = mapId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getFloorId() {
        return floorId;
    }
    public void setFloorId(String floorId) {
        this.floorId = floorId;
    }
}
