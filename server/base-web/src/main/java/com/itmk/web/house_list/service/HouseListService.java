package com.itmk.web.house_list.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.web.house_list.entity.HouseList;
import com.itmk.web.house_list.entity.ListParm;

public interface HouseListService extends IService<HouseList> {
    // 分页查询房屋列表
    IPage<HouseList> getList(ListParm parm);

    // 【新增】根据公司ID删除所有房屋及关联的入住记录
    void deleteByCompanyId(Long companyId);
}