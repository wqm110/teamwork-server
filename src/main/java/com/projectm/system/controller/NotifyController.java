package com.projectm.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.framework.common.AjaxResult;
import com.projectm.system.service.NotifyService;
import com.projectm.web.BaseController;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/project")
public class NotifyController   extends BaseController {
    @Autowired
    private NotifyService notifyService;
    /**
     * 项目管理	消息提醒 页面初始化
     * @param mmap
     * @return
     */
    @PostMapping("/notify/index")
    @ResponseBody
    public AjaxResult notify(@RequestParam Map<String,Object> mmap)  throws Exception
    {
        Map loginMember = getLoginMember();
        Integer pageSize = MapUtils.getInteger(mmap,"pageSize",1000);
        Integer page = MapUtils.getInteger(mmap,"page",1);
        String type = MapUtils.getString(mmap,"type",null);
        String title = MapUtils.getString(mmap,"title",null);
        String date = MapUtils.getString(mmap,"date",null);

        IPage<Map> ipage = new Page();
        ipage.setSize(pageSize);
        ipage.setCurrent(page);
        Map params = new HashMap();
        params.put("to",MapUtils.getString(loginMember,"memberCode",null));
        params.put("terminal","project");

        IPage<Map> resultData = notifyService.getAllOrganizationByMemberCode(ipage,params);
        Map data = new HashMap();
        if(null == resultData){
            resultData = new Page<>();
        }
        data.put("list",resultData.getRecords());
        data.put("total",resultData.getTotal());
        data.put("page",resultData.getCurrent());
        return new AjaxResult(AjaxResult.Type.SUCCESS, "", data);
    }

    @PostMapping("/notify/noReads")
    @ResponseBody
    public AjaxResult getNoReads(){
        return AjaxResult.success(notifyService.getNoReads());
    }
}
