package com.projectm.org.service;

import com.projectm.org.mapper.OrgMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrgService{

    @Autowired
    private OrgMapper orgMapper;

    public List<Map> selectOrgByMemCode(Map params) {
        return orgMapper.selectOrgByMemCode(params);
    }

    public List<Map> _getOrgList(Map params){
        return orgMapper._getOrgList(params);
    }
}
