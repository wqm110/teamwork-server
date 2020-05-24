package com.projectm.org.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.projectm.common.AjaxResult;
import com.projectm.common.CommUtils;
import com.projectm.common.DateUtil;
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
@RequestMapping("/api/project")
public class OrgController   extends BaseController {

    @Autowired
    private OrgService orgService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private DepartmentService departmentService;

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

        Department dep = new Department();
        dep.setCode(CommUtils.getUUID());dep.setCreate_time(DateUtil.formatDateTime(new Date()));
        dep.setName(name);dep.setOrganization_code(organizationCode);dep.setPcode(parentDepartmentCode);
        return AjaxResult.success(departmentService.save(dep));

    }


    @PostMapping("/department")
    @ResponseBody
    public AjaxResult getDepartment(@RequestParam Map<String,Object> mmap)
    {
        Map loginMember = getLoginMember();
        String organizationCode = MapUtils.getString(loginMember,"organizationCode");
        String pCode = MapUtils.getString(mmap,"pcode","");
        Integer page = MapUtils.getInteger(mmap,"page",1);
        Integer pageSize = MapUtils.getInteger(mmap,"pageSize",10);
        IPage<Map> ipage = new Page();
        ipage.setSize(pageSize);
        ipage.setCurrent(page);
        IPage<Map> deptData = departmentService.getDepartmentByOrgCodeAndPCode(ipage,organizationCode,pCode);

        Map resultData = new HashMap();
        resultData.put("list",deptData.getRecords());
        resultData.put("total",deptData.getTotal());
        resultData.put("page",deptData.getCurrent());
        return new AjaxResult(AjaxResult.Type.SUCCESS, "", resultData);

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
        List<Map> resultData = orgService._getOrgList(null);
        return AjaxResult.success(resultData);
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
        Integer page = MapUtils.getInteger(mmap,"page",1);
        Integer pageSize = MapUtils.getInteger(mmap,"pageSize",10);
        IPage<Map> ipage = new Page();
        ipage.setSize(pageSize);
        ipage.setCurrent(page);
        IPage<Map> orgData = organizationService.getAllOrganizationByMemberCode(ipage,memberCode);

        Map resultData = new HashMap();
        resultData.put("list",orgData.getRecords());
        resultData.put("total",orgData.getTotal());
        resultData.put("page",orgData.getCurrent());
        return new AjaxResult(AjaxResult.Type.SUCCESS, "", resultData);
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
