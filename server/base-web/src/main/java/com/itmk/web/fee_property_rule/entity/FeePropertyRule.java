package com.itmk.web.fee_property_rule.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 物业费规则实体类
 */
@Data
@TableName("fee_property_rule")
public class FeePropertyRule {
    @TableId(type = IdType.AUTO)
    private Long ruleId;
    private Long companyId;
    private String ruleName;
    private BigDecimal pricePerSquare;
    private BigDecimal lateFeeRate;
    private Integer lateFeeDays;
    private String status;
}
