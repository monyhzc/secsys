package com.itmk.web.house_unit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.house_unit.entity.HouseUnit;
import com.itmk.web.house_unit.entity.HouseUnitParm;
import com.itmk.web.house_unit.mapper.HouseUnitMapper;
import com.itmk.web.house_unit.service.HouseUnitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HouseUnitServiceImpl extends ServiceImpl<HouseUnitMapper,HouseUnit> implements HouseUnitService {
    
    @Override
    public IPage<HouseUnit> getList(HouseUnitParm parm) {
        // 构造分页对象
        IPage<HouseUnit> page = new Page<>();
        page.setSize(parm.getPageSize());
        page.setCurrent(parm.getCurrentPage());
        
        // 调用修改后的 Mapper
        return this.baseMapper.getList(page, parm);
    }

    /**
     * 根据公司ID删除单元
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByCompanyId(Long companyId) {
        QueryWrapper<HouseUnit> query = new QueryWrapper<>();
        query.lambda().eq(HouseUnit::getCompanyId, companyId);
        this.remove(query);
    }
}