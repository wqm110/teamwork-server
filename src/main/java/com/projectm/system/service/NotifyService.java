package com.projectm.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.projectm.system.domain.Notify;
import com.projectm.system.mapper.NotifyMapper;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class NotifyService extends ServiceImpl<NotifyMapper, Notify> {

    //获取用户所在的所有组织信息
    public IPage<Map> getAllOrganizationByMemberCode(IPage<Map> page, Map params){
        return baseMapper.getAllNotifyByParams(page,params);
    }


}
