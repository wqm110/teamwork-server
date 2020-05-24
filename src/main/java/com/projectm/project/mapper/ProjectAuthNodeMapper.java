package com.projectm.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.projectm.project.domain.ProjectAuthNode;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
@Repository
@Mapper
public interface ProjectAuthNodeMapper  extends BaseMapper<ProjectAuthNode> {

    Integer delProjectAuthNodeByAuthCodes(Map params);

    @Delete("DELETE FROM pear_project_auth_node WHERE auth = #{authId}")
    Integer deleteProjectAuthNodeByAuth(Integer authId);

    @Select("SELECT * FROM pear_project_auth_node WHERE auth = #{authId}")
    List<Map> selectProjectAuthNodeByAuth(Integer authId);
}
