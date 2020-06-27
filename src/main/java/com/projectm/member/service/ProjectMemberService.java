package com.projectm.member.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.projectm.common.DateUtil;
import com.projectm.member.domain.MemberAccount;
import com.projectm.member.mapper.ProjectMemberMapper;
import com.projectm.member.domain.ProjectMember;
import com.projectm.project.domain.Project;
import com.projectm.project.service.ProjectLogService;
import com.projectm.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectMemberService extends ServiceImpl<ProjectMemberMapper, ProjectMember> {

    @Autowired
    MemberAccountService memberAccountService;
    @Autowired
    ProjectService projectService;
    @Autowired
    ProjectLogService projectLogService;

    //查询项目的拥有者  is_owener=1
    public Map getProjectMemberByProjectCode(String projectCode){
        return baseMapper.getProjectMemberByProjectCodeOwner(projectCode);
    }

    public IPage<Map> getProjectMemberByProjectCode(ProjectMember projectMember){
        IPage<Map> ipage = new Page();
        ipage.setCurrent(projectMember.getCurrent());
        ipage.setSize(projectMember.getSize());
        IPage<Map> mapIPage = baseMapper.getProjectMemberByProjectCode(ipage,projectMember.getProject_code());
        return mapIPage;
    }

    //根据项目编号和用户编号确定是否是项目成员
    public boolean isProjectMember(String projectCode, String memberCode){
        List<Map> list = baseMapper.getProjectMemberByProjectCodeAndMemberCode(projectCode,memberCode);
        if(!CollectionUtils.isEmpty(list)){
            return true;
        }
        return false;
    }

    public Map gettMemberCodeAndNameByProjectCode(String projectCode){
        return baseMapper.selectMemberCodeAndNameByProjectCode(projectCode);
    }

    @Transactional
    public ProjectMember inviteMember(String memberCode, Project project, Integer isOwner){
        ProjectMember projectMember = ProjectMember.builder().member_code(memberCode).
                project_code(project.getCode()).is_owner(isOwner).
                join_time(DateUtil.getCurrentDateTime()).build();
        save(projectMember);
        memberAccountService.inviteMember(MemberAccount.builder().member_code(memberCode).
                organization_code(project.getOrganization_code()).build());

        projectLogService.run(new HashMap(){{
            put("member_code",memberCode);
            put("source_code",project.getCode());
            put("type","inviteMember");
            put("to_member_code",memberCode);
            put("is_comment",0);
            put("content","");
        }});
        return projectMember;
    }
    @Transactional
    public Integer removeMember(String memberCode, Project project){
        LambdaQueryWrapper<ProjectMember> lambdaQueryWrapper=new LambdaQueryWrapper<ProjectMember>();
        lambdaQueryWrapper.eq(ProjectMember::getProject_code,project.getCode());
        lambdaQueryWrapper.eq(ProjectMember::getMember_code,memberCode);
        Integer result = baseMapper.delete(lambdaQueryWrapper);
        projectLogService.run(new HashMap(){{
            put("member_code",memberCode);
            put("source_code",project.getCode());
            put("type","removeMember");
            put("to_member_code",memberCode);
            put("is_comment",0);
            put("content","");
        }});
        return result;
    }


}
