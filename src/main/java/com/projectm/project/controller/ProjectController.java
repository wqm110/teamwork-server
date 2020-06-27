package com.projectm.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.framework.common.AjaxResult;
import com.framework.common.utils.StringUtils;
import com.framework.security.util.UserUtil;
import com.projectm.common.*;
import com.projectm.config.MProjectConfig;
import com.projectm.login.entity.LoginUser;
import com.projectm.member.domain.Member;
import com.projectm.member.domain.MemberAccount;
import com.projectm.member.service.MemberAccountService;
import com.projectm.member.service.MemberService;
import com.projectm.member.service.ProjectMemberService;
import com.projectm.org.service.OrgService;
import com.projectm.project.domain.*;
import com.projectm.project.mapper.ProjectLogMapper;
import com.projectm.project.service.*;
import com.projectm.task.domain.Task;
import com.projectm.task.domain.TaskStagesTemplete;
import com.projectm.task.service.FileService;
import com.projectm.task.service.TaskService;
import com.projectm.task.service.TaskStagesTempleteService;
import com.projectm.web.BaseController;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/project")
public class ProjectController extends BaseController {

    @Autowired
    private OrgService orgService;

    @Autowired
    private ProjectService proService;

    @Autowired
    private ProjectTemplateService projectTemplateService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ProjectCollectionService projectCollectionService;

    @Autowired
    private ProjectMemberService projectMemberService;

    @Autowired
    private InviteLinkService inviteLinkService;

    @Autowired
    private TaskStagesTempleteService taskStagesTempleteService;
    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private ProjectMenuService projectMenuService;


    /**
     * 登录系统后，请求的索引
     * @param
     * @return
     */
    @PostMapping("/index")
    @ResponseBody
    public AjaxResult projectIndex(){
        return AjaxResult.success(projectMenuService.getCurrentUserMenu());
    }


    /**
     * 上传头像
     * @param
     * @return
     */
    @PostMapping("/project/getLogBySelfProject")
    @ResponseBody
    public AjaxResult getLogBySelfProject(@RequestParam Map<String,Object> mmap){
        String projectCode = MapUtils.getString(mmap,"projectCode");
        Map loginMember = getLoginMember();
        IPage<Map> ipage = Constant.createPage(mmap);
        Map params = new HashMap();
        params.put("memberCode",MapUtils.getString(loginMember,"memberCode"));
        params.put("orgCode",MapUtils.getString(loginMember,"organizationCode"));
        params.put("projectCode",projectCode);

        IPage<Map> resultData =  proService.getLogBySelfProject(ipage,params);

        if(null != resultData){
            if(StringUtils.isEmpty(projectCode)){
                return new AjaxResult(AjaxResult.Type.SUCCESS, "", resultData.getRecords());
            }else{
                return  AjaxResult.success(Constant.createPageResultMap(ipage));
            }
        }
        return AjaxResult.success();
    }

