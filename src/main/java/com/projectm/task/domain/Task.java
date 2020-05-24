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
    private String code;
    private String project_code;
    private String name;
    private Integer pri;
    private String execute_status;
    private String description;
    private String create_by;
    private String create_time;
    private String assign_to;
    private Integer deleted;
    private String stage_code;
    private String task_tag;
    private Integer done;
    private String begin_time;
    private String end_time;
    private String remind_time;
    private String pcode;
    private Integer sort;
    private Integer like;
    private Integer star;
    private String deleted_time;
    @TableField("private")
    private Integer privated;
    private Integer id_num;
    private String path;
    private Integer schedule;
    private String version_code;
    private String features_code;
    private Integer work_time;
}
