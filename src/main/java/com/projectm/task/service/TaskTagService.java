package com.projectm.task.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.projectm.task.domain.TaskTag;
import com.projectm.task.mapper.TaskTagMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class  TaskTagService  extends ServiceImpl<TaskTagMapper, TaskTag> {

    public List<Map> getTaskTagByProjectCode(String projectCode){
        return baseMapper.selectTaskTagByProjectCode(projectCode);
    }

    public Map getTaskTagByCode(String code){
        return baseMapper.selectTaskTagByCode(code);
    }

    public  Map getTaskTagByNameAndProjectCode(Map params){
        return  baseMapper.selectTaskTagByNameAndProjectCode(params);
    }

}
