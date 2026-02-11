package com.itmk.config.security.customerdetailservice;

import com.itmk.config.security.exception.CustomerAuthenionException;
import com.itmk.web.company.entity.SysCompany;
import com.itmk.web.company.service.SysCompanyService;
import com.itmk.web.live_user.entity.LiveUser;
import com.itmk.web.live_user.service.LiveUserService;
import com.itmk.web.menu.entity.Menu;
import com.itmk.web.menu.service.MenuService;
import com.itmk.web.user.entity.User;
import com.itmk.web.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义UserDetailsService
 * 目的：告诉spring security如何查询用户信息
 */
@Component("custometUserDetailService") //把该类交给spring进行管理
public class CustometUserDetailService implements UserDetailsService {
    @Autowired
    private UserService userService;
    @Autowired
    private LiveUserService liveUserService;
    @Autowired
    private MenuService menuService;
    // 【新增】注入公司服务，用于查询公司状态
    @Autowired
    private SysCompanyService sysCompanyService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        int index = s.indexOf(":");     //获取用户类型  //0：业主  1： 物主 2: 平台运营
        String username = s.substring(0, index);  //账户
        String userType = s.substring(index + 1, s.length());    //用户类型
        if (userType.equals("0")) { //业主
            LiveUser liveUser = liveUserService.loadUser(username);
            if (liveUser == null) {
                throw new UsernameNotFoundException("用户账户不存在！");
            }
            if (liveUser.getStatus().equals("1")) {
                throw new CustomerAuthenionException("账户被禁用，请联系管理员!");
            }
            //查询业主的权限信息
            List<Menu> menuList = menuService.getMenuByUserIdForLiveUser(liveUser.getUserId());
            //获取权限字段
            List<String> collect = menuList.stream().filter(item -> item != null).map(item -> item.getMenuCode()).filter(item -> item != null).collect(Collectors.toList());
            //转成数组
            String[] strings = collect.toArray(new String[collect.size()]);
            List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(strings);
            //设置用户的权限
            liveUser.setAuthorities(authorityList);
            return liveUser;
        } else if (userType.equals("1") || userType.equals("2")) { // 1:物业管理员, 2:平台运营
            // 统一从 sys_user 表加载
            User user = userService.loadUser(username);

            System.out.println("Login attempt: username=" + username + ", type=" + userType);
            
            // 【移除严格校验】不再根据 companyId 强制区分平台和物业，允许直接登录
            if (user == null) {
                System.out.println("User NOT found in DB");
                throw new UsernameNotFoundException("用户账户不存在！");
            }

            if (user.getIsUsed().equals("1")) {
                throw new CustomerAuthenionException("账户被禁用，请联系管理员!");
            }

            // 【新增逻辑】如果是物业管理员(userType=1)，需要检查所属公司是否过期或被禁用
            if ("1".equals(userType) && user.getCompanyId() != null) {
                SysCompany company = sysCompanyService.getById(user.getCompanyId());
                if (company != null) {
                    // 假设 "1" 表示禁用 (参考 sysCompanyService 中的逻辑)
                    if ("1".equals(company.getStatus())) {
                        throw new CustomerAuthenionException("您所属的物业公司已被停用，请联系平台管理员!");
                    }
                    // 检查过期时间
                    if (company.getExpireTime() != null && company.getExpireTime().before(new Date())) {
                        throw new CustomerAuthenionException("您所属的物业公司服务已到期，请联系平台管理员续费!");
                    }
                }
            }

            //查询用户权限信息
            List<Menu> menuList = menuService.getMenuByUserId(user.getUserId());
            //获取权限字段
            List<String> collect = menuList.stream().filter(item -> item != null).map(item -> item.getMenuCode()).filter(item -> item != null).collect(Collectors.toList());
            //转成数组
            String[] strings = collect.toArray(new String[collect.size()]);
            List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(strings);
            //设置用户的权限
            user.setAuthorities(authorityList);
            return user;
        } else {
            throw new UsernameNotFoundException("用户类型不存在");
        }
    }
}