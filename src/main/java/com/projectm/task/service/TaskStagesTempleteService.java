package com.projectm.task.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.projectm.task.domain.TaskStagesTemplete;
import com.projectm.task.mapper.TaskStagesTempleteMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStagesTempleteService  extends ServiceImpl<TaskStagesTempleteMapper, TaskStagesTemplete> {

    //根据项目模板编号，查询该模板下的taskstagestemplete
    public List<Integer> selectIdsByProjectTempleteCode(String projectTempleteCode){
        return baseMapper.selectIdsByProjectTempleteCode(projectTempleteCode);
    }

}
