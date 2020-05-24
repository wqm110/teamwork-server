package com.projectm.org.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.projectm.org.domain.Organization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Map;

@SuppressWarnings("rawtypes")
@Repository
@Mapper
public interface OrganizationMapper extends BaseMapper<Organization> {

    @Select("SELECT B.*FROM pear_member_account A, pear_organization B WHERE A.organization_code = B.code AND A.member_code=#{memberCode}")
    IPage<Map> getAllOrganizationByMemberCode(IPage<Map> page, String memberCode);

}
