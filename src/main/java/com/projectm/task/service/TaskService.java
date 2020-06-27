package com.projectm.task.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.framework.common.AjaxResult;
import com.framework.common.utils.StringUtils;
import com.projectm.common.CommUtils;
import com.projectm.common.DateUtil;
import com.projectm.mapper.CommMapper;
import com.projectm.member.domain.Member;
import com.projectm.member.service.MemberService;
import com.projectm.project.domain.Project;
import com.projectm.project.domain.ProjectLog;
import com.projectm.project.mapper.ProjectMapper;
import com.projectm.project.service.CollectionService;
import com.projectm.project.service.ProjectLogService;
import com.projectm.project.service.ProjectService;
import com.projectm.project.service.ProjectVersionService;
import com.projectm.system.domain.Notify;
import com.projectm.task.domain.Task;
import com.projectm.task.domain.TaskLike;
import com.projectm.task.domain.TaskStage;
import com.projectm.task.domain.TaskToTag;
import com.projectm.task.mapper.TaskLikeMapper;
import com.projectm.task.mapper.TaskMapper;
import com.projectm.task.mapper.TaskTagMapper;
import com.projectm.task.mapper.TaskToTagMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskService   extends ServiceImpl<TaskMapper, Task> {


    @Autowired
    CommMapper commMapper;
    @Autowired
    TaskLikeMapper taskLikeMapper;

    @Autowired
    ProjectLogService projectLogService;

    public Map getTaskMapByCode(String code){
        return  baseMapper.selectTaskByCode(code);
    }
    public Map getTaskByCodeNoDel(String code){
        return  baseMapper.selectTaskByCodeNoDel(code);
    }
    public Task getTaskByCode(String code){
        LambdaQueryWrapper<Task> taskQW = new LambdaQueryWrapper<>();
        taskQW.eq(Task::getCode, code);
        return baseMapper.selectOne(taskQW);
    }
    public List<Map> getTaskByProjectCodeAndDel(String projectCode,Integer deleted){
        LambdaQueryWrapper<Task> taskQW = new LambdaQueryWrapper<>();
        taskQW.eq(Task::getProject_code, projectCode);
        taskQW.eq(Task::getDeleted,deleted);
        return baseMapper.selectTaskByProjectCodeAndDel(projectCode,deleted);
    }

    public List<Map> getTaskByParams(Map params){
        return baseMapper.selectTaskByParams(params);
    }
    public List<Map> selectTaskToTagByTaskCode(String taskCode){
        return baseMapper.selectTaskToTagByTaskCode(taskCode);
    }
    public Map selectTaskTagByCode(String code){
        return baseMapper.selectTaskTagByCode(code);
    }
    public Map selectHasUnDone(String pcode){
        return baseMapper.selectHasUnDone(pcode);
    }
    public Map selectHasComment(String pcode){
        return baseMapper.selectHasComment(pcode);
    }
    public Map selectHasSource(String pcode){
        return baseMapper.selectHasSource(pcode);
    }
    public Map selectChildCount0(String pcode){
        return baseMapper.selectChildCount0(pcode);
    }
    public Map selectChildCount1(String pcode){
        return baseMapper.selectChildCount1(pcode);
    }
    public Map selectCanRead(String taskCode,String memberCode){
        return baseMapper.selectCanRead(taskCode,memberCode);
    }
    public Map selectParentDone(String pcode){
        return baseMapper.selectParentDone(pcode);
    }

    public IPage<Map> getMemberTasks(IPage<Map> ipage, Map params){
        /*if(type == 0){
            return baseMapper.selectTaskSelfListNoFinish(ipage,params);
        }else{
            return baseMapper.selectTaskSelfListAll(ipage,params);
        }*/
        Integer taskType = MapUtils.getInteger(params,"taskType",1);
        Integer done = MapUtils.getInteger(params,"done",0);
        String memberCode = MapUtils.getString(params,"memberCode");
        String doneSql = "";
        if(-1!=done){
            doneSql="and t.done = " + done;
        }
        String sql = null;
        //我执行的
        if(1==taskType){
            sql = "select t.project_code,t.assign_to,t.deleted,t.stage_code,t.task_tag,t.done,t.begin_time,t.end_time,t.remind_time, t.pcode,t.sort,t.`liked`,t.star,t.deleted_time,t.pri,t.private,t.id_num,t.path,t.`schedule`,t.version_code, t.features_code,t.work_time,p.cover,p.access_control_type,p.white_list,p.`order`, p.template_code,p.organization_code,p.prefix,p.open_prefix,p.archive,p.archive_time, p.open_begin_time,p.open_task_private,p.task_board_theme,p.auto_update_schedule, t.create_time,t.create_by,p.description,t.id as id,t.name as name,t.code as code from team_task as t join team_project as p on t.project_code = p.code where  t.deleted = 0 "+doneSql+" and t.assign_to = '"+memberCode+"' and p.deleted = 0 order by t.id desc";
        }
        //我参与的
        if(2==taskType){
            sql = "select t.project_code,t.assign_to,t.deleted,t.stage_code,t.task_tag,t.done,t.begin_time,t.end_time,t.remind_time, t.pcode,t.sort,t.`liked`,t.star,t.deleted_time,t.pri,t.private,t.id_num,t.path,t.`schedule`,t.version_code, t.features_code,t.work_time,p.cover,p.access_control_type,p.white_list,p.`order`, p.template_code,p.organization_code,p.prefix,p.open_prefix,p.archive,p.archive_time, p.open_begin_time,p.open_task_private,p.task_board_theme,p.auto_update_schedule, t.create_time,t.create_by,p.description,t.id as id,t.name as name,t.code as code from team_task as t join team_project as p on t.project_code = p.code left join team_task_member as tm on tm.task_code = t.code where  t.deleted = 0 "+doneSql+" and tm.member_code = '"+memberCode+"' and p.deleted = 0 order by t.id desc";
        }
        //我创建的
        if(3==taskType){
            sql = "select t.project_code,t.assign_to,t.deleted,t.stage_code,t.task_tag,t.done,t.begin_time,t.end_time,t.remind_time, t.pcode,t.sort,t.`liked`,t.star,t.deleted_time,t.pri,t.private,t.id_num,t.path,t.`schedule`,t.version_code, t.features_code,t.work_time,p.cover,p.access_control_type,p.white_list,p.`order`, p.template_code,p.organization_code,p.prefix,p.open_prefix,p.archive,p.archive_time, p.open_begin_time,p.open_task_private,p.task_board_theme,p.auto_update_schedule, t.create_time,t.create_by,p.description,t.id as id,t.name as name,t.code as code from team_task as t join team_project as p on t.project_code = p.code where  t.deleted = 0 "+doneSql+" and t.create_by = '"+memberCode+"' and p.deleted = 0 order by t.id desc";
        }
        return commMapper.customQueryItem(ipage,sql);
    }

    public List<Map> getTaskListByVersionAndDelete(Map params){
        return  baseMapper.selectTaskListByVersionAndDelete(params);
    }

    @Autowired
    private  TaskStageService taskStageService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private MemberService memberService;

    @Transactional
    public AjaxResult createTask(Task task,String pcode){
        TaskStage ts = taskStageService.getTaskStageByCode(task.getStage_code());
        if(ObjectUtils.isEmpty(ts)){
            return AjaxResult.warn("该任务列表无效");
        }
        Project project = projectService.getProjectByCodeNotDel(task.getProject_code());
        if(ObjectUtils.isEmpty(project)){
            return AjaxResult.warn("该项目已失效");
        }

        Member member = memberService.getMemberByCode(task.getAssign_to());
        if(ObjectUtils.isEmpty(member)){
            return AjaxResult.warn("任务执行人有误");
        }
        Map parentTask = null;
        if(StringUtils.isNotEmpty(pcode)){
            parentTask = getTaskMapByCode(pcode);
            if(ObjectUtils.isEmpty(parentTask)){
                return AjaxResult.warn("父目录无效");
            }
            if(MapUtils.getInteger(parentTask,"deleted",-1) == 1){
                return AjaxResult.warn("父任务在回收站中无法编辑");
            }
            if(MapUtils.getInteger(parentTask,"done",-1) == 1){
                return AjaxResult.warn("父任务已完成，无法添加新的子任务");
            }
            task.setProject_code(MapUtils.getString(parentTask,"project_code"));
            task.setStage_code(MapUtils.getString(parentTask,"stage_code"));
            task.setPcode(pcode);
        }

        Integer maxIdNum = baseMapper.selectMaxIdNumByProjectCode(task.getProject_code());
        String path = "";
        if(maxIdNum == null)maxIdNum = 0;
        if(!ObjectUtils.isEmpty(parentTask)){
            String parentPath = MapUtils.getString(parentTask,"path");
            if(StringUtils.isNotEmpty(parentPath)){
                parentPath = ","+parentPath;
            }else{
                parentPath = "";
            }
            path = MapUtils.getString(parentTask,"code")+parentPath;
        }
        task.setCreate_time(DateUtil.getCurrentDateTime());
        task.setCode(CommUtils.getUUID());
        task.setPath(path);
        task.setPri(0);
        if(null == project.getOpen_task_private() || 0 == project.getOpen_task_private()){
            task.setPrivated(0);
        }else{
            task.setPrivated(1);
        }
        task.setId_num(maxIdNum+1);
        int i = baseMapper.insert(task);
        if(i>0){
            Map taskMap = baseMapper.selectTaskByCode(task.getCode());
            return AjaxResult.success(buildTaskMap(taskMap,task.getCreate_by()));
        }
        return AjaxResult.warn("保存失败！");
    }

    protected String getPriTextAttr(String pri){
        Map<String,String> status = new HashMap(){{
           put("0","普通");
           put("1","紧急");
           put("2","非常紧急");
        }};
        if(StringUtils.isEmpty(pri)){
            pri = "0";
        }
        return status.get(pri);
    }
    protected String getStatusTextAttr(String stat){
        Map<String,String> status = new HashMap(){{
            put("0","未开始");
            put("1","已完成");
            put("2","进行中");
            put("3","挂起");
            put("4","测试中");
        }};
        if(StringUtils.isEmpty(stat)){
            stat = "0";
        }
        return status.get(stat);
    }
    @Autowired
    TaskToTagService taskToTagService;
    /**
     * 标签
     */
    @Autowired
    TaskToTagMapper taskToTagMapper;
    @Autowired
    TaskTagMapper taskTagMapper;
    protected  List<Map> getTagsAttr(String taskCode){
        List<Map> tags = new ArrayList();
        List<Map> result = new ArrayList<>();
        if(StringUtils.isNotEmpty(taskCode)){
            tags = taskToTagMapper.selectTaskToTagByTaskCode(taskCode);
            if(CollectionUtils.isNotEmpty(tags)){
                tags.stream().forEach(map -> {
                    Map tag = taskTagMapper.selectTaskTagByCode(MapUtils.getString(map,"tag_code"));
                    map.put("tag",tag);
                    result.add(map);
                });
            }

        }
        return result;
    }
    /**
     * 子任务数
     */
    protected List getChildCountAttr(String taskCode){
        List childTasks = new ArrayList();
        Map childCount0 = selectChildCount0(taskCode);
        Map childCount1 = selectChildCount1(taskCode);
        childTasks.add(childCount0.get("tp_count"));
        childTasks.add(childCount1.get("tp_count"));
        return childTasks;
    }

    public Integer getParentDoneAttr(String code){
        Integer done = 1;
        Map parentDone=baseMapper.selectParentDone(code);
        if(!MapUtils.isEmpty(parentDone) && MapUtils.getInteger(parentDone,"done",0)!=0 && MapUtils.getInteger(parentDone,"deleted",0)!=0){
            done = 0;
        }else{
            done = 1;
        }
        return done;
    }

    public Integer getHasUnDoneAttr(String code){
        Integer hasUnDone = 0;
        Map parentDone = baseMapper.selectHasUnDone(code);
        Integer tp_count = MapUtils.getInteger(parentDone,"tp_count",0);
        if(tp_count>0){
            hasUnDone = 1;
        }else{
            hasUnDone = 0;
        }
        return hasUnDone;
    }

    public Integer getHasCommentAttr(String code){
        Map hasComment = baseMapper.selectHasComment(code);
        if(!MapUtils.isEmpty(hasComment)){
            return MapUtils.getInteger(hasComment,"tp_count");
        }else{
            return 0;
        }
    }

    protected Integer getHasSourceAttr(String code){
        Map hasSource = baseMapper.selectHasSource(code);
        if(!MapUtils.isEmpty(hasSource)){
            return MapUtils.getInteger(hasSource,"tp_count");
        }else{
            return 0;
        }
    }
    protected Integer getCanReadAttr(String taskCode,String memberCode,Integer privated){

        Integer canRead = 1;
        if(null !=privated){
            if(privated > 0){
                Map canReadMap = baseMapper.selectCanRead(taskCode,memberCode);
                if(MapUtils.isNotEmpty(canReadMap)){
                    canRead = 0;
                }
            }
        }
        return canRead;
    }

    protected  Integer getLikedAttr(String code,String memberCode){
        Integer like = 0;
        Map taskLike = baseMapper.selectTaskLike(code,memberCode);
        if(MapUtils.isNotEmpty(taskLike)){
            like = 1;
        }
        return like;
    }

    protected Integer getStaredAttr(String code,String memberCode){
        Integer stared = 0;
        Map taskStar = baseMapper.selectTaskStared(code,memberCode);
        if(MapUtils.isNotEmpty(taskStar)){
            stared = 1;
        }
        return stared;
    }
    public Integer getDateTaskTotalForProject(String projectCode,String beginTime,String endTime){
        return baseMapper.selectDateTaskTotalForProject(projectCode,beginTime,endTime);
    }

    public Map buildTaskMap(Map task,String memberCode){
        String taskCode = MapUtils.getString(task,"code");
        task.put("priText",getPriTextAttr(MapUtils.getString(task,"pri")));
        task.put("statusText",getStatusTextAttr(MapUtils.getString(task,"status")));
        task.put("liked",getLikedAttr(taskCode,memberCode));
        task.put("stared",getStaredAttr(taskCode,memberCode));
        task.put("tags",getTagsAttr(taskCode));
        task.put("childCount",getChildCountAttr(taskCode));
        task.put("hasUnDone",getHasUnDoneAttr(taskCode));
        task.put("parentDone",getParentDoneAttr(taskCode));
        task.put("hasComment",getHasCommentAttr(taskCode));
        task.put("hasSource",getHasSourceAttr(taskCode));
        task.put("canRead",getCanReadAttr(taskCode,memberCode,MapUtils.getInteger(task,"private")));
        return task;
    }

    public Map readTask(String taskCode,String memberCode){
        Map task = baseMapper.selectTaskByCode(taskCode);
        LambdaQueryWrapper<Project> projectQW = new LambdaQueryWrapper<>();
        projectQW.eq(Project::getCode, MapUtils.getString(task,"project_code"));
        Project project = projectService.getBaseMapper().selectOne(projectQW);
        LambdaQueryWrapper<TaskStage> taskStageQW = new LambdaQueryWrapper<>();
        taskStageQW.eq(TaskStage::getCode, MapUtils.getString(task,"stage_code"));
        TaskStage taskStage = taskStageService.getBaseMapper().selectOne(taskStageQW);
        task.put("executor",null);
        if(StringUtils.isNotEmpty(MapUtils.getString(task,"assign_to"))){
            Member member = memberService.getMemberByCode(MapUtils.getString(task,"assign_to"));
            task.put("executor",member);
        }
        if(StringUtils.isNotEmpty(MapUtils.getString(task,"pcode"))){
            Task pTask = baseMapper.selTaskByCode(MapUtils.getString(task,"pcode"));
            task.put("parentTask",pTask);
            List<Map> pathList = new ArrayList<>();
            if(StringUtils.isNotEmpty(MapUtils.getString(task,"path"))){
                String path = MapUtils.getString(task,"path");
                String[] paths = path.split(",");
                for(int i=paths.length-1;i>=0;i--){
                    Task t = baseMapper.selTaskByCode(paths[i]);
                    int finalI = i;
                    pathList.add(new HashMap(){{
                        put("code",paths[finalI]);
                        put("name",t.getName());
                    }});
                }
            }
            task.put("parentTasks",pathList);
        }
        task.put("openBeginTime",project.getOpen_begin_time());
        task.put("projectName",project.getName());
        task.put("stageName",taskStage.getName());
        return buildTaskMap(task,memberCode);
    }

    public IPage<Map> taskIndex(IPage<Map> page,Map param){
        page = baseMapper.selectTaskListByParam(page,param);
        String memberCode = MapUtils.getString(param,"memberCode");
        List<Map> result = new ArrayList<>();
        List<Map> taskList = page.getRecords();
        if(CollectionUtils.isNotEmpty(taskList)){
            taskList.stream().forEach(map -> {
                Member member = memberService.getMemberByCode(MapUtils.getString(map,"assign_to"));
                if(!ObjectUtils.isEmpty(member)){
                    map.put("executor",new HashMap(){{
                        put("name",member.getName());
                        put("avatar",member.getAvatar());
                    }});
                }
                map = buildTaskMap(map,memberCode);
                result.add(map);
            });
        }
        page.setRecords(result);
        return page;
    }

    @Transactional
    public void like(Map taskMap,String memberCode,Integer likeData){
        Integer like = MapUtils.getInteger(taskMap,"like");
        String code = MapUtils.getString(taskMap,"code");
        LambdaUpdateWrapper<TaskLike> taskLikeUW = new LambdaUpdateWrapper<TaskLike>();
        if(0==likeData) {
            like = like-1;
            taskLikeUW.eq(TaskLike::getMember_code,memberCode);
            taskLikeUW.eq(TaskLike::getTask_code,code);
            taskLikeMapper.delete(taskLikeUW);
        }
        if(1==likeData) {
            like = like+1;
            taskLikeMapper.insert(TaskLike.builder().create_time(DateUtil.getCurrentDateTime())
                    .member_code(memberCode).task_code(code).build());
        }
        baseMapper.updateTaskLike(like,code);
    }

    @Transactional
    public void  recycle(String taskCode,String memberCode){
        Task task = getTaskByCode(taskCode);
        task.setDeleted(1);
        task.setDeleted_time(DateUtil.getCurrentDateTime());
        updateById(task);
        taskHook(memberCode,taskCode,"recycle","",0,
                "","","",null,null);
    }

    @Autowired
    CollectionService collectionService;

    @Transactional
    public void star(Map taskMap,String memberCode,Integer starData){
        String code = MapUtils.getString(taskMap,"code");
        Integer star = MapUtils.getInteger(taskMap,"star");
        if(1==starData){
            star = star+1;
        }else {
            star = star-1;
        }
        baseMapper.updateTaskStar(star,code);
        collectionService.starTask(code,memberCode,star);
    }

    @Transactional
    public void edit(Task task,String memberCode){

        String type = null;
        if(StringUtils.isNotEmpty(task.getDescription()) || "<p><br></p>".equals(task.getDescription())){
            task.setDescription("");
            type = "clearContent";
        }
        updateById(task);

        if(StringUtils.isNotEmpty(task.getName())){
            type = "name";
        }
        if(StringUtils.isNotEmpty(task.getDescription())){
            type = "content";
        }
        if(!ObjectUtils.isEmpty(task.getPri())){
            type = "pri";
        }
        if(!ObjectUtils.isEmpty(task.getStatus())){
            type = "status";
        }
        if(StringUtils.isNotEmpty(task.getBegin_time())){
            type = "setBeginTime";
        }
        if("".equals(task.getBegin_time())){
            type = "clearBeginTime";
        }
        if(StringUtils.isNotEmpty(task.getEnd_time())){
            type = "setEndTime";
        }
        if("".equals(task.getEnd_time())){
            type = "clearEndTime";
        }
        if(!ObjectUtils.isEmpty(task.getWork_time()) && task.getWork_time()>0){
            type = "setWorkTime";
        }
        if(StringUtils.isNotEmpty(type)){
            String finalType = type;
            projectLogService.run(new HashMap(){{
                put("member_code",memberCode);
                put("source_code",task.getProject_code());
                put("type", finalType);
                put("is_comment",0);
            }});
        }
    }

    public void taskHook(String memberCode,String taskCode,String type,String toMemberCode,Integer isComment,
                         String remark,String content,String fileCode,Map data,String tag){
        run(new HashMap(){{
            put("memberCode",memberCode);
            put("taskCode",taskCode);
            put("toMemberCode",toMemberCode);
            put("isComment",isComment);
            put("remark",remark);
            put("content",content);
            put("fileCode",fileCode);
            put("type", type);
            put("is_comment",0);
            put("data",data);
        }});
    }

    @Transactional
    public void taskDone(String taskCode,Integer done,String memberCode)throws  Exception{
        Map taskMap = getTaskMapByCode(taskCode);
        taskMap = buildTaskMap(taskMap,memberCode);
        if(MapUtils.isEmpty(taskMap)){
            throw new Exception("任务已失效");
        }
        if(MapUtils.getInteger(taskMap,"deleted",0)>0){
            throw new Exception("任务在回收站中无法进行编辑");
        }
        if(StringUtils.isNotEmpty(MapUtils.getString(taskMap,"pcode")) && MapUtils.getInteger(taskMap,"parentDone",0)>0){
            throw new Exception("父任务已完成，无法重做子任务");
        }
        if(MapUtils.getInteger(taskMap,"hasUnDone",0)>0){
            throw new Exception("子任务尚未全部完成，无法完成父任务");
        }
        LambdaQueryWrapper<Project> projectQW = new LambdaQueryWrapper<>();
        projectQW.eq(Project::getCode, MapUtils.getString(taskMap,"project_code"));
        Project project = projectService.getBaseMapper().selectOne(projectQW);
        if(null != project && project.getAuto_update_schedule()>0){
            Integer taskCount = baseMapper.selectCountByProjectCode(MapUtils.getString(taskMap,"project_code"));
            if(taskCount>0){
                Integer doneTaskCount = baseMapper.selectCountByProjectCodeAndDone(MapUtils.getString(taskMap,"project_code"));
                taskCount = taskCount==0?1:taskCount;
                project.setSchedule((double) (doneTaskCount/taskCount*100));
                projectService.updateById(project);
            }
        }
        taskHook(memberCode,taskCode,done>0?"done":"redo","",0,
                "","","",null,null);
        if(StringUtils.isNotEmpty(MapUtils.getString(taskMap,"pcode"))){
            taskHook(memberCode,MapUtils.getString(taskMap,"pcode"),done>0?"doneChild":"redoChild","",0,
                    "","","",null,null);
        }
    }
    @Autowired
    ProjectVersionService projectVersionService;


    public void run(Map data){
        int isRobot = MapUtils.getObject(data,"data")!=null && MapUtils.getString((Map)data.get("data"),"is_robot")!=null?1:0;
        ProjectLog logData = ProjectLog.builder().member_code(MapUtils.getString(data,"memberCode"))
                .source_code(MapUtils.getString(data,"taskCode"))
                .remark(MapUtils.getString(data,"remark"))
                .type(MapUtils.getString(data,"type"))
                .content(MapUtils.getString(data,"content"))
                .is_comment(MapUtils.getInteger(data,"isComment"))
                .to_member_code(MapUtils.getString(data,"toMemberCode"))
                .create_time(DateUtil.getCurrentDateTime())
                .code(CommUtils.getUUID())
                .action_type("task").is_robot(isRobot).build();
        Task task = getTaskByCode(MapUtils.getString(data,"taskCode"));
        logData.setProject_code(task.getProject_code());
        Member toMember = null;
        if(StringUtils.isNotEmpty(MapUtils.getString(data,"toMemberCode"))){
            toMember = memberService.getMemberByCode(MapUtils.getString(data,"toMemberCode"));
        }
        Notify notifyData = Notify.builder().title("").content("")
                .type("message").action("task").terminal("project").source_code(task.getCode()).build();
        String remark="";
        String content="";
        String icon = "";
        switch (MapUtils.getString(data,"type","")){
            case "create":
                icon = "plus";
                remark = "创建了任务 ";
                content = task.getName();
                break;
            case "name":
                icon = "edit";
                remark = "更新了内容 ";
                content = task.getName();
                break;
            case "move":
                icon = "drag";
                remark = "将任务移动到 "+MapUtils.getString((Map)data.get("data"),"stageName");
                content = task.getName();
                break;
            case "content":
                icon = "file-text";
                remark = "更新了备注 ";
                content = task.getDescription();
                break;
            case "clearContent":
                icon = "file-text";
                remark = "清空了备注 ";
                break;
            case "done":
                icon = "check";
                remark = "完成了任务 ";
                if (StringUtils.isNotEmpty(task.getVersion_code())) {
                    projectVersionService.updateSchedule(task.getVersion_code());
                }
                break;
            case "redo":
                icon = "border";
                remark = "重做了任务 ";
                if (StringUtils.isNotEmpty(task.getVersion_code())) {
                    projectVersionService.updateSchedule(task.getVersion_code());
                }
                break;
            case "createChild":
                icon = "bars";
                remark = "添加了子任务 "+MapUtils.getString((Map)data.get("data"),"taskName");
                break;
            case "doneChild":
                icon = "bars";
                remark = "完成了子任务 "+ task.getName();
                break;
            case "redoChild":
                icon = "undo";
                remark = "重做了子任务 "+ task.getName();
                break;
            case "claim":
                icon = "user";
                remark = "认领了任务 ";
                break;
            case "assign":
                icon = "user";
                remark = "指派给了 "+toMember.getName();
                break;
            case "pri":
                icon = "user";
                remark = "更新任务优先级为 "+getPriTextAttr(String.valueOf(task.getPri()));
                break;
            case "status":
                icon = "deployment-unit";
                remark = "修改执行状态为 " +getStatusTextAttr(String.valueOf(task.getStatus())) ;
                break;
            case "removeExecutor":
                icon = "user-delete";
                remark = "移除了执行者 ";
                break;
            case "changeState":
                icon = "edit";
                TaskStage taskStage = taskStageService.getTaskStageByCode(task.getStage_code());
                remark = "将任务移动到 "+taskStage.getName();
                break;
            case "inviteMember":
                icon = "user-add";
                remark = "添加了参与者 "+toMember.getName();
                break;
            case "removeMember":
                icon = "user-delete";
                remark = "移除了参与者 "+toMember.getName();
                break;
            case "setBeginTime":
                icon = "calendar";
                remark = "更新开始时间为 "+task.getBegin_time();
                break;
            case "clearBeginTime":
                icon = "calendar";
                remark = "清除了开始时间 ";
                break;
            case "setEndTime":
                icon = "calendar";
                remark = "更新截止时间为 "+ task.getEnd_time();
                break;
            case "clearEndTime":
                icon = "calendar";
                remark = "清除了截止时间 ";
                break;
            case "recycle":
                icon = "delete";
                remark = "把任务移到了回收站 ";
                break;
            case "recovery":
                icon = "undo";
                remark = "恢复了任务 ";
                break;
            case "setWorkTime":
                icon = "clock-circle";
                remark = "更新预估工时为 "+task.getWork_time();
                break;
            case "linkFile":
                icon = "link";
                remark = "关联了文件 ";
                remark = "<a target='_blank' class='muted' href='"+MapUtils.getString((Map)data.get("data"),"url")+ "'>{$data['data']['title']}</a>";

                break;
            case "unlinkFile":
                icon = "disconnect";
                remark = "取消关联文件";
                remark = "<a target='_blank' class='muted' href='"+MapUtils.getString((Map)data.get("data"),"url")+ "'>"+MapUtils.getString((Map)data.get("data"),"title")+ "</a>";
                break;
            case "comment":
                icon = "file-text";
                remark = MapUtils.getString(data,"content","");
                content = MapUtils.getString(data,"content","");
                break;
            default:
                icon = "plus";
                remark = " 创建了任务 ";
                break;
        }
        logData.setIcon(icon);
        if(logData.getIs_robot()>0){
            logData.setIcon("alert");
        }
        if(StringUtils.isNotEmpty(MapUtils.getString(data,"remark"))){
            logData.setRemark(MapUtils.getString(data,"remark"));
        }
        if(StringUtils.isNotEmpty(MapUtils.getString(data,"content"))){
            logData.setContent(MapUtils.getString(data,"content"));
        }
        projectLogService.save(logData);

        //工作流事件
        //触发推送的事件
        //todo 短信,消息推送
        //通知所有组织内的成员
        // todo



    }
}
