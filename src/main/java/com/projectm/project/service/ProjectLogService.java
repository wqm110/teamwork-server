package com.projectm.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.projectm.project.domain.ProjectLog;
import com.projectm.project.mapper.ProjectLogMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectLogService   extends ServiceImpl<ProjectLogMapper, ProjectLog> {

    public IPage<Map> getProjectLogByParam(IPage<Map> ipage,Map params){
        return baseMapper.selectProjectLogByParam(ipage,params);
    }
}
