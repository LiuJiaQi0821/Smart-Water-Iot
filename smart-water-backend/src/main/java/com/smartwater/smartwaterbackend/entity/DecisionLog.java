package com.smartwater.smartwaterbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("decision_log")
public class DecisionLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String logType;
    private String content;
    private Integer status;
    private LocalDateTime createTime;
}