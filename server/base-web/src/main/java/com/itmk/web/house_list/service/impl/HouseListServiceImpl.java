package com.itmk.web.house_list.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.house_list.entity.HouseList;
import com.itmk.web.house_list.entity.ListParm;
import com.itmk.web.house_list.mapper.HouseListMapper;
import com.itmk.web.house_list.service.HouseListService;
import com.itmk.web.live_house.entity.LiveHouse;
import com.itmk.web.live_house.mapper.LiveHouseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HouseListServiceImpl extends ServiceImpl<HouseListMapper, HouseList> implements HouseListService {

    @Resource
    private LiveHouseMapper liveHouseMapper;

    @Override
    public IPage<HouseList> getList(ListParm parm) {
        // 构造分页对象
        IPage<HouseList> page = new Page<>();
        page.setSize(parm.getPageSize());
        page.setCurrent(parm.getCurrentPage());
        // 调用自定义Mapper查询
        return this.baseMapper.getList(page, parm);
    }

    /**
     * 根据公司ID删除房屋及其关联数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByCompanyId(Long companyId) {
        // 1. 查询该公司下的所有房屋
        QueryWrapper<HouseList> query = new QueryWrapper<>();
        query.lambda().eq(HouseList::getCompanyId, companyId);
        List<HouseList> houseList = this.list(query);

        if (houseList != null && !houseList.isEmpty()) {
            // 提取房屋ID列表
            List<Long> houseIds = houseList.stream()
                    .map(HouseList::getHouseId)
                    .collect(Collectors.toList());

            // 2. 删除 live_house 表中与这些房屋关联的记录 (清理业主-房屋关系)
            QueryWrapper<LiveHouse> liveHouseQuery = new QueryWrapper<>();
            liveHouseQuery.lambda().in(LiveHouse::getHouseId, houseIds);
            liveHouseMapper.delete(liveHouseQuery);

            // 3. 删除房屋本身
            this.remove(query);
        }
    }
}