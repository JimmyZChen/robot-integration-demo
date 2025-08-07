package com.ruoyi.robot.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 机器人设备对象 robot_device
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("robot_device")
public class Robot extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 设备名称 */
    private String name;

    /** 设备类型 */
    private String type;

    /** 运行状态 */
    private String status;

    /** 最后活跃时间 */
    @TableField("last_active_time")
    private LocalDateTime lastActiveTime;
}
