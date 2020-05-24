package com.projectm.project.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.framework.common.utils.StringUtils;
import com.projectm.common.AjaxResult;
import com.projectm.common.CommUtils;
import com.projectm.common.DateUtil;
import com.projectm.member.service.MemberService;
import com.projectm.project.domain.*;
import com.projectm.project.service.ProjectFeaturesService;
import com.projectm.project.service.ProjectInfoService;
import com.projectm.project.service.ProjectVersionLogService;
import com.projectm.project.service.ProjectVersionService;
import com.projectm.task.domain.Task;
import com.projectm.task.service.FileService;
import com.projectm.task.service.TaskService;
import com.projectm.web.BaseController;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/project")
public class ProjectAssistController  extends BaseController {
    @Autowired
    private FileService fileService;
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private ProjectFeaturesService projectFeaturesService;

    @Autowired
    private ProjectVersionService projectVersionService;

    @Autowired
    private MemberService memberService;
    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectVersionLogService projectVersionLogService;


    /**编辑版本库
     * @param
     * @return
     */
    @PostMapping("/project/project_features/edit")
    @ResponseBody
    public AjaxResult getProjectVersionEdit(@RequestParam Map<String,Object> mmap){
        String featuresCode = MapUtils.getString(mmap,"featuresCode");
        String projectCode = MapUtils.getString(mmap,"projectCode");
        String description = MapUtils.getString(mmap,"description");
        String name = MapUtils.getString(mmap,"name");
        if(CommUtils.isEmpty(name)){
            return AjaxResult.warn("请填写版本库名称");
        }
        if(CommUtils.isEmpty(featuresCode)){
            return AjaxResult.warn("请选择一个版本库");
        }
        Map m = projectFeaturesService.getProjectFeaturesOneByNameAndProjectCode(name,projectCode);
        if(MapUtils.isNotEmpty(m)){
            return AjaxResult.warn("该版本库已名称存在");
        }
        m = projectFeaturesService.getProjectFeaturesByCode(featuresCode);
        ProjectFeatures pf = new ProjectFeatures();
        pf.setId(MapUtils.getInteger(m,"id"));
        pf.setName(name);pf.setDescription(description);
        return AjaxResult.success(projectFeaturesService.updateById(pf));

    }

    /**删除版本库
     * @param
     * @return
     */
    @PostMapping("/project/project_features/delete")
    @ResponseBody
    public AjaxResult getProjectVersionDelete(@RequestParam Map<String,Object> mmap){
        String featuresCode = MapUtils.getString(mmap,"featuresCode");
        if(CommUtils.isEmpty(featuresCode)){
            return AjaxResult.warn("请选择一个版本库");
        }
        return AjaxResult.success(projectFeaturesService.delProjectFeaturesAndTask(featuresCode));

    }

    /**
     * @param创建项目版本库
     * @return
     */
    @PostMapping("/project/project_features/save")
    @ResponseBody
    public AjaxResult projectProjectFeaturesSave(@RequestParam Map<String,Object> mmap){
        String projectCode = MapUtils.getString(mmap,"projectCode");
        String description = MapUtils.getString(mmap,"description");
        String name = MapUtils.getString(mmap,"name");
        if(null == name || "".equals(name)){
            return AjaxResult.warn("请填写版本库名称");
        }
        Map m = projectFeaturesService.getProjectFeaturesOneByNameAndProjectCode(name,projectCode);
        if(MapUtils.isNotEmpty(m)){
            return AjaxResult.warn("该版本库已名称存在");
        }
        ProjectFeatures pf = new ProjectFeatures();
        pf.setCode(CommUtils.getUUID());
        pf.setCreate_time(DateUtil.formatDateTime(new Date()));
        pf.setProject_code(projectCode);
        pf.setDescription(description);
        pf.setName(name);
        pf.setOrganization_code(MapUtils.getString(getLoginMember(),"organizationCode"));
        return AjaxResult.success(projectFeaturesService.save(pf));
    }

    /**
     *
     * @param
     * @return
     */
    @PostMapping("/project/project_features")
    @ResponseBody
    public AjaxResult projectProjectFeatures(@RequestParam Map<String,Object> mmap){
        String projectCode = MapUtils.getString(mmap,"projectCode");
        if(CommUtils.isEmpty(projectCode)){
            return AjaxResult.warn("请选择一个项目");
        }
        return AjaxResult.success(projectFeaturesService.getProjectFeaturesByProjectCode(projectCode));
    }


