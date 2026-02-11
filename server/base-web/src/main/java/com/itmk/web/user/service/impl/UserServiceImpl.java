package com.itmk.web.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.user.entity.User;
import com.itmk.web.user.entity.UserParm;
import com.itmk.web.user.mapper.UserMapper;
import com.itmk.web.user.service.UserService;
import com.itmk.web.user_role.entity.UserRole;
import com.itmk.web.user_role.mapper.UserRoleMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public IPage<User> list(UserParm parm) {
        // 构造分页对象
        IPage<User> page = new Page<>();
        page.setSize(parm.getPageSize());
        page.setCurrent(parm.getCurrentPage());

        QueryWrapper<User> query = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(parm.getLoginName())) {
            query.lambda().like(User::getLoginName, parm.getLoginName());
        }
        
        // 构造数据权限隔离
        // 如果是超级管理员(companyId为null)，可以看所有；如果是物业管理员，只能看自己公司的
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long companyId = null;
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            if (currentUser.getCompanyId() != null) {
                companyId = currentUser.getCompanyId();
            }
        }
        
        // 调用自定义 Mapper 进行查询 (UserMapper.xml)
        return this.baseMapper.getList(page, parm, companyId);
    }

    @Override
    public UserRole getRoleByUserId(UserRole userRole) {
        QueryWrapper<UserRole> query = new QueryWrapper<>();
        query.lambda().eq(UserRole::getUserId, userRole.getUserId());
        return userRoleMapper.selectOne(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRole(UserRole userRole) {
        // 先把原来的角色删除
        QueryWrapper<UserRole> query = new QueryWrapper<>();
        query.lambda().eq(UserRole::getUserId, userRole.getUserId());
        userRoleMapper.delete(query);
        // 把新的角色保存
        userRoleMapper.insert(userRole);
    }

    @Override
    public User loadUser(String username) {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.lambda().eq(User::getUsername, username);
        return this.baseMapper.selectOne(query);
    }

    /**
     * 根据公司ID删除员工及其关联数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByCompanyId(Long companyId) {
        // 1. 查询该公司的所有员工
        QueryWrapper<User> query = new QueryWrapper<>();
        query.lambda().eq(User::getCompanyId, companyId);
        List<User> userList = this.list(query);

        if (userList != null && !userList.isEmpty()) {
            // 提取员工ID列表
            List<Long> userIds = userList.stream().map(User::getUserId).collect(Collectors.toList());

            // 2. 删除 sys_user_role 中这些员工的关联记录
            QueryWrapper<UserRole> roleQuery = new QueryWrapper<>();
            roleQuery.lambda().in(UserRole::getUserId, userIds);
            userRoleMapper.delete(roleQuery);

            // 3. 删除员工账号
            this.remove(query);
        }
    }
}