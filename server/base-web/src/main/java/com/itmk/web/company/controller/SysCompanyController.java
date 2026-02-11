package com.itmk.web.company.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.web.company.entity.SysCompany;
import com.itmk.web.company.service.SysCompanyService;
import org.apache.commons.lang.StringUtils; // 引入 StringUtils
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
public class SysCompanyController {

    @Autowired
    private SysCompanyService sysCompanyService;

    // 列表查询
    @GetMapping("/list")
    public ResultVo list(SysCompany sysCompany, Integer currentPage, Integer pageSize) {
        IPage<SysCompany> page = new Page<>(currentPage, pageSize);
        QueryWrapper<SysCompany> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(sysCompany.getCompanyName())) {
            queryWrapper.like("company_name", sysCompany.getCompanyName());
        }
        // 按创建时间倒序排列
        queryWrapper.orderByDesc("create_time");
        IPage<SysCompany> res = sysCompanyService.page(page, queryWrapper);
        return ResultUtils.success("查询成功", res);
    }

    /**
     * 开通物业公司
     * 1. 修复路径：去掉 "/add"，匹配前端 api/company
     * 2. 增加唯一性校验：参考 UserController 的逻辑
     */
    @PostMapping
    @PreAuthorize("hasAuthority('sys:company:add')")
    public ResultVo add(@RequestBody SysCompany sysCompany) {
        // 1. 基础非空校验
        if (StringUtils.isEmpty(sysCompany.getCompanyName())) {
            return ResultUtils.error("公司名称不能为空!");
        }

        // 2. 【关键逻辑】参考 UserController：判断公司名称是否已被占用
        QueryWrapper<SysCompany> query = new QueryWrapper<>();
        query.lambda().eq(SysCompany::getCompanyName, sysCompany.getCompanyName());
        SysCompany one = sysCompanyService.getOne(query);
        if (one != null) {
            return ResultUtils.error("该公司名称已存在，请勿重复添加!", 500);
        }

        // 3. 调用 Service 保存
        if (sysCompanyService.createCompany(sysCompany)) {
            return ResultUtils.success("开通物业公司成功，请前往【员工管理】为该公司创建管理员账号");
        }
        return ResultUtils.error("开通失败");
    }

    /**
     * 编辑物业公司
     * 1. 增加非空校验
     * 2. 增加名称唯一性校验（排除自身）
     */
    @PutMapping
    @PreAuthorize("hasAuthority('sys:company:edit')")
    public ResultVo edit(@RequestBody SysCompany sysCompany) {
        if (sysCompany.getCompanyId() == null) {
            return ResultUtils.error("编辑失败，ID不能为空");
        }

        // 校验名称唯一性（排除当前编辑的这条记录）
        if (StringUtils.isNotEmpty(sysCompany.getCompanyName())) {
            QueryWrapper<SysCompany> query = new QueryWrapper<>();
            query.lambda()
                    .eq(SysCompany::getCompanyName, sysCompany.getCompanyName())
                    .ne(SysCompany::getCompanyId, sysCompany.getCompanyId()); // 排除自己
            SysCompany one = sysCompanyService.getOne(query);
            if (one != null) {
                return ResultUtils.error("该公司名称已被其他公司占用!", 500);
            }
        }

        if (sysCompanyService.updateById(sysCompany)) {
            return ResultUtils.success("编辑成功");
        }
        return ResultUtils.error("编辑失败");
    }

    // 删除
    @DeleteMapping("/{companyId}")
    @PreAuthorize("hasAuthority('sys:company:delete')")
    public ResultVo delete(@PathVariable("companyId") Long companyId) {
        boolean b = sysCompanyService.removeById(companyId);
        if (b) {
            return ResultUtils.success("删除成功");
        }
        return ResultUtils.error("删除失败");
    }

    /**
     * 禁用/启用 物业公司
     */
    @PutMapping("/status")
    @PreAuthorize("hasAuthority('sys:company:status')")
    public ResultVo updateStatus(@RequestBody SysCompany sysCompany) {
        if (sysCompany.getCompanyId() == null || sysCompany.getStatus() == null) {
             return ResultUtils.error("参数错误");
        }
        SysCompany update = new SysCompany();
        update.setCompanyId(sysCompany.getCompanyId());
        update.setStatus(sysCompany.getStatus());
        if (sysCompanyService.updateById(update)) {
            return ResultUtils.success("状态更新成功");
        }
        return ResultUtils.error("状态更新失败");
    }

    /**
     * 期限管理
     */
    @PutMapping("/term")
    @PreAuthorize("hasAuthority('sys:company:term')")
    public ResultVo updateTerm(@RequestBody SysCompany sysCompany) {
        if (sysCompany.getCompanyId() == null || sysCompany.getExpireTime() == null) {
            return ResultUtils.error("请选择到期时间");
        }
        SysCompany update = new SysCompany();
        update.setCompanyId(sysCompany.getCompanyId());
        update.setExpireTime(sysCompany.getExpireTime());
        if (sysCompanyService.updateById(update)) {
            return ResultUtils.success("续期成功");
        }
        return ResultUtils.error("续期失败");
    }
}