    /**
     * @param
     * @return
     */
    @PostMapping("/project/project_version/save")
    @ResponseBody
    public AjaxResult getProjectVersionSave(@RequestParam Map<String,Object> mmap){
        Map memberMap = getLoginMember();
        String name = MapUtils.getString(mmap,"name");
        String description = MapUtils.getString(mmap,"description");
        String startTime = MapUtils.getString(mmap,"startTime");
        String planPublishTime = MapUtils.getString(mmap,"planPublishTime");
        String featuresCode = MapUtils.getString(mmap,"featuresCode");

        if(CommUtils.isEmpty(name)){
            return AjaxResult.warn("请填写版本名称");
        }

        Map m = projectFeaturesService.getProjectFeaturesByCode(featuresCode);
        if(MapUtils.isNotEmpty(m)){
            return AjaxResult.warn("该版本库已失效");
        }
        m = projectVersionService.gettProjectVersionByNameAndFeaturesCode(name,featuresCode);
        if(MapUtils.isNotEmpty(m)){
            return AjaxResult.warn("该版本已名称存在");
        }

        ProjectVersion pv = new ProjectVersion();
        ProjectVersionLog pvl = new ProjectVersionLog();
        pv.setCreate_time(DateUtil.formatDateTime(new Date()));
        pv.setCode(CommUtils.getUUID());pv.setFeatures_code(featuresCode);
        pv.setStart_time(startTime);pv.setPlan_publish_time(planPublishTime);
        pv.setDescription(description);pv.setName(name);
        pv.setOrganization_code(MapUtils.getString(memberMap,"organizationCode"));
        pvl.setMember_code(MapUtils.getString(mmap,"memberCountCode"));
        pvl.setSource_code(pv.getCode());pvl.setRemark("创建了新版本");
        pvl.setType("create");pvl.setContent(name);pvl.setCreate_time(DateUtil.formatDateTime(new Date()));
        pvl.setCode(CommUtils.getUUID());pvl.setFeatures_code(featuresCode);pvl.setIcon("plus");
        Integer i = projectVersionService.addProjectVersionAndVersionLog(pv,pvl);
        if(i == 2){
            Map pvMap = projectVersionService.getProjectVersionByCode(pv.getCode());
            return AjaxResult.success(pv);
        }
        return AjaxResult.warn("保存失败");
    }
    /**
     * 项目版本
     * @param
     * @return
     */
    @PostMapping("/project/project_version")
    @ResponseBody
    public AjaxResult getProjectVersion(@RequestParam Map<String,Object> mmap){
        String projectFeaturesCode = MapUtils.getString(mmap,"projectFeaturesCode");
        if(CommUtils.isEmpty(projectFeaturesCode)){
            return AjaxResult.warn("请选择一个版本库");
        }
        return AjaxResult.success(projectVersionService.getProjectVersion(projectFeaturesCode));
    }
    /**
     * 项目版本删除
     * @param
     * @return
     */
    @PostMapping("/project/project_version/delete")
    @ResponseBody
    public AjaxResult projectVersionDelete(@RequestParam Map<String,Object> mmap){
        String versionCode = MapUtils.getString(mmap,"versionCode");
        if(CommUtils.isEmpty(versionCode)){
            return AjaxResult.warn("请选择一个版本");
        }
        return AjaxResult.success(projectVersionService.delProjectVersion(versionCode));
    }
    /**
     * @param
     * @return
     */
    @PostMapping("/project/project_version/_getVersionTask")
    @ResponseBody
    public AjaxResult getVersionTask(@RequestParam Map<String,Object> mmap){
        String versionCode = MapUtils.getString(mmap,"versionCode");
        if(CommUtils.isEmpty(versionCode)){
            return AjaxResult.warn("请选择一个版本");
        }
        Map param = new HashMap(){{
            put("versionCode",versionCode);
            put("deleted",0);
        }};
        List<Map>  taskList = taskService.getTaskListByVersionAndDelete(param);
        List<Map> resultList = new ArrayList<>();
        Map memberMap = null;
        for(Map m:taskList){
            memberMap = memberService.getMemberMapByCode(MapUtils.getString(m,"assign_to"));
            m.put("executor",CommUtils.getMapField(memberMap,new String[]{"name","avatar"}));
            resultList.add(m);
        }
        return AjaxResult.success(resultList);
    }

