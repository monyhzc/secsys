package com.itmk.web.house_list.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itmk.web.house_list.entity.HouseList;
import com.itmk.web.house_list.entity.ListParm; // 引入Parm
import org.apache.ibatis.annotations.Param;

public interface HouseListMapper extends BaseMapper<HouseList> {
    // 【修改】改为传对象
    IPage<HouseList> getList(IPage<HouseList> page, @Param("parm") ListParm parm);
}