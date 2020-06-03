package com.projectm.task.domain;

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

@TableName("pear_task")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Task extends BaseDomain implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("code")
    private String code;
    @TableField("project_code")
    private String project_code;
    @TableField("name")
    private String name;
    @TableField("pri")
    private Integer pri;
    @TableField("execute_status")
    private String execute_status;
    @TableField("description")
    private String description;
    @TableField("create_by")
    private String create_by;
    @TableField("create_time")
    private String create_time;
    @TableField("assign_to")
    private String assign_to;
    @TableField("deleted")
    private Integer deleted;
    @TableField("stage_code")
    private String stage_code;
    @TableField("task_tag")
    private String task_tag;
    @TableField("done")
    private Integer done;
    @TableField("begin_time")
    private String begin_time;
    @TableField("end_time")
    private String end_time;
    @TableField("remind_time")
    private String remind_time;
    @TableField("pcode")
    private String pcode;
    @TableField("sort")
    private Integer sort;
    @TableField("like")
    private Integer like;
    @TableField("star")
    private Integer star;
    @TableField("deleted_time")
    private String deleted_time;
    @TableField("private")
    private Integer privated;
    @TableField("id_num")
    private Integer id_num;
    @TableField("path")
    private String path;
    @TableField("schedule")
    private Integer schedule;
    @TableField("version_code")
    private String version_code;
    @TableField("features_code")
    private String features_code;
    @TableField("work_time")
    private Integer work_time;
    @TableField("status")
    private Integer status;

    public Integer getPrivate(){
        return privated;
    }
}
