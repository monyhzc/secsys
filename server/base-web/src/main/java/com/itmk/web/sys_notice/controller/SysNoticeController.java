package com.itmk.web.sys_notice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.web.sys_notice.entity.SysNotice;
import com.itmk.web.sys_notice.entity.SysNoticeParm;
import com.itmk.web.sys_notice.service.SysNoticeService;
import com.itmk.web.user.entity.User;
// 引入 LiveUser 实体，路径根据你的项目结构调整
import com.itmk.web.live_user.entity.LiveUser; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 公告管理模块
 */
@RestController
@RequestMapping("/api/sysNotice")
public class SysNoticeController {
    
    @Autowired
    private SysNoticeService sysNoticeService;

    /**
     * 查询列表 (修复隔离问题)
     */
    @GetMapping("/list")
    public ResultVo getList(SysNoticeParm parm){
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        // 【新增】判断用户类型并设置 companyId
        if (principal instanceof User) {
            // 1. 如果是后台管理员 (Property Admin / Platform Admin)
            User user = (User) principal;
            if (user.getCompanyId() != null) {
                parm.setCompanyId(user.getCompanyId());
            }
        } else if (principal instanceof LiveUser) {
            // 2. 【核心修复】如果是业主 (LiveUser)
            LiveUser liveUser = (LiveUser) principal;
            // 注意：请确保 LiveUser 实体中有 getCompanyId() 方法。
            // 如果没有，需要注入 liveUserService 根据 liveUser.getUserId() 查询归属公司。
            if (liveUser.getCompanyId() != null) {
                parm.setCompanyId(liveUser.getCompanyId());
            }
        }

        IPage<SysNotice> list = sysNoticeService.getList(parm);
        return ResultUtils.success("查询成功", list);
    }

    /**
     * 【新增/修复】获取最新一条公告 (用于首页展示)
     */
    @GetMapping("/one")
    public ResultVo getOne(){
        Long companyId = null;

        // 获取当前登录用户身份
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        // 判断身份提取 companyId
        if (principal instanceof User) {
            User user = (User) principal;
            companyId = user.getCompanyId();
        } else if (principal instanceof LiveUser) {
            LiveUser liveUser = (LiveUser) principal;
            companyId = liveUser.getCompanyId();
        }

        // 调用 Service，传入 companyId 进行过滤
        SysNotice one = sysNoticeService.getOne(companyId);
        
        // 判空处理，防止没有公告时前端报错
        if(one != null) {
            return ResultUtils.success("查询成功", one);
        } else {
            return ResultUtils.success("暂无公告", null);
        }
    }

    /**
     * 新增公告 (保持原有逻辑，略微优化判空)
     */
    @PostMapping
    @PreAuthorize("hasAuthority('sys:noticeList:add')")
    public ResultVo add(@RequestBody SysNotice sysNotice){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            User currentUser = (User) principal;
            sysNotice.setUserId(currentUser.getUserId());
            
            if (currentUser.getCompanyId() != null) {
                sysNotice.setCompanyId(currentUser.getCompanyId());
            } else {
                // 平台管理员
                if (sysNotice.getCompanyId() == null) {
                    return ResultUtils.error("平台管理员发布公告必须选择所属公司！");
                }
            }
        }

        sysNotice.setCreateTime(new Date());
        boolean b = sysNoticeService.save(sysNotice);
        if(b){
            return ResultUtils.success("新增成功!");
        }
        return ResultUtils.error("新增失败!");
    }

    /**
     * 编辑 (保持不变)
     */
    @PutMapping
    @PreAuthorize("hasAuthority('sys:noticeList:edit')")
    public ResultVo edit(@RequestBody SysNotice sysNotice){
        boolean b = sysNoticeService.updateById(sysNotice);
        if(b){
            return ResultUtils.success("编辑成功!");
        }
        return ResultUtils.error("编辑失败!");
    }

    /**
     * 删除 (保持不变)
     */
    @PreAuthorize("hasAuthority('sys:noticeList:delete')")
    @DeleteMapping("/{noticeId}")
    public ResultVo delete(@PathVariable("noticeId") String noticeId){
        boolean b = sysNoticeService.removeById(noticeId);
        if(b){
            return ResultUtils.success("删除成功!");
        }
        return ResultUtils.error("删除失败!");
    }
}