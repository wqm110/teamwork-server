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

@TableName("pear_project_menu")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMenu  extends BaseDomain implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer pid;
    private String title;
    private String icon;
    private String url;
    private String file_path;
    private String params;
    private String node;
    private Integer sort;
    private Integer status;
    private Integer create_by;
    private String create_at;
    private Integer is_inner;
    private String _values;
    @TableField(exist = false)
    private String values;
    private Integer show_slider;

    @TableField(exist = false)
    private String statusText;
    @TableField(exist = false)
    private String innerText;
    @TableField(exist = false)
    private String fullUrl;
}