    /**查询版本日志
     * @param
     * @return
     */
    @PostMapping("/project/project_version/_getVersionLog")
    @ResponseBody
    public AjaxResult getVersionLog(@RequestParam Map<String,Object> mmap){
        String versionCode = MapUtils.getString(mmap,"versionCode");
        Integer showAll = MapUtils.getInteger(mmap,"all",0);

        List<Map> selList = new ArrayList<>();
        List<Map> listResult = new ArrayList<>();
        Map resultData = new HashMap();

        if(showAll == 0){
           selList = projectVersionLogService.getProjectVersionLogBySourceCodeAll(versionCode);
           if(selList == null)selList = new ArrayList<>();

           resultData.put("total",selList.size());
        }else{
            Integer pageSize = MapUtils.getInteger(mmap,"pageSize",1000);
            Integer page = MapUtils.getInteger(mmap,"page",1);
            IPage<Map> iPage = new Page<>();
            iPage.setCurrent(page);iPage.setSize(pageSize);
            iPage = projectVersionLogService.getProjectVersionBySourceCode(iPage,versionCode);
            selList = iPage.getRecords();
            resultData.put("total",iPage.getTotal());
            resultData.put("page",iPage.getCurrent());
        }
        if(!CollectionUtils.isEmpty(selList)){
            Map memberMap = null;
            for(Map m:selList){
                memberMap = memberService.getMemberMapByCode(MapUtils.getString(m,"member_code"));
                m.put("member",CommUtils.getMapField(memberMap,new String[]{"id","name","avatar","code"}));
                listResult.add(m);
            }
        }
        resultData.put("list",listResult);
        return AjaxResult.success(resultData);
    }

