package com.projectm.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.projectm.task.domain.TaskStagesTemplete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("rawtypes")
@Repository
@Mapper
public interface TaskStagesTempleteMapper  extends BaseMapper<TaskStagesTemplete> {

    @Select("SELECT * FROM pear_task_stages_template WHERE project_template_code = #{projectTempleteCode} ")
    List<Integer> selectIdsByProjectTempleteCode(String projectTempleteCode);
}
