package com.projectm.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.projectm.project.domain.ProjectLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Map;

@SuppressWarnings("rawtypes")
@Repository
@Mapper
public interface ProjectLogMapper  extends BaseMapper<ProjectLog> {

    @Select("SELECT * FROM pear_project_log WHERE source_code = #{params.sourceCode} AND action_type = #{params.actionType}")
    IPage<Map> selectProjectLogByParam(IPage<Map> iPage, @Param("params") Map params);

}