    /**关联任务
     * @param
     * @return
     */
    @PostMapping("/project/project_version/addVersionTask")
    @ResponseBody
    public AjaxResult addVersionTask(@RequestParam Map<String,Object> mmap){
        String taskCodeList = MapUtils.getString(mmap,"taskCodeList");
        String versionCode = MapUtils.getString(mmap,"versionCode");
        Map memberMap = getLoginMember();
        Integer successTotal = 0;
        if(!CommUtils.isEmpty(taskCodeList)){
            JSONArray jsonArray = JSON.parseArray(taskCodeList);
            if(StringUtils.isNotEmpty(jsonArray)){
                List<Task> saveTaskList = new ArrayList<>();
                List<ProjectVersion> saveProjectVersionList = new ArrayList<>();
                List<String> taskListName = new ArrayList<>();
                Task task = null;ProjectVersion pv = null;
                for (Object obj : jsonArray) {

                    Map taskMap = taskService.getTaskByCode(String.valueOf(obj));
                    if(MapUtils.isEmpty(taskMap)){
                        return AjaxResult.warn("该任务已被失效");
                    }
                    if(!CommUtils.isEmpty(MapUtils.getString(taskMap,"version_code"))){
                        return AjaxResult.warn("该任务已被关联");
                    }
                    Map versionMap = projectVersionService.getProjectVersionByCode(versionCode);
                    if(MapUtils.isEmpty(taskMap)){
                        return AjaxResult.warn("该版本已被失效");
                    }
                    task = new Task();
                    task.setId(MapUtils.getInteger(taskMap,"id"));task.setVersion_code(versionCode);task.setFeatures_code(MapUtils.getString(versionMap,"features_code"));
                    saveTaskList.add(task);
                    pv = new ProjectVersion();
                    pv.setId(MapUtils.getInteger(versionMap,"id"));
                    pv.setSchedule(projectVersionService.getScheduleByVersion(versionCode));
                    saveProjectVersionList.add(pv);
                    taskListName.add(MapUtils.getString(taskMap,"name"));
                }
                successTotal = projectVersionService.addVersionTask(saveTaskList,saveProjectVersionList);
                if(successTotal>0){
                    Map projectVersion = projectVersionService.getProjectVersionByCode(versionCode);
                    ProjectVersionLog pvl = new ProjectVersionLog();
                    pvl.setMember_code(MapUtils.getString(memberMap,"memberCode")).setSource_code(versionCode);pvl.setRemark("添加了 " + successTotal + " 项目发布内容");
                    pvl.setType("addVersionTask").setContent(taskListName.toString()).setCreate_time(DateUtil.formatDateTime(new Date()));
                    pvl.setFeatures_code(MapUtils.getString(projectVersion,"features_code")).setIcon("link");
                    projectVersionLogService.save(pvl);
                }
            }
        }
        Map result = new HashMap();result.put("successTotal",successTotal);
        return AjaxResult.success(result);
    }
    /**
     * 更改版本状态
     * @param
     * @return
     */
    @PostMapping("/project/project_version/changeStatus")
    @ResponseBody
    public AjaxResult projectVersionChangeStatus(@RequestParam Map<String,Object> mmap) {
        String versionCode = MapUtils.getString(mmap, "versionCode");
        Integer status = MapUtils.getInteger(mmap, "status",-1);
        String publishTime = MapUtils.getString(mmap, "publishTime");
        Map memberMap = getLoginMember();
        if (CommUtils.isEmpty(versionCode)) {
            return AjaxResult.warn("请选择一个版本");
        }
        Map versionMap = projectVersionService.getProjectVersionByCode(versionCode);
        ProjectVersion pv = new ProjectVersion();
        pv.setId(MapUtils.getInteger(versionMap,"id"));
        pv.setStatus(status);
        if(status == 3){
            pv.setPublish_time(publishTime);
        }
        boolean i = projectVersionService.updateById(pv);
        ProjectVersionLog pvl = new ProjectVersionLog();
       pvl.setMember_code(MapUtils.getString(memberMap,"memberCode"));
        pvl.setSource_code(versionCode).setRemark("更新了状态为"+ getStatusTextAttr(String.valueOf(status)));
        pvl.setType("status").setContent("").setCreate_time(DateUtil.formatDateTime(new Date()));
        pvl.setFeatures_code(MapUtils.getString(versionMap,"features_code")).setIcon("check-square");
        projectVersionLogService.save(pvl);
        return AjaxResult.success(i);
    }
    /**
     * 项目版本编辑
     * @param
     * @return
     */
    @PostMapping("/project/project_version/edit")
    @ResponseBody
    public AjaxResult projectVersionEdit(@RequestParam Map<String,Object> mmap){
        String versionCode = MapUtils.getString(mmap,"versionCode");
        String name = MapUtils.getString(mmap,"name");
        String description = MapUtils.getString(mmap,"description");
        String start_time = MapUtils.getString(mmap,"start_time");
        String plan_publish_time = MapUtils.getString(mmap,"plan_publish_time");
        if(CommUtils.isEmpty(versionCode)){
            return AjaxResult.warn("请选择一个版本");
        }
        Map versionMap = projectVersionService.getProjectVersionByCode(versionCode);
        if(MapUtils.isEmpty(versionMap)){
            return AjaxResult.warn("该版本已失效");
        }
        if(!CommUtils.isEmpty(name)){
            Map proVerMap = projectVersionService.gettProjectVersionByNameAndFeaturesCode(name,MapUtils.getString(versionMap,"features_code"));
            if(MapUtils.isNotEmpty(proVerMap)){
                return AjaxResult.warn("该版本名称已存在");
            }
        }
        String type = "name";
        ProjectVersion upProjectVersion = new ProjectVersion();
        upProjectVersion.setId(MapUtils.getInteger(versionMap,"id"));
        if(!CommUtils.isEmpty(name))upProjectVersion.setName(name);
        if(!CommUtils.isEmpty(description))upProjectVersion.setDescription(description);
        if(!CommUtils.isEmpty(start_time))upProjectVersion.setStart_time(start_time);
        if(!CommUtils.isEmpty(plan_publish_time))upProjectVersion.setPlan_publish_time(plan_publish_time);
        boolean bo =  projectVersionService.updateById(upProjectVersion);
        ProjectVersionLog pvl = new ProjectVersionLog();
        String remark = "";
        if(null != name){
            type="name";
            remark = "更新名称为 " + name + " ";
        }
        if(null != description){
            type="content";
            remark = "更新描述为 " + description + " ";
            if("".equals(description)){
                type="clearContent";
                remark = " 清空描述内容 ";
            }
        }
        if(null != start_time){
            type="setStartTime";
            remark = "更新开始时间为 " + start_time + " ";
            if("".equals(start_time)){
                type="clearStartTime";
                remark = " 清空开始时间 ";
            }
        }
        if(null != plan_publish_time){
            type="setPlanPublishTime";
            remark = "更新计划发布时间为 "+ plan_publish_time;
            if("".equals(plan_publish_time)){
                type="clearPlanPublishTime";
                remark = "清除计划发布时间";
            }
        }


        Map memberMap = getLoginMember();

        pvl.setMember_code(MapUtils.getString(memberMap,"memberCode"));
        pvl.setSource_code(versionCode).setRemark(remark);
        pvl.setType("status").setContent("").setCreate_time(DateUtil.formatDateTime(new Date()));
        pvl.setFeatures_code(MapUtils.getString(versionMap,"features_code")).setIcon("check-square");
        projectVersionLogService.save(pvl);
        return AjaxResult.success(bo);
    }
    /**
     * 项目版本读取
     * @param
     * @return
     */
    @PostMapping("/project/project_version/read")
    @ResponseBody
    public AjaxResult projectVersionRead(@RequestParam Map<String,Object> mmap){
        String versionCode = MapUtils.getString(mmap,"versionCode");
        if(CommUtils.isEmpty(versionCode)){
            return AjaxResult.warn("请选择一个版本");
        }
        Map versionMap = projectVersionService.getProjectVersionByCode(versionCode);
        versionMap.put("statusText",getStatusTextAttr(MapUtils.getString(versionMap,"status")));
        if(MapUtils.isNotEmpty(versionMap)){
            Map featureMap = projectFeaturesService.getProjectFeaturesByCode(MapUtils.getString(versionMap,"features_code"));
            versionMap.put("featureName",MapUtils.getString(featureMap,"name"));
            versionMap.put("projectCode",MapUtils.getString(featureMap,"project_code"));
        }
        return AjaxResult.success(versionMap);
    }

