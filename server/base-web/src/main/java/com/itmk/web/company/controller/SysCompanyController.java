package com.itmk.web.company.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.web.company.entity.SysCompany;
import com.itmk.web.company.service.SysCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
public class SysCompanyController {

    @Autowired
    private SysCompanyService sysCompanyService;

    // List companies (Platform Admin only)
    @GetMapping("/list")
    public ResultVo list(SysCompany sysCompany, Integer currentPage, Integer pageSize) {
        IPage<SysCompany> page = new Page<>(currentPage, pageSize);
        QueryWrapper<SysCompany> queryWrapper = new QueryWrapper<>();
        if (sysCompany.getCompanyName() != null) {
            queryWrapper.like("company_name", sysCompany.getCompanyName());
        }
        IPage<SysCompany> res = sysCompanyService.page(page, queryWrapper);
        return ResultUtils.success("查询成功", res);
    }

    // Add company
    @PostMapping("/add")
    public ResultVo add(@RequestBody SysCompany sysCompany) {
        if (sysCompanyService.save(sysCompany)) {
            return ResultUtils.success("新增成功");
        }
        return ResultUtils.error("新增失败");
    }

    // Edit company
    @PutMapping("/edit")
    public ResultVo edit(@RequestBody SysCompany sysCompany) {
        if (sysCompanyService.updateById(sysCompany)) {
            return ResultUtils.success("编辑成功");
        }
        return ResultUtils.error("编辑失败");
    }
}
