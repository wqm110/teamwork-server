package com.projectm.login.controller;

import com.alibaba.fastjson.JSONObject;

import com.framework.common.AjaxResult;
import com.projectm.login.service.LoginService;
import com.projectm.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private LoginService loginService;

    /**
     * 登录方法
     *
     * @param username 用户名
     * @param password 密码
     * @return 结果
     */
    @PostMapping("/teamwork/login")
    public AjaxResult appLogin(String account, String password)
    {
        JSONObject jsonObject = JSONObject.parseObject("{'member':{'id':582,'account':'123456','password':'e10adc3949ba59abbe56e057f20f883e','name':'vilson','mobile':'18681140825','realname':'juli','create_time':null,'status':1,'last_login_time':'2020-05-23 21:13:47','sex':'','avatar':'http://127.0.0.1:8888/static/upload/member/avatar/75723f6176994a9e967b424fd6cdd52b.jpg','idcard':'','province':0,'city':0,'area':0,'address':null,'description':'qqqqqqqqqqq','email':'545522390@qq.com','code':'6v7be19pwman2fird04gqu53','dingtalk_openid':null,'dingtalk_unionid':null,'dingtalk_userid':null,'account_id':21,'is_owner':1,'authorize':'4','position':'资深工程师','department':'','nodes':['project/account/index','project/account/read','project/auth/index','project/department/index','project/department/read','project/department_member/index','project/department_member/searchinvitemember','project/file/index','project/file/read','project/file/uploadfiles','project/index/index','project/index','project/index/changecurrentorganization','project/index/systemconfig','project/index/info','project/index/editpersonal','project/index/editpassword','project/index/uploadimg','project/index/uploadavatar','project/menu/menu','project/node/index','project/node/alllist','project/notify/index','project/notify','project/notify/noreads','project/notify/setreadied','project/notify/batchdel','project/notify/read','project/notify/delete','project/organization/index','project/organization','project/organization/save','project/organization/read','project/organization/edit','project/organization/delete','project/project/index','project/project/selflist','project/project/save','project/project/read','project/project/getlogbyselfproject','project/project/quit','project/project_collect/collect','project/project_collect','project/project_features/index','project/project_member/index','project/project_template/index','project/project_version/index','project/project_version/read','project/task/index','project/task/datetotalforproject','project/task/selflist','project/task/read','project/task/save','project/task/taskdone','project/task/assigntask','project/task/sort','project/task/createcomment','project/task/setprivate','project/task/like','project/task/tasktotags','project/task/settag','project/task/star','project/task/tasklog','project/task_member/index','project/task_member/searchinvitemember','project/task_stages/index','project/task_stages/tasks','project/task_stages/sort','project/task_stages_template/index','project/task_tag/index','project/task_tag','project/task_tag/save','project/task_tag/edit','project/task_tag/delete','project/task_workflow/index','project/account/auth','project/account/add','project/account/edit','project/account/del','project/account/forbid','project/account/resume','project/account']},'tokenList':{'accessToken':'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIiLCJhdWQiOiIiLCJpYXQiOjE1OTA0MjE2NTAsIm5iZiI6MTU5MDQyMTY1MCwiZGF0YSI6eyJjb2RlIjoiNnY3YmUxOXB3bWFuMmZpcmQwNGdxdTUzIn0sInNjb3BlcyI6ImFjY2VzcyIsImV4cCI6MTU5MTAyNjQ1MH0.z9iWbXAycLL9iWB9tjTotexu4XV8_51gm-9L3W1TMqc','refreshToken':'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIiLCJhdWQiOiIiLCJpYXQiOjE1OTA0MjE2NTAsIm5iZiI6MTU5MDQyMTY1MCwiZGF0YSI6eyJjb2RlIjoiNnY3YmUxOXB3bWFuMmZpcmQwNGdxdTUzIn0sInNjb3BlcyI6InJlZnJlc2giLCJleHAiOjE1OTEwMjY0NTB9.IXhm3WpnrKNRb3x5c_bLexSfrnzG50-7l16hPMNlK2k','tokenType':'bearer','accessTokenExp':1591026450},'organizationList':[{'id':1,'name':'vilson的个人项目哈哈','avatar':null,'description':null,'owner_code':'6v7be19pwman2fird04gqu53','create_time':'2018-10-12','personal':1,'code':'6v7be19pwman2fird04gqu53','address':'详细地址','province':0,'city':0,'area':0}]}");
        AjaxResult ajax = AjaxResult.success("",jsonObject);//loginService.login(account,password);
//return ajax;

        return loginService.login(account,password);
    }
}
