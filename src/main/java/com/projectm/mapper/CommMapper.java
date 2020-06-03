package com.projectm.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
@Repository
@Mapper
public interface CommMapper {
    List<Map> customQueryItem(String sqlContent);
    Map customQueryItemOne(String sqlContent);
    IPage<Map> customQueryItem(IPage<Map> page, String sqlContent);
}
