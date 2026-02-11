package com.itmk.web.house_building.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itmk.web.house_building.entity.HouseBuilding;
import com.itmk.web.house_building.entity.HouseBuildingParm;
import org.apache.ibatis.annotations.Param;

public interface HouseBuildingMapper extends BaseMapper<HouseBuilding> {
    // 【新增】自定义分页查询
    IPage<HouseBuilding> getList(IPage<HouseBuilding> page, @Param("parm") HouseBuildingParm parm);
}