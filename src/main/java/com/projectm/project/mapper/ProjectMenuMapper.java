package com.projectm.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.projectm.project.domain.ProjectMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
@Repository
@Mapper
public interface ProjectMenuMapper  extends BaseMapper<ProjectMenu> {

    @Select("SELECT * FROM pear_project_menu ORDER BY sort ASC,id ASC")
    List<Map> selectAllProjectMenu();

    @Select("SELECT * FROM pear_project_menu WHERE pid = #{pid} ORDER BY sort ASC,id ASC")
    List<Map> selectProjectMenuByPid(Integer pid);

    @Select("SELECT * FROM pear_project_menu WHERE pid != 0 ORDER BY sort ASC,id ASC")
    List<Map> selectAllNotBase();


}
