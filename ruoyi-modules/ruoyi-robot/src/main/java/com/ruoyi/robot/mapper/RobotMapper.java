package com.ruoyi.robot.mapper;

import com.ruoyi.robot.domain.Robot;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface RobotMapper {
    List<Robot> selectRobotList(Robot filter);
    Robot selectRobotById(Long id);
    int insertRobot(Robot robot);
    int updateRobot(Robot robot);
    int deleteRobotById(Long id);
    int deleteRobotByIds(Long[] ids);
}
