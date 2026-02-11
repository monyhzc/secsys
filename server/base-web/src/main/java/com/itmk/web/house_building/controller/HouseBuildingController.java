package com.itmk.web.house_building.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.web.house_building.entity.HouseBuilding;
import com.itmk.web.house_building.entity.HouseBuildingParm;
import com.itmk.web.house_building.service.HouseBuildingService;
import com.itmk.web.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 楼栋控制器
 */
@RestController
@RequestMapping("/api/HouseBuilding")
public class HouseBuildingController {
    @Autowired
    private HouseBuildingService houseBuildingService;

    /**
     * 表格列表
     */
    @GetMapping("/list")
    public ResultVo list(HouseBuildingParm parm){
        // --- 权限隔离 ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            if (currentUser.getCompanyId() != null) {
                parm.setCompanyId(currentUser.getCompanyId());
            }
        }
        IPage<HouseBuilding> list = houseBuildingService.getList(parm);
        return ResultUtils.success("查询成功",list);
    }

    /**
     * 给单元查询列表 (下拉框使用)
     * 【修复】增加参数接收和筛选逻辑
     */
    @GetMapping("/unitList")
    public ResultVo unitList(HouseBuildingParm parm){
        QueryWrapper<HouseBuilding> query = new QueryWrapper<>();
        
        // --- 权限/联动 筛选逻辑 ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            
            // 1. 如果是普通物业管理员，强制只查自己公司的
            if (currentUser.getCompanyId() != null) {
                query.lambda().eq(HouseBuilding::getCompanyId, currentUser.getCompanyId());
            } 
            // 2. 如果是平台管理员，且前端传了 companyId (联动筛选)，则查该公司的
            else if (parm.getCompanyId() != null) {
                query.lambda().eq(HouseBuilding::getCompanyId, parm.getCompanyId());
            }
            // 3. 平台管理员没传 companyId，默认查所有(或返回空，这里暂定查所有)
        }
        
        // 按序号排序
        query.lambda().orderByAsc(HouseBuilding::getOrderNum);
        
        List<HouseBuilding> list = houseBuildingService.list(query);
        return ResultUtils.success("查询成功",list);
    }
    
    /**
     * 新增楼栋
     */
    @PostMapping
    @PreAuthorize("hasAuthority('sys:houseBuilding:add')")
    public ResultVo save(@RequestBody HouseBuilding houseBuilding){
        // --- 权限归属 ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            if (currentUser.getCompanyId() != null) {
                houseBuilding.setCompanyId(currentUser.getCompanyId());
            } else if (houseBuilding.getCompanyId() == null) {
                return ResultUtils.error("平台管理员新增必须选择公司");
            }
        }
        
        boolean save = houseBuildingService.save(houseBuilding);
        if(save){
            return ResultUtils.success("新增成功!");
        }
        return ResultUtils.error("新增失败!");
    }

    /**
     * 编辑楼栋
     */
    @PutMapping
    @PreAuthorize("hasAuthority('sys:houseBuilding:edit')")
    public ResultVo edit(@RequestBody HouseBuilding houseBuilding){
        boolean save = houseBuildingService.updateById(houseBuilding);
        if(save){
            return ResultUtils.success("编辑成功!");
        }
        return ResultUtils.error("编辑失败!");
    }

    /**
     * 删除楼栋
     */
    @DeleteMapping("/{buildId}")
    @PreAuthorize("hasAuthority('sys:houseBuilding:delete')")
    public ResultVo delete(@PathVariable("buildId") Long buildId){
        boolean save = houseBuildingService.removeById(buildId);
        if(save){
            return ResultUtils.success("删除成功!");
        }
        return ResultUtils.error("删除失败!");
    }
}