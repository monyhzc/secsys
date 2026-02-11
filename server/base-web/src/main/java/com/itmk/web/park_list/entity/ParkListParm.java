package com.itmk.web.park_list.entity;

import lombok.Data;

@Data
public class ParkListParm {
    private Long currentPage;
    private Long pageSize;
    private String parkName;
    private String parkStatus;
    private String parkType;
    private String vehicleType;
    
    // 【新增】数据权限过滤字段
    private Long companyId;
}