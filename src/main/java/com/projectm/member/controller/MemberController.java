package com.projectm.member.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.projectm.common.AjaxResult;
import com.projectm.member.domain.Member;
import com.projectm.member.domain.MemberAccount;
import com.projectm.member.domain.ProjectMember;
import com.projectm.member.service.MemberAccountService;
import com.projectm.member.service.MemberService;
import com.projectm.member.service.ProjectMemberService;
import com.projectm.org.service.DepartmentService;
import com.projectm.project.service.ProjectAuthService;
import com.projectm.web.BaseController;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/project")
public class MemberController  extends BaseController {
    @Autowired
    private ProjectMemberService projectMemberService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberAccountService memberAccountService;

    @Autowired
    private ProjectAuthService projectAuthService;

    @Autowired
    private DepartmentService departmentService;

    /**
     * 账号停用
     * @param mmap
     * @return
     */
    @PostMapping("/index/editPersonal")
    @ResponseBody
    public AjaxResult indexEditPersonal(@RequestParam Map<String,Object> mmap) {
        String name = MapUtils.getString(mmap,"name");
        String description = MapUtils.getString(mmap,"description");
        String code = MapUtils.getString(mmap,"code");
        String avatar = MapUtils.getString(mmap,"avatar");
        Map memberMap = memberService.getMemberMapByCode(code);
        Map memberAccountMap = memberAccountService.getMemberAccountByMemCode(code);
        Member member = new Member();
        MemberAccount memberAccount = new MemberAccount();
        member.setId(MapUtils.getInteger(memberMap,"id"));member.setName(name);member.setDescription(description);
        member.setAvatar(avatar);
        memberAccount.setId(MapUtils.getInteger(memberAccountMap,"id"));
        memberAccount.setAvatar(avatar);
        return AjaxResult.success("基本信息更新成功",memberService.updateMemberAccountAndMember(memberAccount,member));
    }

    /**
     * 账号停用
     * @param mmap
     * @return
     */
    @PostMapping("/account/forbid")
    @ResponseBody
    public AjaxResult setAccountForbid(@RequestParam Map<String,Object> mmap) {
        String accountCode = MapUtils.getString(mmap,"accountCode");
        Integer status = MapUtils.getInteger(mmap,"status");

        MemberAccount ma = new MemberAccount();
        Map memberAccountMap = memberAccountService.getMemberAccountByCode(accountCode);
        if(MapUtils.isNotEmpty(memberAccountMap)){
            ma.setId(MapUtils.getInteger(memberAccountMap,"id"));
            ma.setStatus(status);
            AjaxResult.success(memberAccountService.updateById(ma));
        }
        return AjaxResult.success();
    }

    /**
     * 账号启用
     * @param mmap
     * @return
     */
    @PostMapping("/account/resume")
    @ResponseBody
    public AjaxResult setAccountResume(@RequestParam Map<String,Object> mmap) {
        String accountCode = MapUtils.getString(mmap,"accountCode");
        Integer status = MapUtils.getInteger(mmap,"status");

        MemberAccount ma = new MemberAccount();
        Map memberAccountMap = memberAccountService.getMemberAccountByCode(accountCode);
        if(MapUtils.isNotEmpty(memberAccountMap)){
            ma.setId(MapUtils.getInteger(memberAccountMap,"id"));
            ma.setStatus(status);
            AjaxResult.success(memberAccountService.updateById(ma));
        }
        return AjaxResult.success();
    }

    /**
     * 账号删除
     * @param mmap
     * @return
     */
    @PostMapping("/account/del")
    @ResponseBody
    public AjaxResult accountDel(@RequestParam Map<String,Object> mmap) {
        String accountCode = MapUtils.getString(mmap,"accountCode");
        Map loginMember = getLoginMember();

        memberAccountService.memberAccountDel(accountCode,MapUtils.getString(loginMember,"organizationCode"));
        return AjaxResult.success();
    }

