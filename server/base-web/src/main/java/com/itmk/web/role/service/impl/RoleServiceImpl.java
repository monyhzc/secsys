package com.itmk.web.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.live_role.entity.LiveRole;
import com.itmk.web.live_role.mapper.LiveRoleMapper;
import com.itmk.web.menu.entity.MakeMenuTree;
import com.itmk.web.menu.entity.Menu;
import com.itmk.web.menu.service.MenuService;
import com.itmk.web.role.entity.*;
import com.itmk.web.role.mapper.RoleMapper;
import com.itmk.web.role.service.RoleService;
import com.itmk.web.role_menu.entity.RoleMenu;
import com.itmk.web.role_menu.mapper.RoleMenuMapper;
import com.itmk.web.role_menu.service.RoleMenuService;
import com.itmk.web.user.entity.User;
import com.itmk.web.user.service.UserService;
import com.itmk.web.user_role.entity.UserRole;
import com.itmk.web.user_role.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private UserService userService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private RoleMenuService roleMenuService;
    @Resource
    private RoleMenuMapper roleMenuMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private LiveRoleMapper liveRoleMapper;

    @Override
    public IPage<Role> list(RoleParm parm) {
        // 构造分页对象
        IPage<Role> page = new Page<>();
        page.setSize(parm.getPageSize());
        page.setCurrent(parm.getCurrentPage());
        return this.baseMapper.getList(page, parm);
    }

    @Override
    public RolePermissionVo getAssignTree(RoleAssignParm parm) {
        // 查询当前用户的信息
        User user = userService.getById(parm.getUserId());
        // 查询权限
        List<Menu> list = null;
        if(user != null && user.getIsAdmin() != null && user.getIsAdmin().equals("1")){
            list = menuService.list();
        }else{
            list = menuService.getMenuByUserId(parm.getUserId());
        }
        // 组装成树的格式
        List<Menu> menusList = MakeMenuTree.makeTree(list, 0L);
        // 查询角色原来的权限
        List<Menu> roleList = menuService.getMenuByRoleId(parm.getRoleId());
        // 存角色原来权限的id
        List<Long> ids = new ArrayList<>();
        Optional.ofNullable(roleList).orElse(new ArrayList<>()).stream().filter(item -> item != null).forEach(item -> {
            ids.add(item.getMenuId());
        });
        
        RolePermissionVo vo = new RolePermissionVo();
        vo.setListmenu(menusList);
        vo.setCheckList(ids.toArray());
        return vo;
    }

    @Override
    public void saveAssign(RolePermissionParm parm) {
        roleMenuService.saveRoleMenu(parm.getRoleId(), parm.getList());
    }

    /**
     * 根据公司ID删除角色及其关联的所有数据（权限、员工关联、业主关联）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByCompanyId(Long companyId) {
        // 1. 查出该公司下的所有角色
        QueryWrapper<Role> query = new QueryWrapper<>();
        query.lambda().eq(Role::getCompanyId, companyId);
        List<Role> roles = this.list(query);

        if (roles != null && !roles.isEmpty()) {
            List<Long> roleIds = roles.stream().map(Role::getRoleId).collect(Collectors.toList());

            // 2. 删除 sys_role_menu (角色-菜单权限关联)
            QueryWrapper<RoleMenu> menuQuery = new QueryWrapper<>();
            menuQuery.lambda().in(RoleMenu::getRoleId, roleIds);
            roleMenuMapper.delete(menuQuery);

            // 3. 删除 sys_user_role (角色-员工关联)
            QueryWrapper<UserRole> userRoleQuery = new QueryWrapper<>();
            userRoleQuery.lambda().in(UserRole::getRoleId, roleIds);
            userRoleMapper.delete(userRoleQuery);

            // 4. 删除 live_role (角色-业主关联)
            QueryWrapper<LiveRole> liveRoleQuery = new QueryWrapper<>();
            liveRoleQuery.lambda().in(LiveRole::getRoleId, roleIds);
            liveRoleMapper.delete(liveRoleQuery);

            // 5. 删除角色本身
            this.remove(query);
        }
    }
}