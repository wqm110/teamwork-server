package com.projectm.project.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.projectm.project.domain.ProjectCollection;
import com.projectm.project.mapper.ProjectCollectionMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectCollectionService extends ServiceImpl<ProjectCollectionMapper, ProjectCollection> {

    //加入收藏
    public ProjectCollection collect(ProjectCollection pc){
        int i = baseMapper.insert(pc);
        return pc;
    }
    //取消收藏
    public int cancel(ProjectCollection pc){
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("member_code",pc.getMember_code());
        updateWrapper.eq("project_code",pc.getProject_code());
        return baseMapper.delete(updateWrapper);
    }

    //根据projectCode和memberCode获取收藏记录
    public Map getProjectCollection(String projectCode, String memberCode){
        LambdaQueryWrapper<ProjectCollection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProjectCollection::getMember_code, memberCode);
        queryWrapper.eq(ProjectCollection::getProject_code, projectCode);
        return baseMapper.getProjectCollection(projectCode,memberCode);
    }
}