    /**
     * 我的文件	移入回收站
     * @param
     * @return
     */
    @PostMapping("/project/project_info")
    @ResponseBody
    public AjaxResult projectProjectInfo(@RequestParam Map<String,Object> mmap){
        String projectCode = MapUtils.getString(mmap,"projectCode");
        List<Map> projectInfoList = projectInfoService.getProjectInfoByProjectCode(projectCode);
        return AjaxResult.success(projectInfoList);

    }
    /**
     * 我的文件	移入回收站
     * @param
     * @return
     */
    @PostMapping("/project/file/recycle")
    @ResponseBody
    public AjaxResult projectFileRecycle(@RequestParam Map<String,Object> mmap){
        String fileCode = MapUtils.getString(mmap,"fileCode");

        Map fileMap = fileService.getFileByCode(fileCode);
        com.projectm.task.domain.File projectFile = new com.projectm.task.domain.File();
        projectFile.setId(MapUtils.getInteger(fileMap,"id"));
        projectFile.setDeleted(1);projectFile.setDeleted_time(DateUtil.formatDateTime(new Date()));
        return AjaxResult.success(fileService.updateById(projectFile));
    }
    /**
     * 我的文件	改名
     * @param
     * @return
     */
    @PostMapping("/project/file/edit")
    @ResponseBody
    public AjaxResult projectFileEdit(@RequestParam Map<String,Object> mmap){
        String title = MapUtils.getString(mmap,"title");
        String fileCode = MapUtils.getString(mmap,"fileCode");

        Map fileMap = fileService.getFileByCode(fileCode);
        com.projectm.task.domain.File projectFile = new com.projectm.task.domain.File();
        projectFile.setId(MapUtils.getInteger(fileMap,"id"));
        projectFile.setTitle(title);
        return AjaxResult.success(fileService.updateById(projectFile));
    }

