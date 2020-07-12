package com.framework.security.handler;

import com.framework.common.ResultJson;
import com.framework.common.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @version V1.0
 * @program: teamwork
 * @package: com.framework.security.handler
 * @description: 注销
 * @author: lzd
 * @create: 2020-06-26 13:24
 **/
@Slf4j
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.info("身份注销{}", true);
        ServletUtils.renderString(response, ResultJson.ok().toString());
    }
}
