package com.itmk.web.house_unit.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.web.house_unit.entity.HouseUnit;
import com.itmk.web.house_unit.entity.HouseUnitParm;

public interface HouseUnitService extends IService<HouseUnit> {
    
    IPage<HouseUnit> getList(HouseUnitParm houseUnitParm);

    // 【新增】根据公司ID删除单元
    void deleteByCompanyId(Long companyId);
}