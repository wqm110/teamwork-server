package com.projectm.member.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.projectm.member.domain.MemberAccount;
import com.projectm.member.mapper.MemberAccountMapper;
import com.projectm.project.mapper.ProjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MemberAccountService extends ServiceImpl<MemberAccountMapper,MemberAccount> {

    @Autowired
    private ProjectMapper projectMapper;

    //根据orgCode获取memberCount
    public List<Map> getMemberCountByOrgCode(String orgCode){
        return  baseMapper.getMemberCountByOrgCode(orgCode);
    }
    //根据orgCode和name模糊查询获取memberCount
    public List<Map> getMemberCountByOrgCodeAndMemberName(String orgCode,String name){
        return  baseMapper.getMemberCountByOrgCodeAndMemberName(orgCode,name);
    }

    public  Map getMemberAccountByMemCodeAndOrgCode(String memberCode,String orgCode){
        return baseMapper.selectMemberAccountByMemCodeAndOrgCode(memberCode,orgCode);
    }

    public IPage<Map> getMemberAccountByOrgCodeStatusDeptCode(IPage page, Map params){
        return baseMapper.selectMemberAccountByOrgCodeStatusDeptCode(page,params);
    }

    public IPage<Map> getMemberAccountByOrgCodeAndStatus(IPage page, Map params){
        return baseMapper.selectMemberAccountByOrgCodeAndStatus(page,params);
    }

    public Map getMemberAccountByCode(String code){
        return baseMapper.selectMemberAccountByCode(code);
    }

    @Transactional
    public Integer memberAccountDel(String accountCode,String orgCode){

        Map memAccountMap = baseMapper.selectMemberAccountByCode(accountCode);
        List<Map> listMapProject = projectMapper.selectProjectByOrgCode(orgCode);
        List<String> projectCodes = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(listMapProject)){
            for(Map m:listMapProject){
                projectCodes.add(MapUtils.getString(m,"code"));
            }
        }
        Map params = new HashMap();
        params.put("proCodeList",projectCodes);
        params.put("memCode",MapUtils.getString(memAccountMap,"member_code"));
        Integer delProjectMemberResult = projectMapper.delProjectMember(params);


        Integer delMemberAccountResult = baseMapper.deleteById(MapUtils.getInteger(memAccountMap,"id"));

        Integer delDeptMemberAccountResult=baseMapper.deleteDepartmentMemberByAccCodeAndOrgCode(accountCode,orgCode);

        return delProjectMemberResult+delMemberAccountResult+delDeptMemberAccountResult;

    }

    public Map getMemberAccountByMemCode(String memCode){
        return baseMapper.selectMemberAccountByMemCode(memCode);
    }

}
