package com.itmk.web.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;

/**
 * 员工表
 */
@Data
@TableName("sys_user")
public class User implements UserDetails, Serializable {
    //设置主键自增
    @TableId(type = IdType.AUTO)
    private Long userId;
    //姓名
    private String loginName;
    //登录密码
    private String password;
    //登录账号
    private String username;
    //电话
    private String phone;
    //性别 0：女 1：男
    private String sex;
    //身份证
    private String idCard;
    //是否是管理员 0：不是 1：是
    private String isAdmin;
    //0：在职  1：离职
    private String status;
    //0：启用 1：禁用
    private String isUsed;
    
    //所属物业公司ID
    @TableField("company_id")
    private Long companyId;

    @TableField(exist = false)
    private Long roleId;
    
    //用于列表显示公司名称，exist = false 表示该字段不属于 sys_user 表
    @TableField(exist = false)
    private String companyName;

    //Spring Security 字段
    @TableField(exist = false)
    private Collection<? extends GrantedAuthority> authorities;
    @TableField(exist = false)
    private boolean isAccountNonExpired = true;
    @TableField(exist = false)
    private boolean isAccountNonLocked = true;
    @TableField(exist = false)
    private boolean isCredentialsNonExpired = true;
    @TableField(exist = false)
    private boolean isEnabled = true;
}