package com.itmk.web.sys_status.controller;

import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.web.user.service.UserService;
import com.itmk.web.live_user.service.LiveUserService;
import com.itmk.web.company.service.SysCompanyService;
import com.itmk.web.work_order.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sysStatus")
public class SysStatusController {

    @Autowired
    private UserService userService;
    @Autowired
    private LiveUserService liveUserService;
    @Autowired
    private SysCompanyService sysCompanyService;
    @Autowired
    private WorkOrderService workOrderService;

    @GetMapping("/info")
    public ResultVo getStatus() {
        Map<String, Object> data = new HashMap<>();

        // 1. 服务器/JVM 状态
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        
        Map<String, Object> server = new HashMap<>();
        server.put("osName", osBean.getName());
        server.put("osArch", osBean.getArch());
        server.put("processors", osBean.getAvailableProcessors());
        server.put("loadAverage", osBean.getSystemLoadAverage());
        
        long heapUsed = memoryBean.getHeapMemoryUsage().getUsed() / 1024 / 1024;
        long heapMax = memoryBean.getHeapMemoryUsage().getMax() / 1024 / 1024;
        server.put("jvmMemoryUsed", heapUsed);
        server.put("jvmMemoryMax", heapMax);
        server.put("jvmMemoryRate", (int)((double)heapUsed / heapMax * 100));
        data.put("server", server);

        // 2. 用户统计
        Map<String, Object> users = new HashMap<>();
        long adminCount = userService.count();
        long liveCount = liveUserService.count();
        users.put("adminCount", adminCount); // 物业人员/管理员
        users.put("liveCount", liveCount);   // 业主
        users.put("totalCount", adminCount + liveCount);
        data.put("users", users);

        // 3. 业务概览
        Map<String, Object> business = new HashMap<>();
        business.put("companyCount", sysCompanyService.count());
        business.put("orderCount", workOrderService.count());
        data.put("business", business);

        return ResultUtils.success("查询成功", data);
    }
}
