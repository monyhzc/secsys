package com.itmk.web.house_list.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.web.house_list.entity.HouseList;
import com.itmk.web.house_list.entity.ListParm;
import com.itmk.web.house_list.service.HouseListService;
import com.itmk.web.user.entity.User; // 引入User
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房屋管理控制器
 */
@RestController
@RequestMapping("/api/houseList")
public class HouseListController {
    @Autowired
    private HouseListService houseListService;

    /**
     * 获取房屋列表
     */
    @GetMapping("/list")
    public ResultVo getList(ListParm parm){
        // --- 权限隔离 ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            if (currentUser.getCompanyId() != null) {
                parm.setCompanyId(currentUser.getCompanyId());
            }
        }
        // ----------------
        IPage<HouseList> list = houseListService.getList(parm);
        return ResultUtils.success("查询成功",list);
    }

    /**
     * 新增
     */
    @PostMapping
    @PreAuthorize("hasAuthority('sys:house:add')")
    public ResultVo add(@RequestBody HouseList houseList){
        // --- 归属逻辑 ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            if (currentUser.getCompanyId() != null) {
                houseList.setCompanyId(currentUser.getCompanyId());
            } else {
                if(houseList.getCompanyId() == null){
                    return ResultUtils.error("平台管理员必须选择所属公司");
                }
            }
        }
        // ----------------

        System.out.println(houseList.toString());
        boolean save = houseListService.save(houseList);
        if(save){
            return ResultUtils.success("新增房屋成功");
        }
        return ResultUtils.error("新增房屋失败!");
    }

    /**
     * 编辑
     */
    @PutMapping
    @PreAuthorize("hasAuthority('sys:house:edit')")
    public ResultVo edit(@RequestBody HouseList houseList){
        boolean save = houseListService.updateById(houseList);
        if(save){
            return ResultUtils.success("编辑房屋成功");
        }
        return ResultUtils.error("编辑房屋失败!");
    }

    /**
     * 删除
     */
    @DeleteMapping("/{houseId}")
    @PreAuthorize("hasAuthority('sys:house:delete')")
    public ResultVo delete(@PathVariable("houseId") Long houseId){
        boolean save = houseListService.removeById(houseId);
        if(save){
            return ResultUtils.success("删除房屋成功");
        }
        return ResultUtils.error("删除房屋失败!");
    }

    /**
     * 根据单元id获取房屋列表
     */
    @GetMapping("/getHouseByUnitId")
    public ResultVo getHouseByUnitId(HouseList houseList){
        QueryWrapper<HouseList> query = new QueryWrapper<>();
        query.lambda().eq(HouseList::getUnitId,houseList.getUnitId());
        List<HouseList> list = houseListService.list(query);
        return ResultUtils.success("查询成功",list);
    }
}