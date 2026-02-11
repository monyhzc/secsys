package com.itmk.web.sys_notice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.web.sys_notice.entity.SysNotice;
import com.itmk.web.sys_notice.entity.SysNoticeParm;

public interface SysNoticeService extends IService<SysNotice> {

    // 获取最新一条公告
    SysNotice getOne(Long companyId);

    // 列表查询
    IPage<SysNotice> getList(SysNoticeParm parm);

    // 【新增】根据公司ID删除公告
    void deleteByCompanyId(Long companyId);
}