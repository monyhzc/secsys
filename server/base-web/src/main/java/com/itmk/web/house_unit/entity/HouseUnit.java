package com.itmk.web.house_unit.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("house_unit")
public class HouseUnit implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long unitId;
    private Long buildId;
    private String unitName;
    
    // 栋数名称 (非数据库字段)
    @TableField(exist = false)
    private String name;

    // 【新增】所属公司ID
    @TableField("company_id")
    private Long companyId;

    // 【新增】所属公司名称 (非数据库字段，用于列表展示)
    @TableField(exist = false)
    private String companyName;
}