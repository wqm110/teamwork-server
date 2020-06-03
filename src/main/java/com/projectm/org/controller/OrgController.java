package com.projectm.org.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.framework.common.AjaxResult;
import com.framework.common.utils.ServletUtils;
import com.framework.common.utils.StringUtils;
import com.projectm.common.CommUtils;
import com.projectm.common.Constant;
import com.projectm.common.DateUtil;
import com.projectm.member.service.MemberService;
import com.projectm.org.domain.Department;
import com.projectm.org.domain.Organization;
import com.projectm.org.service.DepartmentService;
import com.projectm.org.service.OrgService;
import com.projectm.org.service.OrganizationService;
import com.projectm.web.BaseController;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/project")
public class OrgController   extends BaseController {

    @Autowired
    private OrgService orgService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private MemberService memberService;

    /**
     * 新增保存部门
     * @param mmap
     * @return
     */
    @PostMapping("/department/save")
    @ResponseBody
    public AjaxResult departmentSave(@RequestParam Map<String,Object> mmap)
    {
        Map loginMember = getLoginMember();
        String organizationCode = MapUtils.getString(loginMember,"organizationCode");
        String departmentCode = MapUtils.getString(mmap,"departmentCode");
        String parentDepartmentCode = MapUtils.getString(mmap,"parentDepartmentCode");
        String name = MapUtils.getString(mmap,"name");
        if(StringUtils.isEmpty(name)){
            return AjaxResult.warn("请填写部门名称");
        }
        Department dep = Department.builder().code(CommUtils.getUUID()).create_time(DateUtil.formatDateTime(new Date()))
                .name(name).organization_code(organizationCode).pcode(parentDepartmentCode).build();
        boolean result = departmentService.save(dep);
        if(result){
            return AjaxResult.success(dep);
        }
        return AjaxResult.warn("操作失败，请稍候再试！");

    }


    @PostMapping("/department")
    @ResponseBody
    public AjaxResult getDepartment(@RequestParam Map<String,Object> mmap)
    {
        Map loginMember = getLoginMember();
        String organizationCode = MapUtils.getString(loginMember,"organizationCode");
        String pCode = MapUtils.getString(mmap,"pcode","");
        IPage<Map> ipage = departmentService.getDepartmentByOrgCodeAndPCode(Constant.createPage(mmap),organizationCode,pCode);
        return AjaxResult.success(Constant.createPageResultMap(ipage));
    }

    @PostMapping("/department/read")
    @ResponseBody
    public AjaxResult departmentRead(@RequestParam Map<String,Object> mmap)
    {
        String departmentCode = MapUtils.getString(mmap,"departmentCode");
        Map deptMap = departmentService.getDepartmentByCode(departmentCode);

        Map resultData = CommUtils.getMapField(deptMap,new String[]{ "code","organization_code","name","sort","pcode","icon","create_time","path"});

        return new AjaxResult(AjaxResult.Type.SUCCESS, "", resultData);

    }
    @PostMapping("/department/delete")
    @ResponseBody
    public AjaxResult departmentDelete(@RequestParam Map<String,Object> mmap)
    {
        String departmentCode = MapUtils.getString(mmap,"departmentCode");
        Integer resultData = departmentService.delDepartmentByCodes(new ArrayList<String>(){{add(departmentCode);}});
        return new AjaxResult(AjaxResult.Type.SUCCESS, "", resultData);

    }
    @PostMapping("/department/edit")
    @ResponseBody
    public AjaxResult departmentEdit(@RequestParam Map<String,Object> mmap)
    {
        String departmentCode = MapUtils.getString(mmap,"departmentCode");
        String parentDepartmentCode = MapUtils.getString(mmap,"parentDepartmentCode");
        String name = MapUtils.getString(mmap,"name");
        Map depMap = departmentService.getDepartmentByCode(departmentCode);
        Department dep = new Department();
        dep.setId(MapUtils.getInteger(depMap,"id"));
        dep.setName(name);
        boolean resultData = departmentService.updateById(dep);
        return new AjaxResult(AjaxResult.Type.SUCCESS, "", resultData);
    }


    @PostMapping("/organization/_getOrgList")
    @ResponseBody
    public AjaxResult getOrgList()
    {
        Map memberMap = (Map)ServletUtils.getRequest().getSession().getAttribute(Constant.CURRENT_USER);
        return AjaxResult.success(memberService.getOrgList(MapUtils.getString(memberMap,"code")));
    }

    /**
     * 项目管理   项目列表    我的组织页面初始化
     * @param mmap
     * @return
     */
    @PostMapping("/organization")
    @ResponseBody
    public AjaxResult getOrganization(@RequestParam Map<String,Object> mmap)
    {
        Map loginMember = getLoginMember();
        String memberCode = MapUtils.getString(loginMember,"memberCode");
        IPage<Map> ipage = Constant.createPage(mmap);
        IPage<Map> orgData = organizationService.getAllOrganizationByMemberCode(ipage,memberCode);

        return new AjaxResult(AjaxResult.Type.SUCCESS, "", Constant.createPageResultMap(orgData));
    }

    /**
     * 项目管理   项目列表    我的组织页面初始化
     * @param mmap
     * @return
     */
    @PostMapping("/organization/edit")
    @ResponseBody
    public AjaxResult orgEdit(@RequestParam Map<String,Object> mmap)
    {

        String name = MapUtils.getString(mmap,"name","");
        String address = MapUtils.getString(mmap,"address","");
        Integer areas = MapUtils.getInteger(mmap,"areas",-1);
        String organizationCode = MapUtils.getString(mmap,"organizationCode","");

        Organization org = organizationService.getOrganizationByCode(organizationCode);
        if(!ObjectUtils.isEmpty(org)&& !ObjectUtils.isEmpty(org.getId())){
            org.setName(name);
            org.setAddress(address);
            if(areas != -1){
                org.setArea(areas);
            }
            boolean updateResult = organizationService.updateById(org);
            return new AjaxResult(AjaxResult.Type.SUCCESS, "", updateResult);
        }
        return new AjaxResult(AjaxResult.Type.SUCCESS, "组织不存在",false);
    }


}
