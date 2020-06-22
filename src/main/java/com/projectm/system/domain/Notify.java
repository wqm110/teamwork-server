package com.projectm.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.projectm.domain.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@TableName("team_notify")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Notify  extends BaseDomain implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String title;
    private String content;
    private String type;
    private String from;
    private String to;
    private String create_time;
    private Integer is_read;
    private String read_time;
    private String send_data;
    private String finally_send_time;
    private String send_time;
    private String action;
    private String terminal;
    private String from_type;
    private String avatar;
    private String source_code;
}
