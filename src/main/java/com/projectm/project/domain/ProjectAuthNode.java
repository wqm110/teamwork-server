package com.projectm.project.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.projectm.domain.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@TableName("team_project_auth_node")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProjectAuthNode extends BaseDomain implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer auth;
    private String node;

}
