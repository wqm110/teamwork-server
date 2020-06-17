package com.projectm.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.framework.common.AjaxResult;
import com.framework.common.utils.StringUtils;
import com.projectm.common.CommUtils;
import com.projectm.common.Constant;
import com.projectm.common.DateUtil;
import com.projectm.mapper.CommMapper;
import com.projectm.member.domain.ProjectMember;
import com.projectm.member.mapper.ProjectMemberMapper;
import com.projectm.project.domain.Project;
import com.projectm.project.mapper.ProjectCollectionMapper;
import com.projectm.project.mapper.ProjectMapper;
import com.projectm.task.domain.TaskStage;
import com.projectm.task.domain.TaskStagesTemplete;
import com.projectm.task.mapper.TaskMapper;
import com.projectm.task.mapper.TaskStageMapper;
import com.projectm.task.mapper.TaskStagesTempleteMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oshi.util.StringUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ProjectService extends ServiceImpl<ProjectMapper, Project>{

    @Autowired
    TaskMapper taskMapper;
    @Autowired
    ProjectMapper projectMapper;
    @Autowired
    TaskStagesTempleteMapper taskStagesTempleteMapper;
    @Autowired
    TaskStageMapper taskStageMapper;

    @Autowired
    CommMapper commMapper;
    @Autowired
    ProjectCollectionMapper projectCollectionMapper;

    @Autowired
    ProjectMemberMapper projectMemberMapper;

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
        String projectCode = MapUtils.getString(params,"projectCode");
        if(StringUtils.isEmpty(projectCode)){
            List<String> projectCodes = baseMapper.selectProjectCodesByMemberAndOrg(params);
            if(CollectionUtils.isEmpty(projectCodes))return page;
            page = baseMapper.selectTaskLogByProjectCode(page,projectCodes);
        }else{
            //page = baseMapper.selectLogBySelfProjectByMemberCode(page,params);
            page = baseMapper.selectProjectLogByProjectCode(page,projectCode);

            List<Map> record = page.getRecords();
            List resultRecord = new ArrayList();
            if(Optional.ofNullable(record).isPresent()){
                record.stream().forEach(m->{
                    String action_type = MapUtils.getString(m,"action_type");
                    if("task".equals(action_type)){
                        m.put("sourceInfo",taskMapper.selectTaskByCode(MapUtils.getString(m,"source_code")));
                    }else if("project".equals(action_type)){
                        m.put("sourceInfo",projectMapper.selectProjectByCode(MapUtils.getString(m,"source_code")));
                    }
                    resultRecord.add(m);
                });
            }
            page.setRecords(resultRecord);
        }
        return page;
    }

    public AjaxResult projectIndex(IPage page,Map params){
        String archive = MapUtils.getString(params,"archive",null);
        String type = MapUtils.getString(params,"type",null);
        String recycle = MapUtils.getString(params,"recycle",null);
        String all = MapUtils.getString(params,"all",null);
        String memberCode = MapUtils.getString(params,"memberCode");
        String orgCode = MapUtils.getString(params,"orgCode");
        String sql = null;
        String field = " pp.cover,pp.name,pp.code,pp.description,pp.access_control_type,pp.white_list,pp.order,pp.deleted,pp.template_code,pp.schedule,pp.create_time,pp.organization_code,pp.deleted_time,pp.private, pp.prefix, pp.open_prefix, pp.archive, pp.archive_time, pp.open_begin_time,pp.open_task_private,pp.task_board_theme,pp.begin_time,pp.end_time,pp.auto_update_schedule";
        if("my".equals(type) || "other".equals(type)){
            sql = String.format("select "+field+",pm.id,pm.project_code,pm.member_code from pear_project as pp left join pear_project_member as pm on pm.project_code = pp.code where pp.organization_code = '%s' and (pm.member_code = '%s' or pp.private = 0)",orgCode,memberCode);
        }else{
            sql = String.format("select "+field+" from pear_project as pp left  join pear_project_collection as pc on pc.project_code = pp.code where pp.organization_code = '%s' and pc.member_code = '%s' ",orgCode,memberCode);
        }

        if(!"other".equals(type)){
            sql += " and pp.deleted = 0";
        }
        if(StringUtils.isNotEmpty(archive)){
            sql += " and pp.archive = 1";
        }
        if(StringUtils.isNotEmpty(recycle)){
            sql += " and pp.deleted = 1";
        }
        sql += " group by pp.`code` order by pp.id desc";
        page = commMapper.customQueryItem(page,sql);
        List<Map> list = page.getRecords();
        List<Map> listResult = new ArrayList();
        if(null != list){
            for(Map map:list) {
                map.put("collected",0);
                map.put("owner_name","-");
                List<Map> collects=projectCollectionMapper.selectProjectCollection(MapUtils.getString(map,"code"),memberCode);
                if(CollectionUtils.isNotEmpty(collects)){
                    map.put("collected",1);
                }
                String memberName = projectMemberMapper.selectMemberNameByProjectMember(MapUtils.getString(map,"code"),memberCode);
                if(StringUtils.isNotEmpty(memberName)){
                    map.put("owner_name",memberName);
                    listResult.add(map);
                }
            }
        }
        page.setRecords(listResult);
        IPage finalPage = page;
        return AjaxResult.success(new HashMap(){{
            put("list", finalPage.getRecords());
            put("total", finalPage.getTotal());
            put("page", finalPage.getCurrent());
        }});

    }

    //根据templateCode获取任务模板名称
    public List<String> getTaskStageTempNameByTemplateCode(String templateCode){
        return baseMapper.getTaskStageTempNameByTemplateCode(templateCode);
    }

    @Transactional
    public Map saveProject(Project project,Map memberMap){
        QueryWrapper<TaskStagesTemplete> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_template_code", project.getTemplate_code());
        List<TaskStagesTemplete> tsts = taskStagesTempleteMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(tsts)){
            tsts = Constant.getDefaultTaskStageTemplate();
        }
        AtomicInteger i= new AtomicInteger(0);
        tsts.stream().forEach(t->{
            TaskStage taskStage = new TaskStage();
            taskStage.setCode(CommUtils.getUUID());
            taskStage.setProject_code(project.getCode());
            taskStage.setName(t.getName());
            taskStage.setCreate_time(DateUtil.formatDateTime(new Date()));
            taskStage.setSort(i.get());
            taskStageMapper.insert(taskStage);
            i.set(i.get() + 1);
        });
        ProjectMember projectMember = new ProjectMember();
        projectMember.setProject_code(project.getCode());
        projectMember.setIs_owner(1);
        projectMember.setJoin_time(DateUtil.formatDateTime(new Date()));
        projectMember.setMember_code(MapUtils.getString(memberMap,"memberCode"));
        projectMemberMapper.insert(projectMember);
        save(project);
        return baseMapper.getProjectById(project.getId());
    }

    public Project getProjectByCodeNotDel(String code){
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Project::getCode, code);
        queryWrapper.eq(Project::getDeleted, 0);
        return baseMapper.selectOne(queryWrapper);
    }
    public Project getProjectProjectByCode(String code){
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Project::getCode, code);
        return baseMapper.selectOne(queryWrapper);
    }

    //根据projectCode获取project
    public Map getProjectByCode(String projectCode){
        Map project = baseMapper.selectProjectByCode(projectCode);
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
