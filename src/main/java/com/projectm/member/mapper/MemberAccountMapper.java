package com.projectm.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.projectm.member.domain.MemberAccount;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
@Repository
@Mapper
public interface MemberAccountMapper extends BaseMapper<MemberAccount> {

    @Select("SELECT * FROM pear_member_account WHERE organization_code = #{orgCode} ")
    List<Map> getMemberCountByOrgCode(String orgCode);

    @Select("SELECT * FROM pear_member_account A WHERE A.name LIKE CONCAT('%',#{name},'%') AND A.organization_code = #{orgCode}")
    List<Map> getMemberCountByOrgCodeAndMemberName(String orgCode,String name);

    @Select("SELECT * FROM pear_member_account WHERE member_code = #{memberCode} AND organization_code = #{orgCode} LIMIT 1")
    Map selectMemberAccountByMemCodeAndOrgCode(String memberCode,String orgCode);


    @Select("SELECT * FROM pear_member_account WHERE organization_code = #{params.orgCode} AND status = #{params.status} AND department_code LIKE CONCAT('%',#{params.depCode},'%') ORDER BY id ASC")
    IPage<Map> selectMemberAccountByOrgCodeStatusDeptCode(IPage<Map> page, @Param("params") Map params);

    @Select("SELECT * FROM pear_member_account WHERE organization_code = #{params.orgCode} AND status = #{params.status} ORDER BY id ASC")
    IPage<Map> selectMemberAccountByOrgCodeAndStatus(IPage<Map> page, @Param("params") Map params);

    @Select("SELECT * FROM pear_member_account WHERE code = #{memAccCode} ")
    Map selectMemberAccountByCode(String memAccCode);

    @Select("SELECT * FROM pear_member_account WHERE member_code = #{memCode} ")
    Map selectMemberAccountByMemCode(String memCode);

    @Delete("DELETE FROM pear_department_member WHERE account_code = #{accCode} AND organization_code = #{orgCode}")
    Integer deleteDepartmentMemberByAccCodeAndOrgCode(String accCode,String orgCode);


}



