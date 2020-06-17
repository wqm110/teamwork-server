package com.projectm.project.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.projectm.project.domain.ProjectNode;
import com.projectm.project.mapper.ProjectNodeMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProjectNodeService extends ServiceImpl<ProjectNodeMapper, ProjectNode> {

    public List<Map> getProjectNodeByNodeLike(String node){

        return baseMapper.selectProjectNodeByNodeLike(node);
    }

    public List<Map> getAllProjectNode(){
        return baseMapper.selectAllProjectNode();
    }

    public void get(String module){
        if("project".equals(module)){

        }
    }
}
