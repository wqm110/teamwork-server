package com.projectm.org.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.projectm.org.domain.Organization;
import com.projectm.org.mapper.OrganizationMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OrganizationService   extends ServiceImpl<OrganizationMapper, Organization> {

    //获取用户所在的所有组织信息
    public IPage<Map> getAllOrganizationByMemberCode(IPage<Map> page, String memberCode){
        return baseMapper.getAllOrganizationByMemberCode(page,memberCode);
    }

    //根据orgCode获取organization信息
    public Organization getOrganizationByCode(String orgCode){
        LambdaQueryWrapper<Organization> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Organization::getCode, orgCode);
        return baseMapper.selectOne(queryWrapper);
    }

}
