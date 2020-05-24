package com.projectm.member.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.projectm.member.mapper.ProjectMemberMapper;
import com.projectm.member.domain.ProjectMember;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Service
public class ProjectMemberService extends ServiceImpl<ProjectMemberMapper, ProjectMember> {

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


}
