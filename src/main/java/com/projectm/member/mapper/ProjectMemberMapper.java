package com.projectm.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.projectm.member.domain.ProjectMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
@SuppressWarnings("rawtypes")
@Repository
@Mapper
public interface ProjectMemberMapper  extends BaseMapper<ProjectMember> {

    List<Map> selectMemberByLoginParam(@Param("params") Map params);

    List<Map> selectMemberCountByMemberCode(@Param("params") Map params);

    List<Map> getMemberById(String userCode);

    @Select("SELECT * FROM pear_project_member A  WHERE A.is_owner = 1 and A.project_code = #{projectCode}")
    Map getProjectMemberByProjectCodeOwner(String projectCode);

    @Select("SELECT * FROM pear_project_member A  WHERE A.project_code = #{projectCode}")
    IPage<Map> getProjectMemberByProjectCode(IPage<Map> page, String projectCode);

    @Select("SELECT * FROM pear_project_member A  WHERE A.project_code = #{projectCode} AND A.member_code = #{memberCode}")
    List<Map> getProjectMemberByProjectCodeAndMemberCode(String projectCode, String memberCode);

    @Select("SELECT member_code, name FROM pear_project_member pm LEFT JOIN pear_member m ON pm.member_code = m.code WHERE pm.project_code = #{projectCode} AND is_owner = 1 LIMIT 1")
    Map selectMemberCodeAndNameByProjectCode(String projectCode);


}