    /**
     * 我的文件清单
     * @param
     * @return
     */
    @PostMapping("/project/file")
    @ResponseBody
    public AjaxResult getProjectFile(@RequestParam Map<String,Object> mmap){
        Integer pageSize = MapUtils.getInteger(mmap,"pageSize",50);
        Integer page = MapUtils.getInteger(mmap,"page",1);
        String projectCode = MapUtils.getString(mmap,"projectCode");
        Integer deleted = MapUtils.getInteger(mmap,"deleted",0);
        Map params = new HashMap(){{
            put("projectCode",projectCode);
            put("deleted",deleted);
        }};
        IPage<Map> iPage = new Page<>();
        iPage.setCurrent(page);iPage.setSize(pageSize);
        iPage = fileService.gettFileByProjectCodeAndDelete(iPage,params);
        List<Map> resultList = new ArrayList<>();
        for(int i=0;iPage !=null && iPage.getRecords() !=null && i<iPage.getRecords().size();i++){
            Map fileMap = iPage.getRecords().get(i);
            Map memberMap = memberService.getMemberMapByCode(MapUtils.getString(fileMap,"create_by"));
            fileMap.put("creatorName",MapUtils.getString(memberMap,"name"));
            fileMap.put("fullName",MapUtils.getString(fileMap,"title")+"."+MapUtils.getString(fileMap,"extension"));
            resultList.add(fileMap);
        }
        Map data = new HashMap();
        data.put("list",resultList);
        data.put("total",iPage.getTotal());
        data.put("page",iPage.getCurrent());
        return new AjaxResult(AjaxResult.Type.SUCCESS, "", data);

    }

    protected void versionHook(Map map){
        ProjectVersionLog pvl = new ProjectVersionLog();
        pvl.setCode(CommUtils.getUUID());pvl.setMember_code(MapUtils.getString(map,"memberCode"));
        pvl.setSource_code(MapUtils.getString(map,"versionCode"));
        pvl.setRemark(MapUtils.getString(map,"remark"));pvl.setType(MapUtils.getString(map,"type"));
        pvl.setContent(MapUtils.getString(map,"content"));pvl.setCreate_time(DateUtil.formatDateTime(new Date()));
        Map versionMap = projectVersionService.getProjectVersionByCode(MapUtils.getString(map,"versionCode"));
        pvl.setFeatures_code(MapUtils.getString(versionMap,"features_code"));
        String remark="",content="",icon = "";

        String type = MapUtils.getString(map,"type");
        if("create".equals(type)){
            icon = "plus";
            remark="创建了版本";
            content = MapUtils.getString(versionMap,"name");
        }else if("status".equals(type)){
            icon = "check-square";
            remark="更新了状态为"+getStatusTextAttr(MapUtils.getString(versionMap,"status"));
        }else if("publish".equals(type)){
            icon = "check-square";
            remark="完成版本时间为 "+MapUtils.getString(versionMap,"publish_time");
        }else if("name".equals(type)){
            icon = "edit";
            remark="更新了版本名";
            content = MapUtils.getString(versionMap,"name");
        }else if("content".equals(type)){
            icon = "file-text";
            remark="更新了备注";
            content = MapUtils.getString(versionMap,"description");
        }else if("clearContent".equals(type)){
            icon = "file-text";
            remark="清空了备注 ";
        }else if("setStartTime".equals(type)){
            icon = "calendar";
            remark="更新开始时间为 " + MapUtils.getString(versionMap,"start_time");
        }else if("clearStartTime".equals(type)){
            icon = "calendar";
            remark="清除了开始时间 ";
        }else if("setPlanPublishTime".equals(type)){
            icon = "calendar";
            remark="更新计划发布时间为 " + MapUtils.getString(versionMap,"plan_publish_time");
        }else if("clearPlanPublishTime".equals(type)){
            icon = "calendar";
            remark="清除了计划发布时间 ";
        }else if("delete".equals(type)){
            icon = "delete";
            remark="删除了版本 ";
        }else if("addVersionTask".equals(type)){


        }else if("removeVersionTask".equals(type)){
            icon = "disconnect";
            remark="移除了发布内容";

        }else{
            icon = "plus";
            remark="创建了版本";
        }
        pvl.setIcon(icon);
        if(!CommUtils.isEmpty(MapUtils.getString(map,"remark"))){
            pvl.setRemark(remark);
        }

        projectVersionLogService.save(pvl);
    }
    protected String getStatusTextAttr(String status){
        //状态。0：未开始，1：进行中，2：延期发布，3：已发布
        if(null == status){
            return "-";
        }
        switch (Integer.parseInt(status)){
            case 0:
                return "未开始";
            case 1:
                return "进行中";
            case 2:
                return "延期发布";
            case 3:
                return "已发布";
        }
        return "-";
    }
}
