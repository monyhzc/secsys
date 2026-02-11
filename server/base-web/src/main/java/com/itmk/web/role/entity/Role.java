package com.itmk.web.role.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data //自动生成 get 和 set方法
@TableName("sys_role")  //指定实体对应的表 是哪一个
public class Role implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long roleId;
    //角色名称
    private String roleName;
    //备注
    private String remark;

    @TableField("company_id")
    private Long companyId;

    // 【新增】用于列表展示公司名称，非数据库字段
    @TableField(exist = false)
    private String companyName;
}