package com.itmk.web.work_order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("work_order")
public class WorkOrder implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    // 工单标题
    private String title;

    // 工单描述
    private String description;

    // 工单图片
    private String image;

    // 处理人ID
    private Long handlerId;

    // 提交人ID
    private Long submitterId;

    // 复核人ID
    private Long reviewerId;

    // 工单状态 0:待处理 1:处理中 2:待复核 3:已完成
    private String status;

    // 优先级 0:低 1:中 2:高 3:紧急
    private String priority;

    // 满意度评分
    private Integer satisfaction;

    // 处理结果
    private String orderResult;

    // 复核结果
    private String reviewResult;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date finishTime;

    // 所属物业公司ID
    @TableField("company_id")
    private Long companyId;

    // 提交人姓名
    @TableField(exist = false)
    private String submitterName;

    // 处理人姓名
    @TableField(exist = false)
    private String handlerName;

    // 复核人姓名
    @TableField(exist = false)
    private String reviewerName;
}
