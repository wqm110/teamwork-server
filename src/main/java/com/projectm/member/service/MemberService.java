package com.projectm.member.service;


import cn.hutool.poi.excel.sax.Excel07SaxReader;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.framework.common.utils.StringUtils;
import com.framework.common.utils.security.Md5Utils;
import com.projectm.common.CommUtils;
import com.projectm.common.ListUtils;
import com.projectm.member.domain.Member;
import com.projectm.member.domain.MemberAccount;
import com.projectm.member.mapper.MemberAccountMapper;
import com.projectm.member.mapper.MemberMapper;
import com.projectm.member.mapper.ProjectMemberMapper;
import com.projectm.org.domain.Department;
import com.projectm.org.domain.Organization;
import com.projectm.org.mapper.OrganizationMapper;
import com.projectm.org.service.DepartmentMemberService;
import com.projectm.org.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class MemberService extends ServiceImpl<MemberMapper, Member> {

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    @Autowired
    private MemberAccountMapper memberAccountMapper;

    @Autowired
    OrganizationMapper organizationMapper;

    public List<Map> selectMemberByLoginParam(Map params) {
        return projectMemberMapper.selectMemberByLoginParam(params);
    }

    public List<Map> selectMemberCountByMemberCode(Map params){
        return projectMemberMapper.selectMemberCountByMemberCode(params);
    }
    //根据用户编号，查询用户信息
    public Map getMemberById(String userCode){
        List<Map> listMap = projectMemberMapper.getMemberById(userCode);
        if(null != listMap && listMap.size() > 0){
            return listMap.get(0);
        }
        return null;
    }

    //根据memberCode获取member信息
    public Member getMemberByCode(String memberCode){
        LambdaQueryWrapper<Member> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Member::getCode, memberCode);
        return baseMapper.selectOne(queryWrapper);
    }

    public  void inviteMemberBatch(String memberCodes,String taskCode){
        boolean isAll = false;
        JSONArray memberCodeArray = JSON.parseArray(memberCodes);
        //List<>

        if(memberCodes.indexOf("all") != -1){

        }
    }

    //根据memberCode获取member信息
    public Map getMemberMapByCode(String memberCode){
        return baseMapper.selectMemberByCode(memberCode);
    }

    @Transactional
    public Integer updateMemberAccountAndMember(MemberAccount ma,Member m){
        Integer i1 = baseMapper.updateById(m);
        Integer i2 = memberAccountMapper.updateById(ma);
        return i1+i2;
    }

    public Member getMemberByName(String account){
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        return baseMapper.selectOne(queryWrapper);
    }

    public List<Organization> getOrgList(String memberCode){
        return organizationMapper.selectOrganizationByMemberCode(memberCode);
    }



    @Autowired
    DepartmentMemberService departmentMemberService;

}
