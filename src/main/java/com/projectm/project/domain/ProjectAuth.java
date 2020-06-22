package com.projectm.project.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.projectm.domain.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@TableName("team_project_auth")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProjectAuth extends BaseDomain implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String title;
    private Integer status;
    private String _desc;

    private Integer sort;

    @TableField(exist = false)
    private String desc;
    private Integer create_by;
    private String create_at;
    private String organization_code;
    private Integer is_default;
    private String type;

    @TableField(exist = false)
    private Integer canDelete;
}
