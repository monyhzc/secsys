package com.itmk.web.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.config.jwt.JwtUtils;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.web.live_user.entity.LiveUser;
import com.itmk.web.live_user.service.LiveUserService;
import com.itmk.web.menu.entity.MakeMenuTree;
import com.itmk.web.menu.entity.Menu;
import com.itmk.web.menu.entity.RouterVO;
import com.itmk.web.menu.service.MenuService;
import com.itmk.web.user.entity.*;
import com.itmk.web.user.mapper.UserMapper; // 引入UserMapper以便调用自定义查询
import com.itmk.web.user.service.UserService;
import com.itmk.web.user_role.entity.UserRole;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 员工管理的控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;

    @Resource
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MenuService menuService;
    @Autowired
    private LiveUserService liveUserService;

    /**
     * 重置密码
     */
    @PostMapping("/resetPassword")
    public ResultVo resetPassword(@RequestBody ChangePassword user, HttpServletRequest request) {
        //获取token
        String token = request.getHeader("token");
        Claims claims = jwtUtils.getClaimsFromToken(token);
        //判断用户类型
        Object userType = claims.get("userType");
        if (userType.equals("0")) {//0：业主
            LiveUser liveUser = liveUserService.getById(user.getUserId());
            //原来的密码
            String dataOldPassword = liveUser.getPassword();
            boolean encode = passwordEncoder.matches(user.getOldPassword(), dataOldPassword);
            if (!encode) {
                return ResultUtils.error("旧密码错误!");
            }
            LiveUser liveUser1 = new LiveUser();
            liveUser1.setUserId(user.getUserId());
            liveUser1.setPassword(passwordEncoder.encode(user.getNewPassword()));
            boolean b = liveUserService.updateById(liveUser1);
            if (b) {
                return ResultUtils.success("密码修改成功!");
            }
            return ResultUtils.error("密码修改失败!");
        } else {
            User liveUser = userService.getById(user.getUserId());
            //原来的密码
            String dataOldPassword = liveUser.getPassword();
            boolean encode = passwordEncoder.matches(user.getOldPassword(), dataOldPassword);
            if (!encode) {
                return ResultUtils.error("旧密码错误!");
            }
            User liveUser1 = new User();
            liveUser1.setUserId(user.getUserId());
            liveUser1.setPassword(passwordEncoder.encode(user.getNewPassword()));
            boolean b = userService.updateById(liveUser1);
            if (b) {
                return ResultUtils.success("密码修改成功!");
            }
            return ResultUtils.error("密码修改失败!");
        }
    }

    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode("123456");
        System.out.println(encode);
    }

    /**
     * 退出登录
     */
    @PostMapping("/loginOut")
    public ResultVo loginOut(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return ResultUtils.success("退出登录成功!");
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/getInfo")
    public ResultVo getInfo(User user, HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims claims = jwtUtils.getClaimsFromToken(token);
        Object userType = claims.get("userType");

        UserInfo userInfo = new UserInfo();
        userInfo.setAvatar("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");

        if (userType.equals("0")) { // 业主
            LiveUser liveUser = liveUserService.getById(user.getUserId());
            userInfo.setId(liveUser.getUserId());
            userInfo.setName(liveUser.getUsername());
            List<Menu> menuList = menuService.getMenuByUserIdForLiveUser(liveUser.getUserId());
            List<String> collect = menuList.stream().map(Menu::getMenuCode).filter(code -> code != null).collect(Collectors.toList());
            userInfo.setRoles(collect.toArray(new String[0]));
        } else { // 管理员
            User user1 = userService.getById(user.getUserId());
            userInfo.setId(user1.getUserId());
            userInfo.setName(user1.getUsername());
            
            // 【关键修复】设置 companyId，前端靠这个判断是否为平台管理员
            userInfo.setCompanyId(user1.getCompanyId());

            List<Menu> menuList = menuService.getMenuByUserId(user.getUserId());
            List<String> collect = menuList.stream().map(Menu::getMenuCode).filter(code -> code != null).collect(Collectors.toList());
            userInfo.setRoles(collect.toArray(new String[0]));
        }
        return ResultUtils.success("获取用户信息成功", userInfo);
    }

    /**
     * 获取菜单列表
     */
    @GetMapping("/getMenuList")
    public ResultVo getMenuList(HttpServletRequest request) {
        String token = request.getHeader("token");
        String username = jwtUtils.getUsernameFromToken(token);
        Claims claims = jwtUtils.getClaimsFromToken(token);
        Object userType = claims.get("userType");

        if (userType.equals("0")) { // 业主
            LiveUser liveUser = liveUserService.loadUser(username);
            List<Menu> menuList = menuService.getMenuByUserIdForLiveUser(liveUser.getUserId());
            List<Menu> collect = menuList.stream().filter(item -> item != null && !item.getType().equals("2")).collect(Collectors.toList());
            return ResultUtils.success("查询成功", MakeMenuTree.makeRouter(collect, 0L));
        } else { // 管理员
            User user = userService.loadUser(username);
            List<Menu> menuList = menuService.getMenuByUserId(user.getUserId());
            List<Menu> collect = menuList.stream().filter(item -> item != null && !item.getType().equals("2")).collect(Collectors.toList());

            // 【关键修复】如果是普通物业管理员(companyId不为空)，强制移除"公司管理"菜单
            // 假设 "公司管理" 的 menuCode 是 "sys:companyList"
            if (user.getCompanyId() != null) {
                collect = collect.stream()
                        .filter(item -> item.getMenuCode() == null || !item.getMenuCode().equals("sys:companyList"))
                        .collect(Collectors.toList());
            }

            return ResultUtils.success("查询成功", MakeMenuTree.makeRouter(collect, 0L));
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResultVo login(@RequestBody LoginParm parm) {
        if (StringUtils.isEmpty(parm.getUsername()) || StringUtils.isEmpty(parm.getPassword()) || StringUtils.isEmpty(parm.getUserType())) {
            return ResultUtils.error("用户名、密码或用户类型不能为空!");
        }
        //加密后的密码
        String encode = passwordEncoder.encode(parm.getPassword());
        //spring security需要的token
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(parm.getUsername() + ":" + parm.getUserType(), parm.getPassword());
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        if (parm.getUserType().equals("0")) {
            LiveUser user = (LiveUser) authenticate.getPrincipal();
            String token = jwtUtils.generateToken(parm.getUsername(), parm.getUserType());
            //获取token过期的时间
            Long time = jwtUtils.getExpireTime(token);
            LoginResult result = new LoginResult();
            result.setUserId(user.getUserId());
            result.setToken(token);
            result.setExpireTime(time);
            return ResultUtils.success("登录成功", result);
        } else {
            User user = (User) authenticate.getPrincipal();
            String token = jwtUtils.generateToken(parm.getUsername(), parm.getUserType());
            //获取token过期的时间
            Long time = jwtUtils.getExpireTime(token);
            LoginResult result = new LoginResult();
            result.setUserId(user.getUserId());
            result.setToken(token);
            result.setExpireTime(time);

            System.out.println(ResultUtils.success("登录成功", result) + "-------------------");
            return ResultUtils.success("登录成功", result);
        }
    }

    /**
     * 新增员工
     * 【修改】：增加公司ID的自动填充与校验，以及角色关联的保存
     * @param user
     * @return
     */
    @PostMapping
    @PreAuthorize("hasAuthority('sys:user:add')")
    public ResultVo addUser(@RequestBody User user) {
        //判断登录名 的唯一性
        if (StringUtils.isNotEmpty(user.getUsername())) {
            QueryWrapper<User> query = new QueryWrapper<>();
            query.lambda().eq(User::getUsername, user.getUsername());
            User one = userService.getOne(query);
            if (one != null) {
                return ResultUtils.error("登录名已经被占用!", 500);
            }
        }

        // --- 新增逻辑：处理公司归属 ---
        // 获取当前操作员信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        
        if (currentUser.getCompanyId() != null) {
            // 1. 如果是普通物业管理员，强制新增的员工属于当前公司
            user.setCompanyId(currentUser.getCompanyId());
        } else {
            // 2. 如果是平台管理员，校验是否选择了公司
            if (user.getCompanyId() == null) {
                return ResultUtils.error("平台管理员新增员工时，必须选择所属物业公司!");
            }
        }
        // ---------------------------

        //如果密码存在，MD5加密
        if (StringUtils.isNotEmpty(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // 默认密码
            user.setPassword(passwordEncoder.encode("666666"));
        }
        boolean save = userService.save(user);
        if (save) {
            // --- 新增代码 Start: 保存员工角色关联 ---
            //【修改点】：保存用户成功后，如果有角色ID，则保存用户角色关系
            if (user.getRoleId() != null) {
                UserRole userRole = new UserRole();
                userRole.setUserId(user.getUserId());
                userRole.setRoleId(user.getRoleId());
                userService.saveRole(userRole);
            }
            // --- 新增代码 End ---
            return ResultUtils.success("新增员工成功");
        }
        return ResultUtils.error("新增员工失败");
    }

    /**
     * 编辑员工
     *
     * @param user
     * @return
     */
    @PreAuthorize("hasAuthority('sys:user:edit')")
    @PutMapping
    public ResultVo editUser(@RequestBody User user) {
        //判断登录名 的唯一性
        if (StringUtils.isNotEmpty(user.getUsername())) {
            QueryWrapper<User> query = new QueryWrapper<>();
            query.lambda().eq(User::getUsername, user.getUsername());
            User one = userService.getOne(query);
            if (one != null && !one.getUserId().equals(user.getUserId())) {
                return ResultUtils.error("登录名已经被占用!", 500);
            }
        }
        //如果密码存在，MD5加密
        if (StringUtils.isNotEmpty(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        boolean b = userService.updateById(user);
        if (b) {
            return ResultUtils.success("编辑员工成功");
        }
        return ResultUtils.error("编辑员工失败");
    }

    /**
     * 删除员工
     *
     * @param userId
     * @return
     */
    @PreAuthorize("hasAuthority('sys:user:delete')")
    @DeleteMapping("/{userId}")
    public ResultVo deleteUser(@PathVariable("userId") Long userId) {
        boolean b = userService.removeById(userId);
        if (b) {
            return ResultUtils.success("删除员工成功");
        }
        return ResultUtils.error("删除员工失败");
    }

    /**
     * 查询员工列表
     * 【修改】：改为调用 Mapper 的关联查询，并增加数据权限隔离
     * @param parm
     * @return
     */
    @GetMapping("/list")
    public ResultVo list(UserParm parm) {
        try {
            // 构造分页对象
            IPage<User> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
            
            // 数据权限隔离ID，默认为null（查所有）
            Long dataScopeCompanyId = null;

            // 获取当前登录用户信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User currentUser = (User) authentication.getPrincipal();
                // 如果有公司ID，说明是物业管理员，只能查自己公司
                if (currentUser.getCompanyId() != null) {
                    dataScopeCompanyId = currentUser.getCompanyId();
                }
            }

            // 调用自定义 Mapper 方法 (UserMapper.xml 中定义的 LEFT JOIN 查询)
            // 注意：这里将 BaseMapper 强转为 UserMapper 接口
            UserMapper userMapper = (UserMapper) userService.getBaseMapper();
            IPage<User> list = userMapper.getList(page, parm, dataScopeCompanyId);

            //前端不展示密码
            list.getRecords().stream().forEach(item -> item.setPassword(""));
            return ResultUtils.success("查询成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据用户id查询角色
     */
    @GetMapping("/getRoleByUserId")
    public ResultVo getRoleByUserId(UserRole userRole) {
        UserRole userRole1 = userService.getRoleByUserId(userRole);
        return ResultUtils.success("查询成功", userRole1);
    }

    /**
     * 保存用户角色
     */

    @PostMapping("/saveRole")
    public ResultVo saveRole(@RequestBody UserRole userRole) {
        userService.saveRole(userRole);
        return ResultUtils.success("分配角色成功!");
    }
}