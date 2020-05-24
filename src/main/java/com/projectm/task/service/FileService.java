package com.projectm.task.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.projectm.task.domain.File;
import com.projectm.task.mapper.FileMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FileService  extends ServiceImpl<FileMapper, File> {

    public Map getFileByCode(String fileCode){
        return baseMapper.selectFileByCode(fileCode);
    }

    public IPage<Map> gettFileByProjectCodeAndDelete(IPage<Map> page, Map params){
        return baseMapper.selectFileByProjectCodeAndDelete(page,params);
    }
}
