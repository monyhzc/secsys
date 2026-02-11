package com.itmk.web.house_list.entity;

import lombok.Data;

@Data
public class ListParm {
    private String buildNme;
    private String unitName;
    private String houseNum;
    private String status;
    private Long currentPage;
    private Long pageSize;
    
    // 【新增】
    private Long companyId;
}