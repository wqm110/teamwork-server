package com.projectm.member.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.projectm.domain.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@TableName("pear_member_account")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberAccount extends BaseDomain implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String code;
    private String member_code;
    private String organization_code;
    private String department_code;
    private String authorize;
    private Integer is_owner;
    private String name;
    private String mobile;
    private String email;
    private String create_time;
    private String last_login_time;
    private Integer status;
    private String description;
    private String avatar;
    private String position;
    private String department;

}
