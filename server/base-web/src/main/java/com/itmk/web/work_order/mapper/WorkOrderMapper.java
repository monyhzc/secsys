package com.itmk.web.work_order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itmk.web.work_order.entity.WorkOrder;
import com.itmk.web.work_order.entity.WorkOrderParm;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

public interface WorkOrderMapper extends BaseMapper<WorkOrder> {
    IPage<WorkOrder> getList(IPage<WorkOrder> page, @Param("parm") WorkOrderParm parm);
}
