package com.itmk.web.fee_property_rule.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.web.fee_property_rule.entity.FeePropertyRule;
import com.itmk.web.fee_property_rule.service.FeePropertyRuleService;
import com.itmk.web.user.entity.User;
import com.itmk.web.live_user.entity.LiveUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物业费规则Controller
 */
@RestController
@RequestMapping("/api/feePropertyRule")
public class FeePropertyRuleController {
    @Autowired
    private FeePropertyRuleService feePropertyRuleService;

    /**
     * 获取规则列表
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
        QueryWrapper<FeePropertyRule> wrapper = new QueryWrapper<>();
        if (companyId != null) {
            wrapper.eq("company_id", companyId);
        }
        List<FeePropertyRule> list = feePropertyRuleService.list(wrapper);
        return ResultUtils.success("获取成功", list);
    }

    /**
     * 新增规则
     */
    @PostMapping("/add")
    public ResultVo add(@RequestBody FeePropertyRule rule) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            User user = (User) principal;
            rule.setCompanyId(user.getCompanyId());
        } else if (principal instanceof LiveUser) {
            LiveUser liveUser = (LiveUser) principal;
            rule.setCompanyId(liveUser.getCompanyId());
        }
        boolean save = feePropertyRuleService.save(rule);
        if (save) {
            return ResultUtils.success("新增成功");
        }
        return ResultUtils.error("新增失败");
    }

    /**
     * 编辑规则
     */
    @PutMapping("/edit")
    public ResultVo edit(@RequestBody FeePropertyRule rule) {
        boolean update = feePropertyRuleService.updateById(rule);
        if (update) {
            return ResultUtils.success("编辑成功");
        }
        return ResultUtils.error("编辑失败");
    }

    /**
     * 删除规则
     */
    @DeleteMapping("/delete/{ruleId}")
    public ResultVo delete(@PathVariable Long ruleId) {
        boolean remove = feePropertyRuleService.removeById(ruleId);
        if (remove) {
            return ResultUtils.success("删除成功");
        }
        return ResultUtils.error("删除失败");
    }

    /**
     * 获取规则详情
     */
    @GetMapping("/getById/{ruleId}")
    public ResultVo getById(@PathVariable Long ruleId) {
        FeePropertyRule rule = feePropertyRuleService.getById(ruleId);
        return ResultUtils.success("获取成功", rule);
    }
}
