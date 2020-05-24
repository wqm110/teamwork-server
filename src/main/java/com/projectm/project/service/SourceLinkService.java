package com.projectm.project.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.projectm.project.domain.SourceLink;
import com.projectm.project.mapper.SourceLinkMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SourceLinkService  extends ServiceImpl<SourceLinkMapper, SourceLink> {

    public List<Map> getSourceLinkByLinkCodeAndType(String linkCode, String linkType){
        return baseMapper.selectSourceLinkByLinkCodeAndType(linkCode,linkType);
    }
}
