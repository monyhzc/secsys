package com.itmk.web.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itmk.web.user.entity.User;
import com.itmk.web.user.entity.UserParm;
import org.apache.ibatis.annotations.Param;

/**
 * 员工管理数据访问层
 */
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 分页查询用户列表（关联公司表）
     * @param page 分页对象
     * @param parm 查询参数
     * @param dataScopeCompanyId 数据权限CompanyId，如果为null则查询所有，不为null则只查该公司
     * @return
     */
    IPage<User> getList(IPage<User> page, @Param("parm") UserParm parm, @Param("dataScopeCompanyId") Long dataScopeCompanyId);
}