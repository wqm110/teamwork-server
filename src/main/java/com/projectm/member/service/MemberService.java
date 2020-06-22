package com.projectm.member.service;


import cn.hutool.poi.excel.sax.Excel07SaxReader;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.framework.common.utils.StringUtils;
import com.framework.common.utils.security.Md5Utils;
import com.projectm.common.CommUtils;
import com.projectm.common.ListUtils;
import com.projectm.member.domain.Member;
import com.projectm.member.domain.MemberAccount;
import com.projectm.member.mapper.MemberAccountMapper;
import com.projectm.member.mapper.MemberMapper;
import com.projectm.member.mapper.ProjectMemberMapper;
import com.projectm.org.domain.Organization;
import com.projectm.org.mapper.OrganizationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class MemberService extends ServiceImpl<MemberMapper, Member> {

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    @Autowired
    private MemberAccountMapper memberAccountMapper;

    @Autowired
    OrganizationMapper organizationMapper;

    public List<Map> selectMemberByLoginParam(Map params) {
        return projectMemberMapper.selectMemberByLoginParam(params);
    }

    public List<Map> selectMemberCountByMemberCode(Map params){
        return projectMemberMapper.selectMemberCountByMemberCode(params);
    }
    //根据用户编号，查询用户信息
    public Map getMemberById(String userCode){
        List<Map> listMap = projectMemberMapper.getMemberById(userCode);
        if(null != listMap && listMap.size() > 0){
            return listMap.get(0);
        }
        return null;
    }

    //根据memberCode获取member信息
    public Member getMemberByCode(String memberCode){
        LambdaQueryWrapper<Member> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Member::getCode, memberCode);
        return baseMapper.selectOne(queryWrapper);
    }

    public  void inviteMemberBatch(String memberCodes,String taskCode){
        boolean isAll = false;
        JSONArray memberCodeArray = JSON.parseArray(memberCodes);
        //List<>

        if(memberCodes.indexOf("all") != -1){

        }
    }

    //根据memberCode获取member信息
    public Map getMemberMapByCode(String memberCode){
        return baseMapper.selectMemberByCode(memberCode);
    }

    @Transactional
    public Integer updateMemberAccountAndMember(MemberAccount ma,Member m){
        Integer i1 = baseMapper.updateById(m);
        Integer i2 = memberAccountMapper.updateById(ma);
        return i1+i2;
    }

    public Member getMemberByName(String account){
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        return baseMapper.selectOne(queryWrapper);
    }

    public List<Organization> getOrgList(String memberCode){
        return organizationMapper.selectOrganizationByMemberCode(memberCode);
    }



    @Transactional
    public void uploadFile(String orgCode, InputStream ins) {
        int dataStartRow = 4;
        Excel07SaxReader reader = new Excel07SaxReader(createRowHandler(dataStartRow,orgCode));
        reader.read(ins,0);
    }
    @Autowired
    MemberAccountService memberAccountService;

    private RowHandler createRowHandler(int dataStartRow,String orgCode) {
        return new RowHandler() {
            @Override
            public void handle(int sheetIndex, int rowIndex, List<Object> rowlist) {
                if(rowIndex>=dataStartRow-1){
                    Member member = new Member();
                    member.setName(ListUtils.getValue(rowlist,0,String.class));
                    member.setEmail(ListUtils.getValue(rowlist,1,String.class));
                    if(StringUtils.isEmpty(member.getName())||StringUtils.isEmpty(member.getEmail())){
                        return;
                    }
                    LambdaQueryWrapper<Member> memberWQ = new LambdaQueryWrapper<>();
                    memberWQ.eq(Member::getEmail,member.getEmail());
                    member = baseMapper.selectOne(memberWQ);
                    if(ObjectUtils.isEmpty(member)){
                        member = new Member();
                        member.setName(ListUtils.getValue(rowlist,0,String.class));
                        member.setEmail(ListUtils.getValue(rowlist,1,String.class));
                        member.setDepartment(ListUtils.getValue(rowlist,2,String.class));
                        member.setPosition(ListUtils.getValue(rowlist,3,String.class));
                        member.setMobile(ListUtils.getValue(rowlist,4,String.class));
                        member.setPassword(ListUtils.getValue(rowlist,5,String.class));
                        member.setDescription(ListUtils.getValue(rowlist,6,String.class));
                        member.setCode(CommUtils.getUUID());
                        member.setPassword(Md5Utils.hash(member.getPassword()));
                        member.setAvatar("https://static.vilson.online/cover.png");
                        member.setAccount(member.getName());

                        baseMapper.insert(member);
                        MemberAccount memberAccount = new MemberAccount();
                        memberAccount.setMember_code(member.getCode());
                        memberAccount.setOrganization_code(orgCode);
                        memberAccount.setPosition(member.getPosition());
                        memberAccount.setMobile(member.getMobile());
                        memberAccount.setDescription(member.getDescription());
                        memberAccountService.inviteMember(memberAccount);
                    }else{

                        LambdaQueryWrapper<MemberAccount> memberAcWQ = new LambdaQueryWrapper<>();
                        memberAcWQ.eq(MemberAccount::getMember_code,member.getCode());
                        memberAcWQ.eq(MemberAccount::getOrganization_code,orgCode);
                        MemberAccount ma = memberAccountService.getOne(memberAcWQ);
                    }

                }
            }
        };
    }


}