    @Autowired
    TaskService taskService;
    @Autowired
    ProjectLogMapper projectLogMapper;
    @PostMapping("/project/_projectStats")
    @ResponseBody
    public AjaxResult _projectStats(@RequestParam Map<String,Object> mmap)  throws Exception {
        String projectCode = MapUtils.getString(mmap,"projectCode");
        if(StringUtils.isEmpty(projectCode)){
            return AjaxResult.warn("该项目已失效");
        }
        Map projectMap = proService.getProjectByCode(projectCode);
        if(MapUtils.isEmpty(projectMap)){
            return AjaxResult.warn("该项目已失效");
        }
        List<Map> taskList = taskService.getTaskByProjectCodeAndDel(projectCode,0);
        if(CollectionUtils.isEmpty(taskList)){
            taskList = new ArrayList<>();
        }
        Integer total=0;
        Integer unDone=0;
        Integer done=0;
        Integer overdue=0;
        Integer toBeAssign =  0 ;
        Integer expireToday=0;
        Integer doneOverdue=0;
        Date now = new Date();
        String today = DateUtil.format("yyyy-MM-dd HH:mm:ss",now);
        String tomorrow = DateUtil.format("yyyy-MM-dd HH:mm:ss",DateUtil.add(now,5,-1));
        String nowTime = DateUtil.format("yyyy-MM-dd HH:mm:ss",now);
        for(Map task:taskList){
            if(StringUtils.isNotEmpty(MapUtils.getString(task,"assign_to"))){  toBeAssign++;}
            if(1==MapUtils.getInteger(task,"done",0)){ done ++;}
            if(0==MapUtils.getInteger(task,"done",0)){ unDone ++;}
            String endtime = MapUtils.getString(task,"end_time");
            if(StringUtils.isNotEmpty(MapUtils.getString(task,"end_time"))){
                if(0==MapUtils.getInteger(task,"done",0)){
                    if(-1 == endtime.compareTo(nowTime)){
                        overdue++;
                    }
                    if(endtime.compareTo(tomorrow) == -1 && endtime.compareTo(today) >=0){
                        doneOverdue++;
                    }
                }else{
                    List<Map> list = projectLogMapper.selectProjectLogBySourceCode(MapUtils.getString(task,"done"));
                    if(!CollectionUtils.isEmpty(list)){
                        Map m = list.get(0);
                        String createTime = MapUtils.getString(m,"create_time");
                        if(endtime.compareTo(createTime) == -1){
                            doneOverdue++;
                        }
                    }
                }
            }
        }
        Map data = new HashMap();
        data.put("total", taskList.size());
        data.put("unDone",unDone);
        data.put("done",done);
        data.put("overdue",overdue);
        data.put("toBeAssign",toBeAssign);
        data.put("expireToday",expireToday);
        data.put("doneOverdue",doneOverdue);
        return AjaxResult.success(data);
    }

    @Autowired
    ProjectReportService projectReportService;
    @PostMapping("/project/_getProjectReport")
    @ResponseBody
    public AjaxResult _getProjectReport(@RequestParam Map<String,Object> mmap)  throws Exception {
        String projectCode = MapUtils.getString(mmap, "projectCode");
        if(StringUtils.isEmpty(projectCode)){
            return AjaxResult.success("项目已失效");
        }
        return AjaxResult.success(projectReportService.getReportByDay(projectCode,10));
    }

    @Autowired
    FileService fileService;

