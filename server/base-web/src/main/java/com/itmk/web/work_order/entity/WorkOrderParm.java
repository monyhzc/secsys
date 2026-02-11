package com.itmk.web.work_order.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class WorkOrderParm implements Serializable {
    private Long currentPage;
    private Long pageSize;
    private String title;
    private Long companyId;
    private Long handlerId;
    private Long submitterId;
    private Long reviewerId;
    private String status;
}
