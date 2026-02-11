package com.itmk.web.live_user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.web.live_user.entity.AssignHouseParm;
import com.itmk.web.live_user.entity.LiveUser;
import com.itmk.web.live_user.entity.LiveUserParm;

public interface LiveUserService extends IService<LiveUser> {
    // 分页查询
    IPage<LiveUser> getList(LiveUserParm parm);
    
    // 新增业主
    void saveLiveUser(LiveUser liveUser);
    
    // 编辑业主
    void editLiveUser(LiveUser liveUser);
    
    // 根据ID查询业主详情（用于编辑回显）
    LiveUser getUser(Long userId);

    // 保存房屋分配
    void saveHouse(AssignHouseParm parm);
    
    // 保存车位分配
    void savePark(AssignHouseParm parm);
    
    // 根据业主id查询回显房屋数据
    AssignHouseParm getHouseByUserId(AssignHouseParm parm);
    
    // 根据业主id查询回显车位数据
    AssignHouseParm getParkByUserId(AssignHouseParm parm);
    
    // 根据用户名查询用户信息
    LiveUser loadUser(String username);

    // 根据公司ID删除所有业主及关联数据
    void deleteByCompanyId(Long companyId);
}