package com.itmk.web.work_order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.work_order.entity.WorkOrder;
import com.itmk.web.work_order.entity.WorkOrderParm;
import com.itmk.web.work_order.mapper.WorkOrderMapper;
import com.itmk.web.work_order.service.WorkOrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements WorkOrderService {
    @Override
    public IPage<WorkOrder> getList(WorkOrderParm parm) {
        IPage<WorkOrder> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        return this.baseMapper.getList(page, parm);
    }

    @Override
    public Map<String, Object> getStatistics(Long companyId) {
        Map<String, Object> stats = new HashMap<>();
        QueryWrapper<WorkOrder> query = new QueryWrapper<>();
        if (companyId != null) {
            query.eq("company_id", companyId);
        }
        List<WorkOrder> allOrders = this.list(query);

        // 1. 按状态统计
        Map<String, Long> statusCount = allOrders.stream()
                .collect(Collectors.groupingBy(WorkOrder::getStatus, Collectors.counting()));
        stats.put("statusCount", statusCount);

        // 2. 按优先级统计
        Map<String, Long> priorityCount = allOrders.stream()
                .collect(Collectors.groupingBy(WorkOrder::getPriority, Collectors.counting()));
        stats.put("priorityCount", priorityCount);

        // 3. 平均满意度
        double avgSatisfaction = allOrders.stream()
                .filter(o -> o.getSatisfaction() != null)
                .mapToInt(WorkOrder::getSatisfaction)
                .average().orElse(0.0);
        stats.put("avgSatisfaction", avgSatisfaction);

        // 4. 平均处理时长 (单位：小时)
        double avgDuration = allOrders.stream()
                .filter(o -> o.getFinishTime() != null && o.getCreateTime() != null)
                .mapToLong(o -> (o.getFinishTime().getTime() - o.getCreateTime().getTime()) / (1000 * 60 * 60))
                .average().orElse(0.0);
        stats.put("avgDuration", avgDuration);

        return stats;
    }
}
