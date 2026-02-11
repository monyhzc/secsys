package com.itmk.web.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.web.company.entity.SysCompany;

public interface SysCompanyService extends IService<SysCompany> {
    // 新增：创建物业公司（处理默认值）
    boolean createCompany(SysCompany sysCompany);
}