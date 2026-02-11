package com.itmk.web.sys_notice.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysNoticeParm implements Serializable {
    // 当前页
    private Long currentPage;
    // 页容量
    private Long pageSize;
    // 标题关键字
    private String title;

    // 【新增】所属公司ID (用于权限过滤)
    private Long companyId;
}