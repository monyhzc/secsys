package com.itmk.web.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.company.entity.SysCompany;
import com.itmk.web.company.mapper.SysCompanyMapper;
import com.itmk.web.company.service.SysCompanyService;
import com.itmk.web.house_building.service.HouseBuildingService;
import com.itmk.web.house_list.service.HouseListService;
import com.itmk.web.house_unit.service.HouseUnitService;
import com.itmk.web.live_user.service.LiveUserService;
import com.itmk.web.park_list.service.ParkListService;
import com.itmk.web.role.service.RoleService;
import com.itmk.web.sys_notice.service.SysNoticeService;
import com.itmk.web.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
public class SysCompanyServiceImpl extends ServiceImpl<SysCompanyMapper, SysCompany> implements SysCompanyService {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private SysNoticeService sysNoticeService;
    @Autowired
    private HouseBuildingService houseBuildingService;
    @Autowired
    private HouseUnitService houseUnitService;
    @Autowired
    private HouseListService houseListService;
    @Autowired
    private ParkListService parkListService;
    @Autowired
    private LiveUserService liveUserService;

    /**
     * 重写删除方法，实现级联删除
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(java.io.Serializable id) {
        Long companyId = (Long) id;

        // 1. 删除该公司下的员工 (级联删除 sys_user_role)
        userService.deleteByCompanyId(companyId);

        // 2. 删除该公司下的角色 (级联删除 sys_role_menu, sys_user_role, live_role)
        roleService.deleteByCompanyId(companyId);

        // 3. 删除该公司发布的公告
        sysNoticeService.deleteByCompanyId(companyId);

        // 4. 删除房屋资产体系 (注意删除顺序：房 -> 单元 -> 楼)
        // 4.1 删除房屋 (级联删除 live_house)
        houseListService.deleteByCompanyId(companyId);
        // 4.2 删除单元
        houseUnitService.deleteByCompanyId(companyId);
        // 4.3 删除楼栋
        houseBuildingService.deleteByCompanyId(companyId);

        // 5. 删除车位资产 (级联删除 live_park, fee_park)
        parkListService.deleteByCompanyId(companyId);

        // 6. 删除该公司的业主信息
        liveUserService.deleteByCompanyId(companyId);

        // 7. 最后删除公司本身
        return super.removeById(companyId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createCompany(SysCompany sysCompany) {
        // 使用重试机制，防止生成的 ID 偶尔重复
        int min = 100000;
        int max = 999999;
        long randomId = 0;
        boolean isUnique = false;

        // 尝试 5 次生成唯一 ID
        for (int i = 0; i < 5; i++) {
            randomId = min + new Random().nextInt(max - min);
            // 检查数据库中是否已存在
            if (this.getById(randomId) == null) {
                isUnique = true;
                break;
            }
        }

        if (!isUnique) {
            throw new RuntimeException("系统繁忙，生成公司ID失败，请重试！");
        }

        // 赋值生成的随机小 ID
        sysCompany.setCompanyId(randomId);

        return this.save(sysCompany);
    }
}