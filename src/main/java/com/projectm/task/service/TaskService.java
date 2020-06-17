package com.projectm.task.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.projectm.project.service.ProjectService;
import com.projectm.task.domain.Task;
import com.projectm.task.domain.TaskStage;
import com.projectm.task.domain.TaskToTag;
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

    public Map getTaskMapByCode(String code){
        return  baseMapper.selectTaskByCode(code);
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
        return baseMapper.selectMemberTasks(ipage,params);
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
        if(!MapUtils.isEmpty(parentDone)){
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


}
