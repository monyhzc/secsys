package com.itmk.web.fee_property.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.web.fee_property.entity.FeeProperty;
import com.itmk.web.fee_property.service.FeePropertyService;
import com.itmk.web.user.entity.User;
import com.itmk.web.live_user.entity.LiveUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 物业费Controller
 */
@RestController
@RequestMapping("/api/feeProperty")
public class FeePropertyController {
    @Autowired
    private FeePropertyService feePropertyService;

    /**
     * 获取物业费列表
     */
    @GetMapping("/list")
    public ResultVo getList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Long companyId = null;
        if (principal instanceof User) {
            companyId = ((User) principal).getCompanyId();
        } else if (principal instanceof LiveUser) {
            companyId = ((LiveUser) principal).getCompanyId();
        }
        QueryWrapper<FeeProperty> wrapper = new QueryWrapper<>();
        if (companyId != null) {
            wrapper.eq("company_id", companyId);
        }
        List<FeeProperty> list = feePropertyService.list(wrapper);
        return ResultUtils.success("获取成功", list);
    }

    /**
     * 新增物业费
     */
    @PostMapping("/add")
    public ResultVo add(@RequestBody FeeProperty feeProperty) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            User user = (User) principal;
            feeProperty.setCompanyId(user.getCompanyId());
        } else if (principal instanceof LiveUser) {
            LiveUser liveUser = (LiveUser) principal;
            feeProperty.setCompanyId(liveUser.getCompanyId());
        }
        
        // 计算费用
        FeeProperty calculatedFee = feePropertyService.calculateFee(feeProperty);
        boolean save = feePropertyService.save(calculatedFee);
        if (save) {
            return ResultUtils.success("新增成功");
        }
        return ResultUtils.error("新增失败");
    }

    /**
     * 编辑物业费
     */
    @PutMapping("/edit")
    public ResultVo edit(@RequestBody FeeProperty feeProperty) {
        // 重新计算费用
        FeeProperty calculatedFee = feePropertyService.calculateFee(feeProperty);
        boolean update = feePropertyService.updateById(calculatedFee);
        if (update) {
            return ResultUtils.success("编辑成功");
        }
        return ResultUtils.error("编辑失败");
    }

    /**
     * 删除物业费
     */
    @DeleteMapping("/delete/{propertyFeeId}")
    public ResultVo delete(@PathVariable Long propertyFeeId) {
        boolean remove = feePropertyService.removeById(propertyFeeId);
        if (remove) {
            return ResultUtils.success("删除成功");
        }
        return ResultUtils.error("删除失败");
    }

    /**
     * 获取物业费详情
     */
    @GetMapping("/getById/{propertyFeeId}")
    public ResultVo getById(@PathVariable Long propertyFeeId) {
        FeeProperty feeProperty = feePropertyService.getById(propertyFeeId);
        return ResultUtils.success("获取成功", feeProperty);
    }

    /**
     * 标记缴费
     */
    @PutMapping("/pay/{propertyFeeId}")
    public ResultVo pay(@PathVariable Long propertyFeeId) {
        FeeProperty feeProperty = feePropertyService.getById(propertyFeeId);
        feeProperty.setPayPropertyStatus("1");
        feeProperty.setPayPropertyTime(new Date());
        boolean update = feePropertyService.updateById(feeProperty);
        if (update) {
            return ResultUtils.success("缴费成功");
        }
        return ResultUtils.error("缴费失败");
    }
}
