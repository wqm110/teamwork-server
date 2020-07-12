package com.projectm.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.projectm.project.domain.ProjectVersion;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
@Repository
@Mapper
public interface ProjectVersionMapper  extends BaseMapper<ProjectVersion> {
    @Select("SELECT * FROM team_project_version WHERE features_code = #{featuresCode} ORDER BY id ASC")
    List<Map> selectProjectVersionByFeaturesCode(String featuresCode);

    @Select("SELECT * FROM team_project_version WHERE code = #{code} ")
    Map selectProjectVersionByCode(String code);

    @Select("SELECT * FROM team_project_version WHERE name = #{name} AND features_code = #{featuresCode} LIMIT 1")
    Map selectProjectVersionByNameAndFeaturesCode(String name,String featuresCode);

    @Delete("DELETE FROM team_project_version WHERE code = #{code}")
    Integer deleteProjectVersionByCode(String code);

}
