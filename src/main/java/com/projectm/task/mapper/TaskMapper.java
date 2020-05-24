package com.projectm.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.projectm.task.domain.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
@Repository
@Mapper
public interface TaskMapper extends BaseMapper<Task> {

    @Select("SELECT * FROM pear_task WHERE code = #{code} LIMIT 1")
    Map selectTaskByCode(String code);

    @Select("SELECT code,project_code,name,pri,execute_status,description,create_by,create_time,assign_to,deleted,stage_code,task_tag,done,begin_time,end_time,remind_time,pcode,sort,`like`,star,deleted_time,private,id_num,path,schedule,version_code,features_code,work_time FROM pear_task t WHERE pcode = #{params.pcode} AND deleted = #{params.deleted} AND t.stage_code = #{params.stageCode} ORDER BY t.sort ASC,t.id ASC")
    List<Map> selectTaskByParams(@Param("params") Map params);

    @Select("SELECT code,task_code,tag_code,create_time FROM pear_task_to_tag WHERE task_code = #{taskCode} ORDER BY id ASC")
    List<Map> selectTaskToTagByTaskCode(String taskCode);

    @Select("SELECT * FROM pear_task_tag WHERE code = #{code}")
    Map selectTaskTagByCode(String code);

    //HasUnDone 是否有子任务未完成
    @Select("SELECT COUNT(id) AS tp_count FROM pear_task WHERE pcode = #{pcode} AND done = 0 AND deleted = 0")
    Map selectHasUnDone(String pcode);

    //HasComment
    @Select("SELECT COUNT(id) AS tp_count FROM pear_project_log WHERE source_code = #{code} AND type = 'task' AND is_comment = 1")
    Map selectHasComment(String code);

    //HasSource
    @Select("SELECT COUNT(id) AS tp_count FROM pear_source_link WHERE link_code = #{code} AND link_type = 'task'")
    Map selectHasSource(String code);

    //ChildCount0
    @Select("SELECT COUNT(id) AS tp_count FROM pear_task WHERE pcode = #{pcode} AND deleted = 0")
    Map selectChildCount0(String pcode);
    //ChildCount1
    @Select("SELECT COUNT(id) AS tp_count FROM pear_task WHERE pcode = #{pcode} AND deleted = 0 AND done = 1")
    Map selectChildCount1(String pcode);

    //canRead
    @Select("SELECT * FROM pear_task_member WHERE task_code = #{taskCode} AND member_code = #{memberCode}")
    Map selectCanRead(String taskCode,String memberCode);

    //parentDone
    @Select("SELECT done,deleted FROM pear_task WHERE code = #{pcode}")
    Map selectParentDone(String pcode);

    //未完成的任务列表
    @Select("SELECT t.id,t.code,t.project_code,t.name,t.pri,t.execute_status,t.description,t.create_by,t.create_time,t.assign_to,t.deleted,t.stage_code,t.task_tag,t.done,t.begin_time,t.end_time,t.remind_time,t.pcode,t.sort,t.like,t.star,t.deleted_time,t.private,t.id_num,t.path,t.schedule,t.version_code,t.features_code,t.work_time,p.cover,p.access_control_type,p.white_list,p.order,p.template_code,p.organization_code,p.prefix,p.open_prefix,p.archive,p.open_begin_time,p.open_task_private,p.task_board_theme,p.auto_update_schedule FROM pear_task AS t JOIN pear_project AS p ON t.project_code = p.CODE WHERE t.deleted = 0 AND t.done = #{params.done} AND t.assign_to = #{params.memberCode} AND p.deleted = 0 ORDER BY t.id DESC")
    IPage<Map> selectTaskSelfListNoFinish(IPage<Map> page, Map params);
    @Select("SELECT t.id,t.code,t.project_code,t.name,t.pri,t.execute_status,t.description,t.create_by,t.create_time,t.assign_to,t.deleted,t.stage_code,t.task_tag,t.done,t.begin_time,t.end_time,t.remind_time,t.pcode,t.sort,t.like,t.star,t.deleted_time,t.private,t.id_num,t.path,t.schedule,t.version_code,t.features_code,t.work_time,p.cover,p.access_control_type,p.white_list,p.order,p.template_code,p.organization_code,p.prefix,p.open_prefix,p.archive,p.open_begin_time,p.open_task_private,p.task_board_theme,p.auto_update_schedule FROM pear_task AS t JOIN pear_project AS p ON t.project_code = p.CODE WHERE t.deleted = 0 AND t.assign_to = #{params.memberCode} AND p.deleted = 0 ORDER BY t.id DESC")
    IPage<Map> selectTaskSelfListAll(IPage<Map> page, Map params);

    @Update("UPDATE pear_task SET features_code = '' , version_code = '' WHERE features_code = #{featuresCode}")
    Integer updateTaskFeaAndVerByFeaCode(String featuresCode);

    @Update("UPDATE pear_task SET features_code = '' , version_code = '' WHERE version_code = #{versionCode}")
    Integer updateTaskFeaAndVerByVerCode(String versionCode);

    @Select("SELECT * FROM pear_task WHERE version_code = #{params.versionCode} and deleted = #{params.deleted}")
    List<Map> selectTaskListByVersionAndDelete(Map params);
}
