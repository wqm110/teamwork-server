package com.projectm.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.projectm.task.domain.TaskWorkTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
@Repository
@Mapper
public interface  TaskWorkTimeMapper  extends BaseMapper<TaskWorkTime> {


    @Select("SELECT * FROM pear_task_work_time WHERE task_code = #{taskCode}")
    List<Map> selectTaskWorkTimeByTaskCode(String taskCode);

    @Select("SELECT * FROM pear_task_work_time WHERE code = #{code}")
    Map selectTaskWorkTimeByCode(String code);

    @Update("DELETE FROM pear_task_work_time WHERE code = #{code}")
    Integer deleteTaskWorkTimeByCode(String code);

}
