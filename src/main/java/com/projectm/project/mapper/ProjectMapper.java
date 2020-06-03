package com.projectm.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.projectm.project.domain.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.Map;
@SuppressWarnings("rawtypes")
@Repository
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {


    List<String> selectProAuthNode(List<String> authorizeids);

    List<Map> selectOrgByMemCode(@Param("params") Map params);

    List<Map> selectDepByMemCode(@Param("params") Map params);

    IPage<Map> getProjectInfoByMemCodeOrgCode(IPage<Map> page,@Param("params") Map params);

    IPage<Map> getProjectInfoByMemCodeOrgCodeCollection(IPage<Map> page,@Param("params") Map params);


    //根据templateCode获取任务模板名称
    List<String> getTaskStageTempNameByTemplateCode(String templateCode);

    @Select("SELECT * FROM pear_project A  WHERE A.ID = #{id}")
    Map getProjectById(Integer id);

    @Select("SELECT id,cover,name,code,description,access_control_type,white_list, `order`,deleted,template_code,schedule,create_time,organization_code,deleted_time,private,prefix,open_prefix,archive,archive_time,open_begin_time,open_task_private,task_board_theme,begin_time,end_time,auto_update_schedule FROM pear_project WHERE code = #{code}")
    Map selectProjectByCode(String code);

    //更新归档标识
    @Update("UPDATE pear_project SET archive = #{archive} , archive_time = #{archiveTime} WHERE code = #{projectCode}")
    int updateArctiveByCode(String projectCode,Integer archive,String archiveTime);

    //更新逻辑删除（回收站标识）
    @Update("UPDATE pear_project SET deleted = #{deleted} , deleted_time = #{deletedTime} WHERE code = #{projectCode}")
    int updateRecycleByCode(String projectCode,Integer deleted,String deletedTime);

    @Select("SELECT p.*,pm.join_time,pm.is_owner,pm.authorize,pm.member_code,pm.project_code FROM pear_project AS p JOIN pear_project_member AS pm ON p. CODE = pm.project_code LEFT JOIN pear_project_collection AS pc ON p. CODE = pc.project_code WHERE pm.member_code = #{params.memberCode} AND p.organization_code = #{params.orgCode} AND p.deleted = 0 ORDER BY  pc.id DESC, p.id DESC")
    IPage<Map> selfProjectList(IPage<Map> page,@Param("params") Map params);

    @Select("SELECT * FROM pear_project WHERE organization_code = #{orgCode} ")
    List<Map> selectProjectByOrgCode(String orgCode);

    Integer delProjectMember(Map params);

    //@Select("SELECT tl.remark AS remark, tl.content AS content, tl.is_comment AS is_comment, tl.create_time AS create_time,  p. NAME AS project_name, t. NAME AS task_name, t. CODE AS source_code, p. CODE AS project_code, m.avatar AS member_avatar,  m. NAME AS member_name FROM  pear_project_log AS tl JOIN pear_task AS t ON tl.source_code = t.CODE JOIN pear_project AS p ON t.project_code = p.CODE JOIN pear_member AS m ON tl.member_code = m.CODE WHERE tl.action_type = 'task' AND p. CODE IN (SELECT  pp.CODE FROM pear_project AS pp JOIN pear_project_member AS pm  ON pm.project_code = pp.CODE WHERE pp.organization_code = #{params.orgCode} AND (  pm.member_code = #{params.memberCode} ) AND pp.deleted = 0 GROUP BY  pp.code ) AND p.deleted = 0 ORDER BY tl.id DESC")
    IPage<Map> selectLogBySelfProjectByMemberCode(IPage<Map> page, Map params);

    @Select({"<script>",
            "SELECT p.id,p.cover,p.name,p.code,p.description,p.access_control_type,p.white_list,p.order,p.deleted,p.template_code,p.schedule,p.create_time,p.organization_code,p.deleted_time,p.private,p.prefix,p.open_prefix,p.archive,p.archive_time,p.open_begin_time,p.open_task_private,p.task_board_theme,p.begin_time,p.end_time,p.auto_update_schedule,",
            "pm.member_code FROM pear_project AS p JOIN pear_project_member AS pm ON p.CODE = pm.project_code LEFT JOIN pear_project_collection AS pc ON p.CODE = pc.project_code  ",
            " WHERE ",
            " pm.member_code = #{params.memberCode} AND p.organization_code = #{params.orgCode} " ,
            "<if test='params.deleted!=-1 '>",
            "AND p.deleted <![CDATA[ = ]]> #{params.deleted}",
            "</if>",
            "<if test='params.archive!=-1 '>",
            "AND p.archive <![CDATA[ = ]]> #{params.archive}",
            "</if>",
            " ORDER BY pc.id DESC,p.id DESC",
            "</script>"})
    IPage<Map> selectMemberProjects(IPage<Map> page,Map params);

    //@Select("select pp.code from pear_project as pp join pear_project_member as pm on pm.project_code = pp.code where pp.organization_code = #{params.orgCode} and (pm.member_code = #{params.memberCode}) and pp.deleted = 0 group by pp.`code`")
    List<String> selectProjectCodesByMemberAndOrg(@Param("params")Map params);

    IPage<Map> selectTaskLogByProjectCode(IPage<Map> page,List list);

}