    @PostMapping("/file/uploadFiles")
    @ResponseBody
    public AjaxResult uploadFiels(HttpServletRequest request, @RequestParam("file") MultipartFile multipartFile)  throws Exception{
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        String  fileName= request.getParameter("identifier");
        String  orgFileName= request.getParameter("filename");
        String  chunkNumber= request.getParameter("chunkNumber");
        String  totalChunks= request.getParameter("totalChunks");
        String  taskCode= request.getParameter("taskCode");
        String projectCode = request.getParameter("projectCode");
        Map loginMember = getLoginMember();
        String orgCode = MapUtils.getString(loginMember,"organizationCode");
        String memberCode = MapUtils.getString(loginMember,"memberCode");
        if (multipartFile.isEmpty()) {
            return  AjaxResult.warn("文件名不能为空！");
        } else {
            String dateTimeNow = DateUtils.dateTimeNow();
            String date = DateUtils.getDate();
            // 文件原名称
            String originFileName = multipartFile.getOriginalFilename().toString();
            // 上传文件重命名
            String uploadFileName = CommUtils.getUUID()+multipartFile.getOriginalFilename().toString().substring(multipartFile.getOriginalFilename().toString().indexOf("."));
            String file_url = MProjectConfig.getUploadFolderPath()+memberCode+"/"+date+"/"+dateTimeNow+uploadFileName;
            // 这里使用Apache的FileUtils方法来进行保存
            FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), new File(file_url, uploadFileName));
            com.projectm.task.domain.File file = com.projectm.task.domain.File.builder().fsize(multipartFile.getSize())
                    .path_name(file_url).file_url(basePath+file_url).title(multipartFile.getOriginalFilename()).file_type("text/plain").create_time(DateUtils.dateTimeNow())
                    .code(CommUtils.getUUID()).organization_code(orgCode).project_code(projectCode).create_by(memberCode)
                    .deleted(0).downloads(0l).task_code(taskCode).build();
            Project project = fileService.uploadFiles(file,memberCode,projectCode);
            Map result = new HashMap();
            result.put("key",file.getPath_name());
            result.put("url",file.getFile_url());
            result.put("projectName",project.getName());
            return AjaxResult.success(result);
        }
    }



    /**
     * 上传头像
     * @param
     * @return
     */
    @PostMapping("/index/uploadAvatar")
    @ResponseBody
    public AjaxResult uploadAvatar(HttpServletRequest request, @RequestParam("avatar") MultipartFile multipartFile)  throws Exception
    {
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        String code = request.getParameter("code");
        String avatar = request.getParameter("avatar");
        Map resMap = new HashMap();
         if (multipartFile.isEmpty()) {
             return  AjaxResult.warn("文件名不能为空！");
         } else {
             String file_url = MProjectConfig.getUploadFolderPath()+"member/avatar/";
             // 文件原名称
             String originFileName = multipartFile.getOriginalFilename().toString();
             // 上传文件重命名
             String uploadFileName = CommUtils.getUUID()+multipartFile.getOriginalFilename().toString().substring(multipartFile.getOriginalFilename().toString().indexOf("."));
             try {
                 // 这里使用Apache的FileUtils方法来进行保存
                 FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), new File(file_url, uploadFileName));
                 String base_url = MProjectConfig.getStaticUploadPrefix()+"member/avatar/"+uploadFileName;
                 resMap.put("base_url", base_url);
                 resMap.put("url",basePath+base_url);
                 resMap.put("filename", uploadFileName);
                 Map memberMap = memberService.getMemberMapByCode(code);
                 Map memberAccountMap = memberAccountService.getMemberAccountByMemCode(code);

                 Member member = new Member();
                 MemberAccount memberAccount = new MemberAccount();
                 member.setId(MapUtils.getInteger(memberMap,"id"));
                 member.setAvatar(basePath+base_url);
                 memberAccount.setId(MapUtils.getInteger(memberAccountMap,"id"));
                 memberAccount.setAvatar(basePath+base_url);
                 Integer upresult = memberService.updateMemberAccountAndMember(memberAccount,member);
             } catch (IOException e) {
                 return AjaxResult.error(e.getMessage());
             }
         }
         return  AjaxResult.success(resMap);
    }

    @Autowired
    SourceLinkService sourceLinkService;

    @PostMapping("/source_link/delete")
    @ResponseBody
    public AjaxResult sourceLinkDel(@RequestParam Map<String,Object> mmap){
        String sourceCode = MapUtils.getString(mmap,"sourceCode");
        Map loginMap = getLoginMember();
        if(StringUtils.isEmpty(sourceCode)){
            return AjaxResult.warn("资源不存在！");
        }
        int i = sourceLinkService.deleteSource(sourceCode,MapUtils.getString(loginMap,"memberCode"));
        return AjaxResult.success(i);
    }

    /**
     * 项目管理	我的项目 项目设置 项目删除（回收站）
     * @param mmap
     * @return
     */
    @PostMapping("/invite_link/save")
    @ResponseBody
    public AjaxResult inviteLinkSave(@RequestParam Map<String,Object> mmap)  throws Exception
    {
        Map loginMember = getLoginMember();
        String inviteType = MapUtils.getString(mmap,"inviteType");
        String sourceCode = MapUtils.getString(mmap,"sourceCode");
        String memberCode = MapUtils.getString(loginMember,"memberCode");
        Map inviteLink = inviteLinkService.getInviteLinkByInSoCr("project","sourceCode","memberCode");

        if(MapUtils.isEmpty(inviteLink)){
            InviteLink il = new InviteLink();
            il.setCode(CommUtils.getUUID());
            il.setCreate_by(memberCode);
            il.setInvite_type("project");
            il.setCreate_time(DateUtil.formatDateTime(new Date()));
            il.setOver_time(DateUtil.formatDateTime(DateUtil.add(new Date(),5,1)));
            il.setSource_code(sourceCode);
            inviteLinkService.save(il);
            return AjaxResult.success(il);
        }
        return AjaxResult.success(inviteLink);
    }

    /**
     * 项目管理	我的项目 项目设置 项目删除（回收站）
     * @param mmap
     * @return
     */
    @PostMapping("/project/recycle")
    @ResponseBody
    public AjaxResult recycle(@RequestParam Map<String,Object> mmap)  throws Exception
        {
        String projectCode = String.valueOf(mmap.get("projectCode"));
        Map projectMap = proService.getProjectByCode(projectCode);
        if(MapUtils.isEmpty(projectMap)){
            return AjaxResult.warn("文件不存在");
        }
        if("1".equals(MapUtils.getString(projectMap,"deleted"))){
            return AjaxResult.warn("文件已在回收站");
        }
        int i = proService.updateRecycleByCode(projectCode,1,DateUtil.formatDateTime(new Date()));
        return AjaxResult.success(i);
    }
    /**
     * 项目管理	我的项目 项目设置 项目删除恢复（回收站）
     * @param mmap
     * @return
     */
    @PostMapping("/project/recovery")
    @ResponseBody
    public AjaxResult recovery(@RequestParam Map<String,Object> mmap)  throws Exception
    {
        String projectCode = String.valueOf(mmap.get("projectCode"));
        int i = proService.updateRecycleByCode(projectCode,0,DateUtil.formatDateTime(new Date()));
        return AjaxResult.success(i);
    }

    /**
     * 项目管理	我的项目 项目设置 项目归档
     * @param mmap
     * @return
     */
    @PostMapping("/project/archive")
    @ResponseBody
    public AjaxResult archive(@RequestParam Map<String,Object> mmap)  throws Exception
    {
        String projectCode = String.valueOf(mmap.get("projectCode"));
        int i = proService.updateArctiveByCode(projectCode,1,DateUtil.formatDateTime(new Date()));
        return AjaxResult.success(i);
    }

    /**
     * 项目管理	已归档项目  取消归档
     * @param mmap
     * @return
     */
    @PostMapping("/project/recoveryArchive")
    @ResponseBody
    public AjaxResult recoveryArchive(@RequestParam Map<String,Object> mmap)  throws Exception
    {
        String projectCode = String.valueOf(mmap.get("projectCode"));
        int i = proService.updateArctiveByCode(projectCode,0,"");
        return AjaxResult.success(i);
    }


    /**
     * 项目管理	我的项目 项目设置 编辑保存
     * @param mmap
     * @return
     */
    @PostMapping("/project/edit")
    @ResponseBody
    public AjaxResult projectEdit(@RequestParam Map<String,Object> mmap)  throws Exception
    {
        String projectCode = String.valueOf(mmap.get("projectCode"));

        Map projectMap = proService.getProjectByCode(projectCode);


        Project project = BeanMapUtils.mapToBean(projectMap,Project.class);
        project.setName(MapUtils.getString(mmap,"name"));
        project.setDescription(MapUtils.getString(mmap,"description"));
        project.setCover(MapUtils.getString(mmap,"cover"));
        project.setPrivated(MapUtils.getInteger(mmap,"private"));
        project.setPrefix(MapUtils.getString(mmap,"prefix"));
        project.setTask_board_theme(MapUtils.getString(mmap,"task_board_theme"));
        project.setOpen_prefix(MapUtils.getInteger(mmap,"prefix"));
        project.setOpen_begin_time(MapUtils.getInteger(mmap,"open_begin_time"));
        project.setOpen_task_private(MapUtils.getInteger(mmap,"open_task_private"));
        project.setSchedule(MapUtils.getDouble(mmap,"schedule"));
        project.setAuto_update_schedule(MapUtils.getInteger(mmap,"update_schedule"));
        boolean result = proService.updateById(project);
        return AjaxResult.success(result);
    }

    /**
     * 项目管理	我的项目 项目设置打开
     * @param mmap
     * @return
     */
    @PostMapping("/project/read")
    @ResponseBody
    public AjaxResult projectRead(@RequestParam Map<String,Object> mmap)
    {
        Map loginMember = getLoginMember();
        String projectCode = MapUtils.getString(mmap,"projectCode");//String.valueOf(mmap.get("projectCode"));
        Map resultData = new HashMap();
        Map projectMap = proService.getProjectByCode(projectCode);
        resultData.putAll(projectMap);
        Map pm = projectMemberService.getProjectMemberByProjectCode(projectCode);
        List<Map> pc = projectCollectionService.getProjectCollection(projectCode,MapUtils.getString(loginMember,"memberCode"));

        if(pc!=null && pc.size()>0 && null!=pc.get(0).get("member_code")){
            resultData.put("collected",1);
        }else{
            resultData.put("collected",0);
        }

        if(ObjectUtils.isNotEmpty(pm)){
            Member member = memberService.getMemberByCode(MapUtils.getString(projectMap,"member_code"));
            if(ObjectUtils.isNotEmpty(member)){
                resultData.put("owner_name",member.getName());
                resultData.put("owner_avatar",member.getAvatar());
            }

        }
        //resultData.put("private",projectMap.get("privated"));
        return AjaxResult.success(resultData);
    }

    /**
     * 项目管理	我的项目 点击项目进行详细页面初始化
     * @param mmap
     * @return
     */
    @PostMapping("/project/selfList")
    @ResponseBody
    public AjaxResult projectSelfList(@RequestParam Map<String,Object> mmap)
    {
        LoginUser loginUser = UserUtil.getLoginUser();
        Map loginMember = getLoginMember();
        Integer archive = MapUtils.getInteger(mmap,"archive",-1);
        Integer type =  MapUtils.getInteger(mmap,"type",0);
        Integer delete = MapUtils.getInteger(mmap,"delete",-1);
        String organizationCode = MapUtils.getString(mmap,"organizationCode","");
        String memberCode = MapUtils.getString(mmap,"memberCode","");

        Member member = null;
        if(StringUtils.isNotEmpty(memberCode)){
            member = memberService.getMemberByCode(memberCode);
        }else{
            member = memberService.getMemberByCode(MapUtils.getString(loginMember,"memberCode"));
        }
        if(ObjectUtils.isEmpty(member)){
            return AjaxResult.warn("参数有误");
        }

        Integer deleted = delete == -1?1:delete;
        if(type == 0){
            deleted = 0;
        }

        IPage<Map> iPage = Constant.createPage(mmap);

        Map params = new HashMap();
        params.put("memberCode",MapUtils.getString(loginMember,"memberCode"));
        params.put("orgCode",MapUtils.getString(loginMember,"organizationCode"));
        params.put("deleted",deleted);params.put("archive",archive);


        iPage = proService.getMemberProjects(iPage,params);

        List<Map> resultList = new ArrayList<>();
        List<Map> records = iPage.getRecords();
        List<Map> pc = null;
        if(!CollectionUtils.isEmpty(records)){
            for(Map map:records){
                map.put("owner_name","-");
                if(StringUtils.isNotEmpty(MapUtils.getString(map,"project_code"))){

                }
                pc = projectCollectionService.getProjectCollection(MapUtils.getString(map,"code"),MapUtils.getString(map,"member_code"));
                if(pc!=null && pc.size()>0 && null!=pc.get(0).get("member_code")){
                    map.put("collected",1);
                }else{
                    map.put("collected",0);
                }
                Map pm = projectMemberService.gettMemberCodeAndNameByProjectCode(MapUtils.getString(map,"code"));
                if(MapUtils.isNotEmpty(pm)){
                    map.put("owner_name",pm.get("name"));
                }
                resultList.add(map);
            }
        }
        iPage.setRecords(resultList);
        Map data = Constant.createPageResultMap(iPage);
        return new AjaxResult(AjaxResult.Type.SUCCESS, "", data);
    }

    /**
     * 项目管理	我的项目 加入/取消收藏
     * @param mmap
     * @return
     */
    @PostMapping("/project_collect/collect")
    @ResponseBody
    public AjaxResult projectCollect(@RequestParam Map<String,Object> mmap)
    {
        Map loginMember = getLoginMember();

        String projectCode = String.valueOf(mmap.get("projectCode"));
        String type = String.valueOf(mmap.get("type"));

        ProjectCollection pc = new ProjectCollection();
        pc.setProject_code(projectCode);
        pc.setMember_code(String.valueOf(loginMember.get("memberCode")));
        pc.setCreate_time(DateUtil.formatDateTime(new Date()));
        if("collect".equals(type)){
            projectCollectionService.collect(pc);
            return AjaxResult.success("加入收藏成功");
        }else{
            projectCollectionService.cancel(pc);
            return AjaxResult.success("取消收藏成功");
        }

    }

    /**
     * 项目管理	我的项目 页面初始化
     * @param mmap
     * @return
     */
    @PostMapping("/project")
    @ResponseBody
    public AjaxResult projectIndex(@RequestParam Map<String,Object> mmap)
    {
        Map loginMember = getLoginMember();
        String archive = MapUtils.getString(mmap,"archive",null);
        String type = MapUtils.getString(mmap,"type",null);
        String recycle = MapUtils.getString(mmap,"recycle",null);
        String all = MapUtils.getString(mmap,"all",null);
        mmap.put("memberCode",MapUtils.getString(loginMember,"memberCode"));
        mmap.put("orgCode",MapUtils.getString(loginMember,"organizationCode"));


        IPage<Map> ipage = Constant.createPage(mmap);
        return proService.projectIndex(ipage,mmap);
        /*Map params = new HashMap();
        params.put("memberCode",String.valueOf(loginMember.get("memberCode")));
        params.put("orgCode",String.valueOf(loginMember.get("organizationCode")));
        if(null != archive){ params.put("archive",archive); }
        if(null != recycle){ params.put("deleted",recycle); }

        IPage<Map> resultData = new Page<>();
        if("collect".equals(type)){
            resultData =  proService.getProjectInfoByMemCodeOrgCodeCollection(ipage,params);
            List<Map> records = resultData.getRecords();
            List<Map> resultRecords = new ArrayList<>();
            Map map = null;Map pc = null;
            for(int i=0;records !=null && i<records.size();i++){
                map = records.get(i);
                map.put("collected",1);
                resultRecords.add(map);
            }
            resultData.setRecords(resultRecords);
        }else if("my".equals(type)){
            params.put("deleted",0);
            resultData =  proService.getProjectInfoByMemCodeOrgCode(ipage,params);
            List<Map> records = resultData.getRecords();
            List<Map> resultRecords = new ArrayList<>();
            Map map = null;Map pc = null;
            for(int i=0;records !=null && i<records.size();i++){
                map = records.get(i);
                pc = projectCollectionService.getProjectCollection(String.valueOf(map.get("code")),String.valueOf(loginMember.get("memberCode")));
                if(pc!=null && null!=pc.get("member_code")){
                    map.put("collected",1);
                }else{
                    map.put("collected",0);
                }
                resultRecords.add(map);
            }
            resultData.setRecords(resultRecords);

        }else if("other".equals(type)){
            resultData =  proService.getProjectInfoByMemCodeOrgCode(ipage,params);
        }

        if(null != resultData){
            Map data = new HashMap();
            data.put("list",resultData.getRecords());
            data.put("total",resultData.getTotal());
            data.put("page",resultData.getCurrent());
            return new AjaxResult(AjaxResult.Type.SUCCESS, "", data);
        }
        return AjaxResult.success(resultData);*/
    }

    @PostMapping("/project_template")
    @ResponseBody
    public AjaxResult projectTemplateIndex(@RequestParam Map<String,Object> mmap)
    {
        Map loginMember = getLoginMember();
        mmap.put("memberCode",MapUtils.getString(loginMember,"memberCode"));
        mmap.put("orgCode",MapUtils.getString(loginMember,"organizationCode"));
        return AjaxResult.success(Constant.createPageResultMap(projectTemplateService.getProjectTemplateIndex(Constant.createPage(mmap),mmap)));
    }

    /**
     * 项目管理  基础设置  项目模板  项目模板删除
     * @param mmap
     * @return
     */
    @PostMapping("/project_template/delete")
    @ResponseBody
    public AjaxResult projectTemplateDelete(@RequestParam Map<String,Object> mmap)
    {
        String code = MapUtils.getString(mmap,"code");
        Map projectTempMap = projectTemplateService.getProjectTemplateByCode(code);
        if(!MapUtils.isEmpty(projectTempMap)){
            String projectTempleteCode = MapUtils.getString(projectTempMap,"code");
            Integer projectTempleteId = MapUtils.getInteger(projectTempMap,"id");
            List<Integer> taskStagesTempIds = taskStagesTempleteService.selectIdsByProjectTempleteCode(projectTempleteCode);
            projectTemplateService.deleteProjectTemplateAndTaskStagesTemplage(projectTempleteId,taskStagesTempIds);
            return AjaxResult.success("删除成功");
        }else{
            return AjaxResult.warn("模板编号查询失败");
        }

    }

    /**
     * 项目管理  基础设置  项目模板  项目模板编辑保存
     * @param mmap
     * @return
     */
    @PostMapping("/project_template/edit")
    @ResponseBody
    public AjaxResult projectTemplateEdit(@RequestParam Map<String,Object> mmap)
    {
        String name = MapUtils.getString(mmap,"name");
        String description = MapUtils.getString(mmap,"description");
        String cover = MapUtils.getString(mmap,"cover");
        String code = MapUtils.getString(mmap,"code");

        Map projectTempMap = projectTemplateService.getProjectTemplateByCode(code);
        if(!MapUtils.isEmpty(projectTempMap)){
            ProjectTemplate pt = new ProjectTemplate();
            pt.setId(MapUtils.getInteger(projectTempMap,"id"));
            pt.setName(name);pt.setDescription(description);pt.setCover(cover);
            boolean bo = projectTemplateService.updateById(pt);
            return AjaxResult.success(bo);
        }else{
            return AjaxResult.warn("模板编号查询失败");
        }

    }

    /**
     * 项目管理  基础设置  项目模板  制作项目模板保存
     * @param mmap
     * @return
     */
    @PostMapping("/project_template/save")
    @ResponseBody
    public AjaxResult projectTemplateSave(@RequestParam Map<String,Object> mmap)
    {
        Map loginMember = getLoginMember();
        String name = MapUtils.getString(mmap,"name");
        String description = MapUtils.getString(mmap,"description");
        String cover = MapUtils.getString(mmap,"cover");
        String code = MapUtils.getString(mmap,"code");
        ProjectTemplate pt = new ProjectTemplate();
        pt.setName(name);pt.setDescription(description);pt.setCover(cover);pt.setCode(CommUtils.getUUID());
        pt.setCreate_time(DateUtil.formatDateTime(new Date()));
        pt.setMember_code(MapUtils.getString(loginMember,"memberCode"));
        pt.setOrganization_code(MapUtils.getString(loginMember,"organizationCode"));
        TaskStagesTemplete tst1 = new TaskStagesTemplete();
        TaskStagesTemplete tst2 = new TaskStagesTemplete();
        TaskStagesTemplete tst3 = new TaskStagesTemplete();
        List<TaskStagesTemplete> listTst = new ArrayList();
        tst1.setCode(CommUtils.getUUID());tst1.setCreate_time(DateUtil.formatDateTime(new Date()));tst1.setName("待处理");
        tst1.setProject_template_code(pt.getCode());tst1.setSort(0);listTst.add(tst1);
        tst2.setCode(CommUtils.getUUID());tst2.setCreate_time(DateUtil.formatDateTime(new Date()));tst2.setName("进行中");
        tst2.setProject_template_code(pt.getCode());tst2.setSort(0);listTst.add(tst2);
        tst3.setCode(CommUtils.getUUID());tst3.setCreate_time(DateUtil.formatDateTime(new Date()));tst3.setName("已完成");
        tst3.setProject_template_code(pt.getCode());tst3.setSort(0);listTst.add(tst3);

        //boolean bo = projectTemplateService.save(pt);
        //boolean bo1 = taskStagesTempleteService.saveBatch(listTst);
        try{
            projectTemplateService.saveProjectTemplateAndTaskStagesTemplage(pt,listTst);
            return AjaxResult.success(true);
        }catch (Exception e){
            return AjaxResult.error(e.getMessage());
        }

    }

    /**
     * 创建新项目->保存
     * @param mmap
     * @return
     */
    @PostMapping("project/save")
    @ResponseBody
    public AjaxResult saveProject(@RequestParam Map<String,Object> mmap)
    {
        Map loginMember = getLoginMember();
        Project project = new Project();
        project.setName(String.valueOf(mmap.get("name")));
        project.setDescription(String.valueOf(mmap.get("description")));
        project.setTemplate_code(String.valueOf(mmap.get("templateCode")));
        project.setAccess_control_type("open");
        project.setCreate_time(DateUtil.formatDateTime(new Date()));
        project.setOrganization_code(MapUtils.getString(loginMember,"organizationCode"));
        project.setTask_board_theme("simple");
        project.setCode(CommUtils.getUUID());
        //project = proService.saveProject(project);
        return AjaxResult.success(proService.saveProject(project,loginMember));
    }
}
