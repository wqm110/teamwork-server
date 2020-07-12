package com.projectm.task.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.projectm.task.domain.TaskWorkflow;
import com.projectm.task.mapper.TaskWorkflowMapper;
import com.projectm.task.mapper.TaskWorkflowRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TaskWorkflowService   extends ServiceImpl<TaskWorkflowMapper, TaskWorkflow> {

    @Autowired
    private TaskWorkflowRuleMapper taskWorkflowRuleMapper;
    //根据 项目编号查询taskWorkflow
    public List<Map> selectTaskWorkflowByProjectCode(String projectCode){
        return baseMapper.selectTaskWorkflowByProjectCode(projectCode);
    }

    //根据 workflow编号查询taskWorkflowrule
    public List<Map> selectTaskWorkflowRuleByWorkflowCode(String workflowCode){
        return taskWorkflowRuleMapper.selectTaskWorkflowRuleByWorkflowCode(workflowCode);
    }
    //根据 workflow编号删除workflow
    public int deleteTaskWorkflowByCode(String workflowCode){
        return baseMapper.deleteTaskWorkflowByCode(workflowCode);
    }



}
