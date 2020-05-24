package com.projectm.task.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.projectm.domain.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
@TableName("pear_task_stages")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TaskStage extends BaseDomain implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String project_code;
    private Integer sort;
    private String description;
    private String create_time;
    private String code;
    private Integer deleted;
}
