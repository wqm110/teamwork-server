package com.projectm.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.projectm.project.domain.ProjectCollection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Map;

@SuppressWarnings("rawtypes")
@Repository
@Mapper
public interface ProjectCollectionMapper extends BaseMapper<ProjectCollection> {

    @Select("SELECT * FROM pear_project_collection A  WHERE A.project_code = #{projectCode} and A.member_code = #{memberCode}")
    Map getProjectCollection(String projectCode,String memberCode);
}