    /**
     * 账号编辑
     * @param mmap
     * @return
     */
    @PostMapping("/account/edit")
    @ResponseBody
    public AjaxResult accountEdit(@RequestParam Map<String,Object> mmap) {
        String name = MapUtils.getString(mmap,"name");
        String mobile = MapUtils.getString(mmap,"mobile");
        String email = MapUtils.getString(mmap,"email");
        String description = MapUtils.getString(mmap,"description");
        String code = MapUtils.getString(mmap,"code");

        Map memAccMap = memberAccountService.getMemberAccountByCode(code);
        if(MapUtils.isNotEmpty(memAccMap)){
            MemberAccount ma = new MemberAccount();
            ma.setId(MapUtils.getInteger(memAccMap,"id"));
            ma.setName(name);ma.setMobile(mobile);ma.setEmail(email);ma.setDescription(description);
           return AjaxResult.success(memberAccountService.updateById(ma));
        }
        return AjaxResult.success();
    }

    /**
     * 账号编辑
     * @param mmap
     * @return
     */
    @PostMapping("/account/auth")
    @ResponseBody
    public AjaxResult accountAuth(@RequestParam Map<String,Object> mmap) {
        String auth = MapUtils.getString(mmap,"auth");
        Integer id = MapUtils.getInteger(mmap,"id");

        MemberAccount ma = new MemberAccount();
        ma.setId(id);
        ma.setAuthorize(auth);
        return AjaxResult.success(memberAccountService.updateById(ma));
    }

    /**
     *首页 > 成员 >vilson >任务安排
     * @param mmap
     * @return
     */
    @PostMapping("/account/read")
    @ResponseBody
    public AjaxResult accountRead(@RequestParam Map<String,Object> mmap) {
        String code = MapUtils.getString(mmap,"code");
        Map memberAccountMap= memberAccountService.getMemberAccountByCode(code);
        String[] depCodeArr ,authorizeArr= null;
        Map depMap = null;
        String  depCodes = MapUtils.getString(memberAccountMap,"department_code");
        String authorize = MapUtils.getString(memberAccountMap,"authorize");
        depCodeArr = depCodes.split(",");
        authorizeArr = authorize.split(",");
        Integer status = MapUtils.getInteger(memberAccountMap,"status",-1);
        if(status == 0){
            memberAccountMap.put("statusText","禁用");
        }else if(status == 1){
            memberAccountMap.put("statusText","使用中");
        }
        String depName = "";
        if(null != depCodeArr) {
            for (String deptCode : depCodeArr) {
                depMap = departmentService.getDepartmentByCode(deptCode);
                if (MapUtils.isNotEmpty(depMap)) {
                    depName = ("".equals(depName)) ? MapUtils.getString(depMap,"name") : (depName + "-" + MapUtils.getString(depMap,"name") );
                }
            }
        }
        memberAccountMap.put("departments",depName);
        List authorizeArrList  = new ArrayList();
        if(null != authorizeArr){
            Map authMap = null;
            for(int i=0;i<authorizeArr.length;i++){
                authMap = new HashMap();
                authMap.put(i,authorizeArr[i]);
                authorizeArrList.add(authMap);
            }
        }
        memberAccountMap.put("authorizeArr",authorizeArrList);
        return AjaxResult.success(memberAccountMap);
    }

