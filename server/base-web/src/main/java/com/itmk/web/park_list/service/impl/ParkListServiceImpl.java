package com.itmk.web.park_list.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.fee_park.entity.FeePark;
import com.itmk.web.fee_park.mapper.FeeParkMapper;
import com.itmk.web.live_park.entity.LivePark;
import com.itmk.web.live_park.mapper.LiveParkMapper;
import com.itmk.web.park_list.entity.ParkList;
import com.itmk.web.park_list.entity.ParkListParm;
import com.itmk.web.park_list.mapper.ParkListMapper;
import com.itmk.web.park_list.service.ParkListService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParkListServiceImpl extends ServiceImpl<ParkListMapper, ParkList> implements ParkListService {

    @Resource
    private LiveParkMapper liveParkMapper;
    @Resource
    private FeeParkMapper feeParkMapper;

    @Override
    public IPage<ParkList> getList(ParkListParm parkListParm) {
        // 构造分页对象
        IPage<ParkList> page = new Page<>();
        page.setCurrent(parkListParm.getCurrentPage());
        page.setSize(parkListParm.getPageSize());

        // 调用自定义 Mapper 方法
        return this.baseMapper.getList(page, parkListParm);
    }

    /**
     * 根据公司ID删除车位及其关联数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByCompanyId(Long companyId) {
        // 1. 查询该公司下的所有车位
        QueryWrapper<ParkList> query = new QueryWrapper<>();
        query.lambda().eq(ParkList::getCompanyId, companyId);
        List<ParkList> parkList = this.list(query);

        if (parkList != null && !parkList.isEmpty()) {
            // 提取车位ID列表
            List<Long> parkIds = parkList.stream()
                    .map(ParkList::getParkId)
                    .collect(Collectors.toList());

            // 2. 删除 live_park (车位-业主关联关系)
            QueryWrapper<LivePark> liveParkQuery = new QueryWrapper<>();
            liveParkQuery.lambda().in(LivePark::getParkId, parkIds);
            liveParkMapper.delete(liveParkQuery);

            // 3. 删除 fee_park (停车费记录)
            QueryWrapper<FeePark> feeParkQuery = new QueryWrapper<>();
            feeParkQuery.lambda().in(FeePark::getParkId, parkIds);
            feeParkMapper.delete(feeParkQuery);

            // 4. 删除车位本身
            this.remove(query);
        }
    }
}