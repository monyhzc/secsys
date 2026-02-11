package com.itmk.web.work_order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.web.work_order.entity.WorkOrder;
import com.itmk.web.work_order.entity.WorkOrderParm;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Map;

public interface WorkOrderService extends IService<WorkOrder> {
    IPage<WorkOrder> getList(WorkOrderParm parm);
    Map<String, Object> getStatistics(Long companyId);
}
