package com.itmk.web.house_unit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itmk.web.house_unit.entity.HouseUnit;
import com.itmk.web.house_unit.entity.HouseUnitParm; // 引入 Parm
import org.apache.ibatis.annotations.Param;

public interface HouseUnitMapper extends BaseMapper<HouseUnit> {
    // 【修改】使用对象传递参数
    IPage<HouseUnit> getList(IPage<HouseUnit> page, @Param("parm") HouseUnitParm parm);
}