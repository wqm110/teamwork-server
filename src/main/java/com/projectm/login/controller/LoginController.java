package com.projectm.login.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.framework.common.AjaxResult;
import com.framework.common.constant.Constants;
import com.framework.common.utils.security.Md5Utils;
import com.framework.security.util.RedisCache;
import com.framework.security.util.TokenUtil;
import com.projectm.common.Constant;
import com.projectm.login.entity.LoginUser;
import com.projectm.login.service.LoginService;
import com.projectm.member.domain.MemberAccount;
import com.projectm.member.service.MemberAccountService;
import com.projectm.member.service.MemberService;
import com.projectm.org.domain.Organization;
import com.projectm.org.service.OrganizationService;
import com.projectm.project.domain.ProjectAuthNode;
import com.projectm.project.mapper.ProjectAuthNodeMapper;
import com.projectm.project.service.ProjectAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import sun.rmi.runtime.NewThreadAction;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class LoginController {

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private LoginService loginService;

    @Value("${jwt.expiration}")
    private int expireTime;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private MemberAccountService memberAccountService;

    @Autowired
    private ProjectAuthNodeMapper projectAuthNodeMapper;

    /**
     * 登录方法
     *
     * @param account  用户名
     * @param password 密码
     * @return 结果
     */
    @PostMapping("/teamwork/login")
    @ResponseBody
    public AjaxResult appLogin(String account, String password) {
        //验证码验证

        //用户验证
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(account, password));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String userCode = loginUser.getUser().getCode();
        List<MemberAccount> list = memberAccountService.lambdaQuery().eq(MemberAccount::getMember_code, userCode).list();
        loginUser.getUser().setMemberAccountList(list);
        Set<String> authSet = list.stream().map(MemberAccount::getAuthorize).collect(Collectors.toSet());
        List<ProjectAuthNode> projectAuthNodeList = projectAuthNodeMapper.selectList(Wrappers.<ProjectAuthNode>lambdaQuery().in(ProjectAuthNode::getAuth, authSet));
        list.forEach(memberAccount -> {
            List<String> nodeList = projectAuthNodeList.parallelStream().filter(auth -> Objects.equals(auth.getAuth().toString(), memberAccount.getAuthorize()))
                    .map(ProjectAuthNode::getNode).collect(Collectors.toList());
            memberAccount.setNodeList(nodeList);
        });
        // 生成token
        redisCache.setCacheObject(Constants.LOGIN_USER_KEY + userCode, loginUser);
        Calendar instance = Calendar.getInstance();
        Date issDate = instance.getTime();
        instance.add(Calendar.HOUR, expireTime);
        Date expireDate = instance.getTime();
        String token = tokenUtil.createToken(issDate, expireDate, userCode);
        Map<String, Object> tokenList = new HashMap<>(10);
        Calendar.getInstance();
        tokenList.put("accessToken", token);
        tokenList.put("refreshToken", token);
        tokenList.put("tokenType", Constants.TOKEN_PREFIX.trim());
        tokenList.put("accessTokenExp", expireDate.getTime());
        Set<String> collect = list.stream().map(MemberAccount::getOrganization_code).collect(Collectors.toSet());
        List<Organization> organizationList = organizationService.lambdaQuery().in(Organization::getCode, collect).list();
        Map<String, Object> resultMap = new HashMap<>(8);
        resultMap.put("member", loginUser.getUser());
        resultMap.put("organizationList", organizationList);
        resultMap.put("tokenList", tokenList);
        return AjaxResult.success(resultMap);
    }

    public static void main(String[] args) {
        String hash = Md5Utils.hash("e10adc3949ba59abbe56e057f20f883e");

        System.out.println(hash);
    }
}
