package com.projectm.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.projectm.project.domain.ProjectAuth;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
@Repository
@Mapper
public interface ProjectAuthMapper  extends BaseMapper<ProjectAuth> {

    @Select("SELECT * FROM team_project_auth WHERE status = #{status} AND organization_code = #{orgCode}")
    List<Map> selectProjectAuthByStatusAndOrgCode(String status, String orgCode);

    @Select("SELECT * FROM team_project_auth WHERE organization_code = #{orgCode} ORDER BY id DESC ")
    IPage<Map> selectProjectAuthByOrgCode(IPage<Map> ipage, String orgCode);

    @Update("UPDATE team_project_auth SET is_default = #{isDefault} WHERE organization_code = #{orgCode}")
    Integer updateProjectAuthIsDefaultByOrgCode(Integer isDefault,String orgCode);
}
