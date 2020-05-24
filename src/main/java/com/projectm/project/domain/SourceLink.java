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

@TableName("pear_source_link")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SourceLink  extends BaseDomain implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String code;
    private String source_type;
    private String source_code;
    private String link_type;
    private String link_code;
    private String organization_code;
    private String create_by;
    private String create_time;
    private Integer sort;
}
