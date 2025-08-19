package com.ruoyi.robot.service.impl;

import com.ruoyi.robot.domain.Robot;
import com.ruoyi.robot.mapper.RobotMapper;
import com.ruoyi.robot.service.IRobotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 机器人设备 Service 实现
 */
@Service
public class RobotServiceImpl implements IRobotService {

    @Autowired
    private RobotMapper robotMapper;

    @Override
    public List<Robot> selectRobotList(Robot filter) {
        return robotMapper.selectRobotList(filter);
    }

    @Override
    public Robot selectRobotById(Long id) {
        return robotMapper.selectRobotById(id);
    }

    @Override
    public int insertRobot(Robot robot) {
        return robotMapper.insertRobot(robot);
    }

    @Override
    public int updateRobot(Robot robot) {
        return robotMapper.updateRobot(robot);
    }

    @Override
    public int deleteRobotById(Long id) {
        return robotMapper.deleteRobotById(id);
    }

    @Override
    public int deleteRobotByIds(Long[] ids) {
        return robotMapper.deleteRobotByIds(ids);
    }
}
