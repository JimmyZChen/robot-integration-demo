package com.ruoyi.robot.service;

import com.ruoyi.robot.domain.Robot;
import java.util.List;

/**
 * 机器人设备 Service 接口
 */
public interface IRobotService {

    List<Robot> selectRobotList(Robot filter);

    Robot selectRobotById(Long id);

    int insertRobot(Robot robot);

    int updateRobot(Robot robot);

    int deleteRobotById(Long id);

    int deleteRobotByIds(Long[] ids);
}
