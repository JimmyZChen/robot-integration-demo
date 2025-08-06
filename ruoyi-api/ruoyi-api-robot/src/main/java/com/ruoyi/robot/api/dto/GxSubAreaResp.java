package com.ruoyi.robot.api.dto;

import java.util.List;

public class GxSubAreaResp {
    private int code;
    private String msg;
    private Data data;

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }

    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }

    // 内部类，对应data字段
    public static class Data {
        private String mapId;
        private Subareas subareas;

        public String getMapId() { return mapId; }
        public void setMapId(String mapId) { this.mapId = mapId; }

        public Subareas getSubareas() { return subareas; }
        public void setSubareas(Subareas subareas) { this.subareas = subareas; }
    }

    // 内部类，对应subareas字段
    public static class Subareas {
        private List<Partition> partitions;

        public List<Partition> getPartitions() { return partitions; }
        public void setPartitions(List<Partition> partitions) { this.partitions = partitions; }
    }

    // 内部类，对应分区
    public static class Partition {
        private int id;
        private String name;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
