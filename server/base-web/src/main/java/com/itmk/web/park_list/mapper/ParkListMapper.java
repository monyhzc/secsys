package com.itmk.web.park_list.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itmk.web.park_list.entity.ParkList;
import com.itmk.web.park_list.entity.ParkListParm;
import org.apache.ibatis.annotations.Param;

public interface ParkListMapper extends BaseMapper<ParkList> {
    // 【新增】自定义分页查询，关联公司表
    IPage<ParkList> getList(IPage<ParkList> page, @Param("parm") ParkListParm parm);
}