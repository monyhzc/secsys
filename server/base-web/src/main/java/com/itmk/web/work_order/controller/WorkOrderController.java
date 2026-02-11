package com.itmk.web.work_order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.web.work_order.entity.WorkOrder;
import com.itmk.web.work_order.entity.WorkOrderParm;
import com.itmk.web.work_order.service.WorkOrderService;
import com.itmk.web.user.entity.User;
import com.itmk.web.live_user.entity.LiveUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/workOrder")
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    public ResultVo getList(WorkOrderParm parm) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            User user = (User) principal;
            if (user.getCompanyId() != null) {
                parm.setCompanyId(user.getCompanyId());
            }
        } else if (principal instanceof LiveUser) {
            LiveUser liveUser = (LiveUser) principal;
            if (liveUser.getCompanyId() != null) {
                parm.setCompanyId(liveUser.getCompanyId());
            }
        }

        IPage<WorkOrder> list = workOrderService.getList(parm);
        return ResultUtils.success("查询成功", list);
    }

    /**
     * 获取统计数据
     */
    @GetMapping("/getStats")
    public ResultVo getStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Long companyId = null;
        if (principal instanceof User) {
            companyId = ((User) principal).getCompanyId();
        }
        Map<String, Object> stats = workOrderService.getStatistics(companyId);
        return ResultUtils.success("查询成功", stats);
    }

    /**
     * 新增工单
     */
    @PostMapping
    @PreAuthorize("hasAuthority('sys:myWorkOrder:add')")
    public ResultVo add(@RequestBody WorkOrder workOrder) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            User currentUser = (User) principal;
            workOrder.setSubmitterId(currentUser.getUserId());
            if (currentUser.getCompanyId() != null) {
                workOrder.setCompanyId(currentUser.getCompanyId());
            }
        } else if (principal instanceof LiveUser) {
            LiveUser liveUser = (LiveUser) principal;
            workOrder.setSubmitterId(liveUser.getUserId());
            if (liveUser.getCompanyId() != null) {
                workOrder.setCompanyId(liveUser.getCompanyId());
            }
        }

        workOrder.setStatus("0"); // 待处理
        workOrder.setCreateTime(new Date());
        boolean b = workOrderService.save(workOrder);
        if (b) {
            return ResultUtils.success("工单提交成功!");
        }
        return ResultUtils.error("工单提交失败!");
    }

    /**
     * 编辑工单
     */
    @PutMapping
    @PreAuthorize("hasAuthority('sys:myWorkOrder:edit')")
    public ResultVo edit(@RequestBody WorkOrder workOrder) {
        boolean b = workOrderService.updateById(workOrder);
        if (b) {
            return ResultUtils.success("编辑成功!");
        }
        return ResultUtils.error("编辑失败!");
    }

    /**
     * 处理工单
     */
    @PostMapping("/handle")
    @PreAuthorize("hasAuthority('sys:workOrderList:do')")
    public ResultVo handle(@RequestBody WorkOrder workOrder) {
        // 更新工单状态为 2 (待复核)
        workOrder.setStatus("2");
        workOrder.setUpdateTime(new Date());
        boolean b = workOrderService.updateById(workOrder);
        if (b) {
            return ResultUtils.success("工单处理成功!");
        }
        return ResultUtils.error("工单处理失败!");
    }

    /**
     * 复核工单
     */
    @PostMapping("/review")
    @PreAuthorize("hasAuthority('sys:workOrderReview:do')")
    public ResultVo review(@RequestBody WorkOrder workOrder) {
        // 更新工单状态为 3 (已完成)
        workOrder.setStatus("3");
        workOrder.setFinishTime(new Date());
        workOrder.setUpdateTime(new Date());
        boolean b = workOrderService.updateById(workOrder);
        if (b) {
            return ResultUtils.success("工单复核成功!");
        }
        return ResultUtils.error("工单复核失败!");
    }

    /**
     * 用户评价
     */
    @PostMapping("/evaluate")
    public ResultVo evaluate(@RequestBody WorkOrder workOrder) {
        if (workOrder.getSatisfaction() == null) {
            return ResultUtils.error("请评分!");
        }
        WorkOrder wo = new WorkOrder();
        wo.setId(workOrder.getId());
        wo.setSatisfaction(workOrder.getSatisfaction());
        boolean b = workOrderService.updateById(wo);
        if (b) {
            return ResultUtils.success("评价成功!");
        }
        return ResultUtils.error("评价失败!");
    }

    /**
     * 删除工单
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:myWorkOrder:delete')")
    public ResultVo delete(@PathVariable("id") Long id) {
        boolean b = workOrderService.removeById(id);
        if (b) {
            return ResultUtils.success("删除成功!");
        }
        return ResultUtils.error("删除失败!");
    }
}