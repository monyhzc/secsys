package com.itmk.web.company.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.company.entity.SysCompany;
import com.itmk.web.company.mapper.SysCompanyMapper;
import com.itmk.web.company.service.SysCompanyService;
import org.springframework.stereotype.Service;

@Service
public class SysCompanyServiceImpl extends ServiceImpl<SysCompanyMapper, SysCompany> implements SysCompanyService {
}
