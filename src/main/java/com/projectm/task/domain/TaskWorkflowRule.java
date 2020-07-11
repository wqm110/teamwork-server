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

@TableName("team_task_workflow_rule")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TaskWorkflowRule   extends BaseDomain implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String code;
    private Integer type;
    private String object_code;
    private String action;
    private String create_time;
    private String update_time;
    private String workflow_code;
    private Integer sort;
}
