package com.itmk.web.live_user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.web.fee_park.entity.FeePark;
import com.itmk.web.fee_park.service.FeeParkService;
import com.itmk.web.live_user.entity.AssignHouseParm;
import com.itmk.web.live_user.entity.LiveUser;
import com.itmk.web.live_user.entity.LiveUserParm;
import com.itmk.web.live_user.service.LiveUserService;
import com.itmk.web.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 业主管理控制器
 */
@RestController
@RequestMapping("/api/liveUser")
public class LiveUserController {
    @Autowired
    private LiveUserService liveUserService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    // 保留停车费服务，用于删除时的欠费检查
    @Autowired
    private FeeParkService feeParkService;

    /**
     * 分页查询业主列表
     */
    @GetMapping("/list")
    public ResultVo list(LiveUserParm parm){
        IPage<LiveUser> list = liveUserService.getList(parm);
        return ResultUtils.success("查询成功", list);
    }

    /**
     * 新增业主
     */
    @PostMapping
    @PreAuthorize("hasAuthority('sys:liveUser:add')")
    public ResultVo add(@RequestBody LiveUser liveUser){
        // 判断账号是否被占用
        QueryWrapper<LiveUser> query = new QueryWrapper<>();
        query.lambda().eq(LiveUser::getUsername, liveUser.getUsername());
        LiveUser one = liveUserService.getOne(query);
        if(one != null){
            return ResultUtils.error("账号被占用!");
        }
        // 判断电话是否被占用
        QueryWrapper<LiveUser> queryPhone = new QueryWrapper<>();
        queryPhone.lambda().eq(LiveUser::getPhone, liveUser.getPhone());
        LiveUser phoneUser = liveUserService.getOne(queryPhone);
        if(phoneUser != null){
            return ResultUtils.error("电话被占用!");
        }

        // 处理密码加密 (默认密码123456，或者前端传来的密码)
        // 这里的加密方式取决于您的 SpringSecurityConfig 配置，通常用 BCryptPasswordEncoder
        // 如果原代码使用的是 MD5 (DigestUtils)，请保持原样；这里假设用 Spring Security 标准加密
        String password = liveUser.getPassword();
        if (password == null || password.isEmpty()) {
            password = "666666"; // 默认密码
        }
        liveUser.setPassword(passwordEncoder.encode(password));
        
        // 设置默认状态
        liveUser.setStatus("0");

        // 权限隔离逻辑
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            if (currentUser.getCompanyId() != null) {
                liveUser.setCompanyId(currentUser.getCompanyId());
            } else if (liveUser.getCompanyId() == null) {
                return ResultUtils.error("平台管理员必须选择所属公司");
            }
        }

        boolean save = liveUserService.save(liveUser);
        if(save){
            return ResultUtils.success("新增业主成功");
        }
        return ResultUtils.error("新增业主失败");
    }

    /**
     * 编辑业主
     */
    @PutMapping
    @PreAuthorize("hasAuthority('sys:liveUser:edit')")
    public ResultVo edit(@RequestBody LiveUser liveUser){
        // 编辑时判断账号是否重复（排除自己）
        QueryWrapper<LiveUser> query = new QueryWrapper<>();
        query.lambda().eq(LiveUser::getUsername, liveUser.getUsername())
                      .ne(LiveUser::getUserId, liveUser.getUserId());
        LiveUser one = liveUserService.getOne(query);
        if(one != null){
            return ResultUtils.error("账号被占用!");
        }
        
        // 编辑时通常不修改密码，除非有单独的接口
        // 如果前端传了空密码，则设为null防止覆盖原密码（MyBatisPlus updateById 默认策略通常是不更新 null 字段，或者手动处理）
        liveUser.setPassword(null); 

        boolean update = liveUserService.updateById(liveUser);
        if(update){
            return ResultUtils.success("编辑业主成功");
        }
        return ResultUtils.error("编辑业主失败");
    }

    /**
     * 删除业主
     * 删除前检查：是否有未缴清的停车费
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('sys:liveUser:delete')")
    public ResultVo deleteUser(@PathVariable("userId") Long userId){
        // 1. 查询停车费欠费情况
        // 逻辑：在 fee_park 表中，如果存在 userId = 当前ID 且 payParkStatus = '0' (未缴费) 的记录，则不能删除
        QueryWrapper<FeePark> queryPark = new QueryWrapper<>();
        queryPark.lambda().eq(FeePark::getUserId, userId)
                          .eq(FeePark::getPayParkStatus, "0");
        int count = feeParkService.count(queryPark);
        if(count > 0){
            return ResultUtils.error("该用户有未缴清的停车费，不能删除!");
        }

        // 2. 执行删除
        // (注：liveUserService.removeById 应该已经配置了逻辑删除或级联清理关联表，或者依靠数据库外键)
        boolean b = liveUserService.removeById(userId);
        if(b){
            return ResultUtils.success("删除成功");
        }
        return ResultUtils.error("删除失败");
    }

    /**
     * 查询业主关联的房屋
     */
    @GetMapping("/getHouseByUserId")
    public ResultVo getHouseByUserId(AssignHouseParm parm){
        AssignHouseParm house = liveUserService.getHouseByUserId(parm);
        return ResultUtils.success("查询成功", house);
    }

    /**
     * 查询业主关联的车位
     */
    @GetMapping("/getParkByUserId")
    public ResultVo getParkByUserId(AssignHouseParm parm){
        AssignHouseParm park = liveUserService.getParkByUserId(parm);
        return ResultUtils.success("查询成功", park);
    }

    /**
     * 分配房屋保存
     */
    @PostMapping("/assignHouse")
    @PreAuthorize("hasAuthority('sys:liveUser:assignHouse')")
    public ResultVo assignHouse(@RequestBody AssignHouseParm parm){
        liveUserService.saveHouse(parm);
        return ResultUtils.success("分配房屋成功");
    }

    /**
     * 分配车位保存
     */
    @PostMapping("/assignPark")
    @PreAuthorize("hasAuthority('sys:liveUser:assignPark')")
    public ResultVo assignPark(@RequestBody AssignHouseParm parm){
        liveUserService.savePark(parm);
        return ResultUtils.success("分配车位成功");
    }
    
    /**
     * 重置密码
     */
    @PostMapping("/resetPassword")
    @PreAuthorize("hasAuthority('sys:liveUser:resetPwd')")
    public ResultVo resetPassword(@RequestBody LiveUser liveUser){
        String newPwd = "666666"; // 默认重置密码
        // 加密
        String encode = passwordEncoder.encode(newPwd);
        
        LiveUser update = new LiveUser();
        update.setUserId(liveUser.getUserId());
        update.setPassword(encode);
        
        boolean b = liveUserService.updateById(update);
        if(b){
            return ResultUtils.success("重置密码成功，新密码为：666666");
        }
        return ResultUtils.error("重置密码失败");
    }

    @GetMapping("/getUserById")
    public ResultVo getUserById(LiveUser liveUser) {
        // 调用 service 获取包含关联信息（如角色、房屋）的业主详情
        LiveUser user = liveUserService.getUser(liveUser.getUserId());
        return ResultUtils.success("查询成功", user);
    }
}