    /**
     *
     * @param mmap
     * @return
     */
    @PostMapping("/account")
    @ResponseBody
    public AjaxResult getAccount(@RequestParam Map<String,Object> mmap) {

        String departmentCode = MapUtils.getString(mmap, "departmentCode");
        Map loginMember = getLoginMember();
        String orgCode = MapUtils.getString(loginMember,"organizationCode");
        Integer page = MapUtils.getInteger(mmap, "page",1);
        Integer pageSize = MapUtils.getInteger(mmap, "pageSize",20);
        Integer searchType = MapUtils.getInteger(mmap, "searchType",-1);
        IPage<Map> ipage = new Page();
        IPage<Map> resultPage = null;
        ipage.setSize(pageSize);
        ipage.setCurrent(page);
        Map params = new HashMap();
        Map resultData = new HashMap();
        params.put("orgCode",orgCode);
        switch (searchType){
            case 1:{
                params.put("status",1);
                resultPage =  memberAccountService.getMemberAccountByOrgCodeAndStatus(ipage,params);
                break;
            }
            case 2:{
                params.put("depCode","");
                resultPage =  memberAccountService.getMemberAccountByOrgCodeStatusDeptCode(ipage,params);
                break;
            }
            case 3:{
                params.put("status",0);
                resultPage =  memberAccountService.getMemberAccountByOrgCodeAndStatus(ipage,params);
                break;
            }
            case 4:{
                params.put("status",1);
                params.put("depCode",departmentCode);
                resultPage =  memberAccountService.getMemberAccountByOrgCodeStatusDeptCode(ipage,params);
                /*resultData.put("list",resultPage.getRecords());
                resultData.put("total",resultPage.getTotal());
                resultData.put("page",resultPage.getCurrent());
                resultData.put("authList",projectAuthService.getProjectAuthByStatusAndOrgCode("1",orgCode));*/
                break;
            }
            default:{
                params.put("status",1);
                resultPage =  memberAccountService.getMemberAccountByOrgCodeAndStatus(ipage,params);
            }
        }

        List<Map> records = resultPage.getRecords();
        List<Map> resultList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(records)){
            String depCodes,authorize = null;
            String[] depCodeArr ,authorizeArr= null;
            Map depMap = null;
            for(Map map:records){
                depCodes = MapUtils.getString(map,"department_code");
                authorize = MapUtils.getString(map,"authorize");

                map.put("membar_account_code",MapUtils.getString(map,"code"));
                depCodeArr = depCodes.split(",");
                authorizeArr = authorize.split(",");
                Integer status = MapUtils.getInteger(map,"status",-1);
                if(status == 0){
                    map.put("statusText","禁用");
                }else if(status == 1){
                    map.put("statusText","使用中");
                }
                String depName = "";
                if(null != depCodeArr) {
                    for (String deptCode : depCodeArr) {
                        depMap = departmentService.getDepartmentByCode(deptCode);
                        if (MapUtils.isNotEmpty(depMap)) {
                            depName = ("".equals(depName)) ? MapUtils.getString(depMap,"name") : (depName + "-" + MapUtils.getString(depMap,"name") );
                        }
                    }
                }
                map.put("departments",depName);
                List authorizeArrList  = new ArrayList();
                if(null != authorizeArr){
                    Map authMap = null;
                    for(int i=0;i<authorizeArr.length;i++){
                        authMap = new HashMap();
                        authMap.put(i,authorizeArr[i]);
                        authorizeArrList.add(authMap);
                    }
                }
                map.put("authorizeArr",authorizeArrList);
                resultList.add(map);
            }
        }
        resultData.put("list",resultList);
        resultData.put("total",resultPage.getTotal());
        resultData.put("page",resultPage.getCurrent());
        List<Map> listProjectAuth = projectAuthService.getProjectAuthByStatusAndOrgCode("1",orgCode);
        List<Map> authList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(listProjectAuth)){
            for(Map map:listProjectAuth){
                String type = MapUtils.getString(map,"type");
                if("admin".equals(type) || "member".equals(type)){
                    map.put("canDelete",0);
                }else{
                    map.put("canDelete",1);
                }
                authList.add(map);
            }
        }
        resultData.put("authList",authList);

        return AjaxResult.success(resultData);
    }

    /**
     * 项目管理	我的项目 邀请新成员 模糊查询
     * @param mmap
     * @return
     */
    @PostMapping("/project_member/searchInviteMember")
    @ResponseBody
    public AjaxResult searchInviteMember(@RequestParam Map<String,Object> mmap) {
        Map loginMember = getLoginMember();
        String projectCode = MapUtils.getString(mmap, "projectCode");
        String orgCode = MapUtils.getString(loginMember,"organizationCode");
        String keyword = MapUtils.getString(mmap,"keyword");

        List<Map> listMemberAccounts = memberAccountService.getMemberCountByOrgCodeAndMemberName(orgCode,keyword);
        List<Map> resultData = new ArrayList<>();
        if(!CollectionUtils.isEmpty(listMemberAccounts)){
            Map tmpMap = null;
            for(Map cm:listMemberAccounts){
                tmpMap = new HashMap();
                tmpMap.put("memberCode",MapUtils.getString(cm, "member_code",""));
                tmpMap.put("status",MapUtils.getString(cm, "status",""));
                tmpMap.put("avatar",MapUtils.getString(cm, "avatar",""));
                tmpMap.put("name",MapUtils.getString(cm, "name",""));
                tmpMap.put("email",MapUtils.getString(cm, "email",""));
                tmpMap.put("joined",projectMemberService.isProjectMember(projectCode,MapUtils.getString(cm, "member_code","")));
                resultData.add(tmpMap);
            }
        }
        return AjaxResult.success(resultData);
    }

    /**
     * 项目管理	我的项目 邀请新成员页面初始化
     * @param mmap
     * @return
     */
    @PostMapping("/project_member/_listForInvite")
    @ResponseBody
    public AjaxResult _listForInvite(@RequestParam Map<String,Object> mmap) {
        Map loginMember = getLoginMember();
        String projectCode = MapUtils.getString(mmap, "projectCode");
        String orgCode = MapUtils.getString(loginMember,"organizationCode");

        List<Map> listMemberAccounts = memberAccountService.getMemberCountByOrgCode(orgCode);
        List<Map> resultData = new ArrayList<>();
        if(!CollectionUtils.isEmpty(listMemberAccounts)){
            Map tmpMap = null;
            for(Map cm:listMemberAccounts){
                tmpMap = new HashMap();
                tmpMap.put("memberCode",MapUtils.getString(cm, "member_code",""));
                tmpMap.put("status",MapUtils.getString(cm, "status",""));
                tmpMap.put("avatar",MapUtils.getString(cm, "avatar",""));
                tmpMap.put("name",MapUtils.getString(cm, "name",""));
                tmpMap.put("email",MapUtils.getString(cm, "email",""));
                tmpMap.put("joined",projectMemberService.isProjectMember(projectCode,MapUtils.getString(cm, "member_code","")));
                resultData.add(tmpMap);
            }
        }
        return AjaxResult.success(resultData);
    }


    /**
     * 项目管理	我的项目 项目设置 任务流转  规则创建页面初始化
     * @param mmap
     * @return
     */
    @PostMapping("/project_member/index")
    @ResponseBody
    public AjaxResult getProject(@RequestParam Map<String,Object> mmap)
    {
        String projectCode = MapUtils.getString(mmap,"projectCode");
        Integer page = MapUtils.getInteger(mmap,"page",1);
        Integer pageSize = MapUtils.getInteger(mmap,"pageSize",100);

        ProjectMember pm = new ProjectMember();
        pm.setCurrent(page);pm.setSize(pageSize);pm.setProject_code(projectCode);

        Map resultData = new HashMap();
        IPage<Map> idata = projectMemberService.getProjectMemberByProjectCode(pm);
        if(null != idata){
            List<Map> list = idata.getRecords();
            List<Map> lrt = new ArrayList();
            Member member = null;Map map = null;
            Map prm = null;
            for(int i=0;i<list.size();i++){
                prm = list.get(i);
                if(null != prm){
                    member = memberService.getMemberByCode(MapUtils.getString(prm,"member_code"));
                    map = new HashMap();
                    map.put("name",member.getName());map.put("avatar",member.getAvatar());
                    map.put("code",member.getCode());map.put("email",member.getEmail());
                    lrt.add(map);
                }
            }
            resultData.put("list",lrt);
            resultData.put("total",idata.getTotal());
            resultData.put("page",idata.getCurrent());
            return new AjaxResult(AjaxResult.Type.SUCCESS, "", resultData);
        }
        return AjaxResult.success(resultData);
    }
}
