package com.projectm.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.projectm.project.domain.Project;
import com.projectm.project.mapper.ProjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProjectService extends ServiceImpl<ProjectMapper, Project>{


    public List<String> selectProAuthNode(List<String> authorizeids) {
        return baseMapper.selectProAuthNode(authorizeids);
    }

    public List<Map> selectOrgByMemCode(Map params) {
        return baseMapper.selectOrgByMemCode(params);
    }

    public List<Map> selectDepByMemCode(Map params) {
        return baseMapper.selectDepByMemCode(params);
    }

    public IPage<Map> getProjectInfoByMemCodeOrgCode(IPage<Map> page,Map params){
        return baseMapper.getProjectInfoByMemCodeOrgCode(page,params);
    }
    public IPage<Map> getProjectInfoByMemCodeOrgCodeCollection(IPage<Map> page,Map params){
        return baseMapper.getProjectInfoByMemCodeOrgCodeCollection(page,params);
    }

    public IPage<Map> getLogBySelfProject(IPage<Map> page,Map params){
        return baseMapper.selectLogBySelfProject(page,params);
    }

    //根据templateCode获取任务模板名称
    public List<String> getTaskStageTempNameByTemplateCode(String templateCode){
        return baseMapper.getTaskStageTempNameByTemplateCode(templateCode);
    }

    public Map saveProject(Project project){
        save(project);
        return baseMapper.getProjectById(project.getId());
    }

    //根据projectCode获取project
    public Map getProjectByCode(String projectCode){
        Map project = baseMapper.getProjectByCode(projectCode);
        project.put("private",project.get("privated"));
        return project;
    }
    //更新归档标识
    public  int updateArctiveByCode(String projectCode,Integer archive,String archiveTime){
        return baseMapper.updateArctiveByCode(projectCode,archive,archiveTime);
    }
    //更新逻辑删除标识（回收站）
    public  int updateRecycleByCode(String projectCode,Integer deleted,String deletedTime){
        return baseMapper.updateRecycleByCode(projectCode,deleted,deletedTime);
    }

    public IPage<Map> selfProjectList(IPage<Map> page,Map params){
        return baseMapper.selfProjectList(page,params);
    }

    public IPage<Map> getMemberProjects(IPage<Map> page,Map params){
        return baseMapper.selectMemberProjects(page,params);
    }
}
