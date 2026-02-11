package com.itmk.web.sys_notice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.sys_notice.entity.SysNotice;
import com.itmk.web.sys_notice.entity.SysNoticeParm;
import com.itmk.web.sys_notice.mapper.SysNoticeMapper;
import com.itmk.web.sys_notice.service.SysNoticeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements SysNoticeService {

    @Override
    public SysNotice getOne(Long companyId) {
        // 构造查询条件
        QueryWrapper<SysNotice> query = new QueryWrapper<>();
        
        // 【新增】如果传入了 companyId，则进行隔离过滤
        if (companyId != null) {
            query.eq("company_id", companyId);
        }
        
        query.orderByDesc("create_time");
        // 获取最新一条
        return this.baseMapper.getOne(query);
    }

    @Override
    public IPage<SysNotice> getList(SysNoticeParm parm) {
        // 构造分页对象
        IPage<SysNotice> page = new Page<>();
        page.setSize(parm.getPageSize());
        page.setCurrent(parm.getCurrentPage());
        
        // 调用自定义 Mapper 方法 (XML中已经写好了 if test="parm.companyId != null" 的逻辑)
        return this.baseMapper.getList(page, parm);
    }
    
    /**
     * 根据公司ID删除公告
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByCompanyId(Long companyId) {
        QueryWrapper<SysNotice> query = new QueryWrapper<>();
        query.lambda().eq(SysNotice::getCompanyId, companyId);
        this.remove(query);
    }
}