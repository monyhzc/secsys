package com.itmk.web.house_building.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.house_building.entity.HouseBuilding;
import com.itmk.web.house_building.entity.HouseBuildingParm;
import com.itmk.web.house_building.mapper.HouseBuildingMapper;
import com.itmk.web.house_building.service.HouseBuildingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HouseBuildingServiceImpl extends ServiceImpl<HouseBuildingMapper, HouseBuilding> implements HouseBuildingService {
    
    @Override
    public IPage<HouseBuilding> getList(HouseBuildingParm parm) {
        // 构造分页对象
        IPage<HouseBuilding> page = new Page<>();
        page.setSize(parm.getPageSize());
        page.setCurrent(parm.getCurrentPage());
        
        // 调用自定义 Mapper 方法
        return this.baseMapper.getList(page, parm);
    }

    /**
     * 根据公司ID删除楼栋
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByCompanyId(Long companyId) {
        QueryWrapper<HouseBuilding> query = new QueryWrapper<>();
        query.lambda().eq(HouseBuilding::getCompanyId, companyId);
        this.remove(query);
    }
}