package com.itmk.web.company.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("sys_company")
public class SysCompany {
    @TableId(type = IdType.AUTO)
    private Long companyId;
    private String companyName;
    private String contactPerson;
    private String contactPhone;
    // 0: Enabled, 1: Disabled
    private String status;
    private Date createTime;
}
