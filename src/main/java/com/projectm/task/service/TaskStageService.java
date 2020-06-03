package com.projectm.task.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.projectm.task.domain.TaskStage;
import com.projectm.task.mapper.TaskStageMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public class TaskStageService  extends ServiceImpl<TaskStageMapper, TaskStage> {

    //根据 项目编号查询taskStage
    public List<Map> selectTaskStageByProjectCode(String projectCode){
        return baseMapper.selectTaskStageByProjectCode(projectCode);
    }

    //根据 项目编号查询taskStage
    public IPage<TaskStage> selectTaskStageByProjectCode(IPage ipage, Map params){
        return baseMapper.selectTaskStageByProjectCodeForPage(ipage,params);
    }

    public TaskStage getTaskStageByCode(String code){
        LambdaQueryWrapper<TaskStage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskStage::getCode, code);
        return baseMapper.selectOne(queryWrapper);
    }

}
