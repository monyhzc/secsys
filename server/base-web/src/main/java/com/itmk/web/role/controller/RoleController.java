package com.itmk.web.role.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.web.role.entity.*;
import com.itmk.web.role.service.RoleService;
import com.itmk.web.role_menu.service.RoleMenuService;
import com.itmk.web.user.entity.User; // 引入User实体
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 角色控制器
 */
@RestController
@RequestMapping("/api/role")
public class RoleController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleMenuService roleMenuService;

    // 获取角色列表
    @GetMapping("/list")
    public ResultVo list(RoleParm parm) {
        // --- 权限隔离逻辑 ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 确保 Principal 是 User 类型
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            // 如果是普通物业管理员（有公司ID）
            if (currentUser.getCompanyId() != null) {
                // 强制只查自己公司的（Mapper中写了 OR company_id IS NULL，所以公共角色也能看到）
                parm.setCompanyId(currentUser.getCompanyId());
            }
            // 如果是平台管理员（companyId 为 NULL），则使用前端传来的 parm.companyId 进行筛选（如果有的话）
        }
        // ------------------

        IPage<Role> list = roleService.list(parm);
        return ResultUtils.success("查询成功", list);
    }

    // 新增角色
    @PostMapping
    public ResultVo addRole(@RequestBody Role role) {
        // --- 权限归属逻辑 ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            if (currentUser.getCompanyId() != null) {
                // 普通管理员：强制角色属于当前公司
                role.setCompanyId(currentUser.getCompanyId());
            } else {
                // 平台管理员：如果前端没传 companyId，默认设为 null (即公共角色，或者视业务需求而定)
                // 这里不需要额外操作，直接使用前端传来的 role.getCompanyId()
            }
        }
        // ------------------

        boolean save = roleService.save(role);
        if (save) {
            return ResultUtils.success("新增角色成功!");
        }
        return ResultUtils.error("新增角色失败!");
    }

    // 编辑角色
    @PutMapping
    public ResultVo editRole(@RequestBody Role role) {
        // 编辑时通常不修改归属公司，直接更新即可
        boolean save = roleService.updateById(role);
        if (save) {
            return ResultUtils.success("编辑角色成功!");
        }
        return ResultUtils.error("编辑角色失败!");
    }

    // 删除角色
    @DeleteMapping("/{roleId}")
    public ResultVo deleteRole(@PathVariable("roleId") Long roleId) {
        if (roleMenuService.selectBindRole(roleId)) {
            return ResultUtils.error("删除角色失败!该角色存在绑定用户！");
        }

        boolean save = roleService.removeById(roleId);
        if (save) {
            return ResultUtils.success("删除角色成功!");
        }
        return ResultUtils.error("删除角色失败!");
    }

    // 分配权限树回显
    @GetMapping("/getAssignTree")
    public ResultVo getAssignTree(RoleAssignParm parm) {
        RolePermissionVo assignTree = roleService.getAssignTree(parm);
        return ResultUtils.success("获取成功", assignTree);
    }

    // 分配权限保存
    @PostMapping("/saveAssign")
    public ResultVo saveAssign(@RequestBody RolePermissionParm parm) {
        roleService.saveAssign(parm);
        return ResultUtils.success("分配权限成功!");
    }
}