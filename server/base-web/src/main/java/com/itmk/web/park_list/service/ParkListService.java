package com.itmk.web.park_list.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.web.park_list.entity.ParkList;
import com.itmk.web.park_list.entity.ParkListParm;

public interface ParkListService extends IService<ParkList> {
    IPage<ParkList> getList(ParkListParm parkListParm);

    // 【新增】根据公司ID删除所有车位及关联数据（租赁记录、缴费记录）
    void deleteByCompanyId(Long companyId);
}