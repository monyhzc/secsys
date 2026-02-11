package com.itmk.web.user.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 返回给前端的用户信息实体
 */
@Data
public class UserInfo implements Serializable {
    private Long id;
    private String name;
    private String avatar;
    private String introduction;
    private Object[] roles;
    
    // 【新增】所属公司ID，用于前端判断是否显示公司选择框
    private Long companyId;
}