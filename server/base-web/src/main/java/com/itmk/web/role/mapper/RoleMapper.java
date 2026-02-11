package com.itmk.web.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itmk.web.role.entity.Role;
import com.itmk.web.role.entity.RoleParm;
import org.apache.ibatis.annotations.Param;

public interface RoleMapper extends BaseMapper<Role> {
    
    /**
     * 自定义角色列表查询
     */
    IPage<Role> getList(IPage<Role> page, @Param("parm") RoleParm parm);
}