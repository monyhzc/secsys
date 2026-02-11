package com.itmk.config.mybatis;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.itmk.web.user.entity.User;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 多租户处理器
 */
@Component
public class CompanyTenantHandler implements TenantLineHandler {

    @Override
    public Expression getTenantId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            if (user.getCompanyId() != null) {
                return new LongValue(user.getCompanyId());
            }
        }
        return new LongValue(0); 
    }

    @Override
    public String getTenantIdColumn() {
        return "company_id";
    }

    @Override
    public boolean ignoreTable(String tableName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            // 平台管理员 (companyId 为空) 查看所有数据
            if (user.getCompanyId() == null) {
                return true; 
            }
        } else {
            // 未登录时（如登录查询），不进行过滤
            return true;
        }

        // 需要进行隔离的表
        if ("sys_role".equalsIgnoreCase(tableName)) return true;
        if ("house_list".equalsIgnoreCase(tableName)) return false;
        if ("house_building".equalsIgnoreCase(tableName)) return false;
        if ("house_unit".equalsIgnoreCase(tableName)) return false;
        if ("parking_list".equalsIgnoreCase(tableName)) return false;
        if ("live_user".equalsIgnoreCase(tableName)) return false;
        if ("sys_notice".equalsIgnoreCase(tableName)) return false;
        if ("work_order".equalsIgnoreCase(tableName)) return false;
        if ("fee_park".equalsIgnoreCase(tableName)) return false;
        if ("live_house".equalsIgnoreCase(tableName)) return false;
        if ("live_park".equalsIgnoreCase(tableName)) return false;

        // sys_user 表暂时忽略，以免影响登录。列表查询需手动处理
        return true; 
    }
}
