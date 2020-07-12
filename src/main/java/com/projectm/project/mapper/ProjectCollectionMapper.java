package com.projectm.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.projectm.project.domain.ProjectCollection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
@Repository
@Mapper
public interface ProjectCollectionMapper extends BaseMapper<ProjectCollection> {

    @Select("SELECT * FROM team_project_collection A  WHERE A.project_code = #{projectCode} and A.member_code = #{memberCode}")
    List<Map> selectProjectCollection(String projectCode, String memberCode);

    @Select("SELECT * FROM `team_collection` WHERE `source_code` = #{sourceCode} AND `type` = 'task' AND `member_code` = #{memberCode} LIMIT 1")
    Map selectProCol(String sourceCode,String memberCode);
}



