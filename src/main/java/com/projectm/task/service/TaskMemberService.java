package com.projectm.task.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.projectm.task.domain.TaskMember;
import com.projectm.task.mapper.TaskMemberMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TaskMemberService  extends ServiceImpl<TaskMemberMapper, TaskMember> {

    public IPage<Map> getTaskMemberByTaskCode(IPage iPage,String taskCode){
        return baseMapper.selectTaskMemberByTaskCode(iPage,taskCode);
    }
}
