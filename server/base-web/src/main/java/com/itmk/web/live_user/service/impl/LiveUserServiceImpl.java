package com.itmk.web.live_user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.live_house.entity.LiveHouse;
import com.itmk.web.live_house.mapper.LiveHouseMapper;
import com.itmk.web.live_park.entity.LivePark;
import com.itmk.web.live_park.mapper.LiveParkMapper;
import com.itmk.web.live_role.entity.LiveRole;
import com.itmk.web.live_role.mapper.LiveRoleMapper;
import com.itmk.web.live_user.entity.AssignHouseParm;
import com.itmk.web.live_user.entity.LiveUser;
import com.itmk.web.live_user.entity.LiveUserParm;
import com.itmk.web.live_user.mapper.LiveUserMapper;
import com.itmk.web.live_user.service.LiveUserService;
import com.itmk.web.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LiveUserServiceImpl extends ServiceImpl<LiveUserMapper, LiveUser> implements LiveUserService {

    @Resource
    private LiveHouseMapper liveHouseMapper;
    @Resource
    private LiveParkMapper liveParkMapper;
    @Resource
    private LiveRoleMapper liveRoleMapper;

    @Override
    public IPage<LiveUser> getList(LiveUserParm parm) {
        // 1. 构造分页对象
        IPage<LiveUser> page = new Page<>();
        page.setSize(parm.getPageSize());
        page.setCurrent(parm.getCurrentPage());

        // 2. 权限隔离：获取当前登录用户的 CompanyId
        Long companyId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            if (currentUser.getCompanyId() != null) {
                companyId = currentUser.getCompanyId();
            }
        }

        // 3. 调用自定义 Mapper 方法
        return this.baseMapper.getList(page, parm.getLoginName(), parm.getPhone(), companyId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveLiveUser(LiveUser liveUser) {
        // 保存业主基本信息
        this.baseMapper.insert(liveUser);
        
        // 可以在这里添加默认角色的逻辑，如果业务需要的话
        // LiveRole role = new LiveRole();
        // role.setUserId(liveUser.getUserId());
        // role.setRoleId(defaultRoleId);
        // liveRoleMapper.insert(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editLiveUser(LiveUser liveUser) {
        // 更新业主信息
        this.baseMapper.updateById(liveUser);
    }

    @Override
    public LiveUser getUser(Long userId) {
        // 调用 Mapper 自定义查询，关联查询角色信息
        return this.baseMapper.getUser(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveHouse(AssignHouseParm parm) {
        // 先删除旧数据
        QueryWrapper<LiveHouse> query = new QueryWrapper<>();
        query.lambda().eq(LiveHouse::getUserId, parm.getUserId());
        liveHouseMapper.delete(query);
        // 保存新数据
        if (parm.getList() != null && parm.getList().size() > 0) {
            LiveHouse liveHouse = new LiveHouse();
            liveHouse.setUserId(parm.getUserId());
            liveHouse.setHouseId(parm.getList().get(0)); // 1对1分配
            liveHouse.setUseStatus("1");
            liveHouseMapper.insert(liveHouse);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePark(AssignHouseParm parm) {
        // 先删除旧数据
        QueryWrapper<LivePark> query = new QueryWrapper<>();
        query.lambda().eq(LivePark::getUserId, parm.getUserId());
        liveParkMapper.delete(query);
        // 保存新数据
        if (parm.getList() != null && parm.getList().size() > 0) {
            LivePark livePark = new LivePark();
            livePark.setUserId(parm.getUserId());
            livePark.setParkId(parm.getList().get(0));
            livePark.setLiveStatue("1");
            liveParkMapper.insert(livePark);
        }
    }

    @Override
    public AssignHouseParm getHouseByUserId(AssignHouseParm parm) {
        QueryWrapper<LiveHouse> query = new QueryWrapper<>();
        query.lambda().eq(LiveHouse::getUserId, parm.getUserId());
        LiveHouse liveHouse = liveHouseMapper.selectOne(query);
        if (liveHouse != null) {
            parm.setHouseId(liveHouse.getHouseId());
        }
        return parm;
    }

    @Override
    public AssignHouseParm getParkByUserId(AssignHouseParm parm) {
        QueryWrapper<LivePark> query = new QueryWrapper<>();
        query.lambda().eq(LivePark::getUserId, parm.getUserId());
        LivePark livePark = liveParkMapper.selectOne(query);
        if (livePark != null) {
            parm.setParkId(livePark.getParkId());
        }
        return parm;
    }

    @Override
    public LiveUser loadUser(String username) {
        QueryWrapper<LiveUser> query = new QueryWrapper<>();
        query.lambda().eq(LiveUser::getUsername, username);
        return this.baseMapper.selectOne(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByCompanyId(Long companyId) {
        QueryWrapper<LiveUser> query = new QueryWrapper<>();
        query.lambda().eq(LiveUser::getCompanyId, companyId);
        List<LiveUser> userList = this.list(query);

        if (userList != null && !userList.isEmpty()) {
            List<Long> userIds = userList.stream().map(LiveUser::getUserId).collect(Collectors.toList());

            // 删除 live_role
            QueryWrapper<LiveRole> roleQuery = new QueryWrapper<>();
            roleQuery.lambda().in(LiveRole::getUserId, userIds);
            liveRoleMapper.delete(roleQuery);

            // 删除业主账号
            this.remove(query);
        }
    }
}