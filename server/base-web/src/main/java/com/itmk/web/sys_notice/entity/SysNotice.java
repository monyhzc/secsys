package com.itmk.web.sys_notice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sys_notice")
public class SysNotice implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long noticeId;
    
    private Long userId;
    
    // 标题
    private String title;
    
    // 内容
    private String noticeContent;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    // 【新增】所属公司ID
    @TableField("company_id")
    private Long companyId;

    // 【新增】所属公司名称 (用于列表显示，数据库不存在此列)
    @TableField(exist = false)
    private String companyName;
}