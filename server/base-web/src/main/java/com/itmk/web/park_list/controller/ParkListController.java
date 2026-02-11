package com.itmk.web.park_list.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.web.park_list.entity.ParkList;
import com.itmk.web.park_list.entity.ParkListParm;
import com.itmk.web.park_list.service.ParkListService;
import com.itmk.web.user.entity.User; // 引入User

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 车位管理模块控制器
 */
@RestController
@RequestMapping("/api/parkList")
public class ParkListController {
    @Autowired
    private ParkListService parkListService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    public ResultVo getList(ParkListParm parkListParm){
        // --- 权限隔离 ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            // 如果是普通物业管理员，强制只查自己公司
            if (currentUser.getCompanyId() != null) {
                parkListParm.setCompanyId(currentUser.getCompanyId());
            }
        }
        // ----------------
        
        IPage<ParkList> list = parkListService.getList(parkListParm);
        return ResultUtils.success("查询成功",list);
    }

    /**
     * 新增车位
     */
    @PostMapping
    @PreAuthorize("hasAuthority('sys:parkList:add')")
    public ResultVo add(@RequestBody ParkList parkList){
        // --- 归属逻辑 ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            if (currentUser.getCompanyId() != null) {
                // 普通管理员：强制归属自己公司
                parkList.setCompanyId(currentUser.getCompanyId());
            } else {
                // 平台管理员：校验必须选择公司
                if (parkList.getCompanyId() == null) {
                    return ResultUtils.error("平台管理员新增车位必须选择所属公司！");
                }
            }
        }
        // ----------------

        boolean save = parkListService.save(parkList);
        if(save){
            return ResultUtils.success("新增车位成功!");
        }
        return ResultUtils.error("新增车位失败!");
    }

    /**
     * 编辑车位
     */
    @PutMapping
    @PreAuthorize("hasAuthority('sys:parkList:edit')")
    public ResultVo edit(@RequestBody ParkList parkList){
        boolean save = parkListService.updateById(parkList);
        if(save){
            return ResultUtils.success("编辑车位成功!");
        }
        return ResultUtils.error("编辑车位失败!");
    }

    /**
     * 删除车位
     */
    @DeleteMapping("/{parkId}")
    @PreAuthorize("hasAuthority('sys:parkList:delete')")
    public ResultVo delete(@PathVariable("parkId") Long parkId){
        boolean b = parkListService.removeById(parkId);
        if(b){
            return ResultUtils.success("删除车位成功!");
        }
        return ResultUtils.error("删除车位失败!");
    }

/**
     * 查询所有车位（用于下拉框）
     */
    @GetMapping("/listNoPage")
    public ResultVo listNoPage(){
        QueryWrapper<ParkList> query = new QueryWrapper<>();
        // --- 权限隔离 ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            // 如果是物业管理员，只能看到自己公司的车位
            if (currentUser.getCompanyId() != null) {
                query.lambda().eq(ParkList::getCompanyId, currentUser.getCompanyId());
            }
        }
        // ----------------
        List<ParkList> list = parkListService.list(query);
        return ResultUtils.success("查询成功", list);
    }

}