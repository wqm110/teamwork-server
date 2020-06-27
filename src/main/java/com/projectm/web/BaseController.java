package com.projectm.web;

import com.framework.common.AjaxResult;
import com.framework.common.ResultCode;
import com.framework.common.exception.CustomException;
import com.framework.common.utils.ServletUtils;
import com.framework.security.util.UserUtil;
import com.projectm.common.DateUtils;
import com.projectm.login.entity.LoginUser;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BaseController {
    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(DateUtils.parseDate(text));
            }
        });
    }


    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected AjaxResult toAjax(int rows) {
        return rows > 0 ? success() : error();
    }

    /**
     * 响应返回结果
     *
     * @param result 结果
     * @return 操作结果
     */
    protected AjaxResult toAjax(boolean result) {
        return result ? success() : error();
    }

    /**
     * 返回成功
     */
    public AjaxResult success() {
        return AjaxResult.success();
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error() {
        return AjaxResult.error();
    }

    /**
     * 返回成功消息
     */
    public AjaxResult success(String message) {
        return AjaxResult.success(message);
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error(String message) {
        return AjaxResult.error(message);
    }


    public Map getLoginMember() {
        LoginUser loginUser = UserUtil.getLoginUser();
        Map<String, String> member = new HashMap<>(4);
        if (loginUser != null) {
            member.put("memberCode", loginUser.getUser().getCode());
            member.put("organizationCode", ServletUtils.getHeaderParam("organizationCode"));
        }
        //member.put("departmentCode","6v7be19pwman2fird04gqu53");
        //member.put("memberCountCode","6v7be19pwman2fird04gqu11");
        return member;
    }

    /**
     * 通用下载请求
     *
     * @param filePathName 文件路径和文件名称
     * @param delete 是否删除
     */
    /*@GetMapping("/common/download")
    public void fileDownload(String filePathName,String realFileName, Boolean delete)
    {
        try
        {
            if (!FileUtils.isValidFilename(filePathName))
            {
                throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", filePathName));
            }
            String filePath = MProjectConfig.getDownloadPath() + filePathName;

            ServletUtils.getResponse().setCharacterEncoding("utf-8");
            ServletUtils.getResponse().setContentType("multipart/form-data");
            ServletUtils.getResponse().setHeader("Content-Disposition",
                    "attachment;fileName=" + FileUtils.setFileDownloadHeader(ServletUtils.getRequest(), realFileName));
            FileUtils.writeBytes(filePath, ServletUtils.getResponse().getOutputStream());
            if (delete)
            {
                FileUtils.deleteFile(filePath);
            }
        }
        catch (Exception e)
        {

        }
    }*/
}
