package com.itmk.web.sys_notice.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itmk.web.sys_notice.entity.SysNotice;
import com.itmk.web.sys_notice.entity.SysNoticeParm;
import org.apache.ibatis.annotations.Param;

public interface SysNoticeMapper extends BaseMapper<SysNotice> {
    
    // 获取最新一条公告 (保持原有逻辑)
    SysNotice getOne(@Param("ew") QueryWrapper<SysNotice> query);

    // 【新增】自定义分页查询，关联公司表获取名称
    IPage<SysNotice> getList(IPage<SysNotice> page, @Param("parm") SysNoticeParm parm);
}