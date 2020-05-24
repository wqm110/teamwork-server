package com.projectm.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.projectm.member.domain.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Map;

@SuppressWarnings("rawtypes")
@Repository
@Mapper
public interface MemberMapper extends BaseMapper<Member> {

    @Select("SELECT * FROM pear_member WHERE code = #{memberCode}")
    Map selectMemberByCode(String memberCode);
}



