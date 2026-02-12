package com.itmk.web.fee_property.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 物业费实体类
 */
@Data
@TableName("fee_property")
public class FeeProperty {
    @TableId(type = IdType.AUTO)
    private Long propertyFeeId;
    private Long userId;
    private Long companyId;
    private Long houseId;
    private Long ruleId;
    private String payPropertyMonth;
    private BigDecimal houseArea;
    private BigDecimal baseMoney;
    private BigDecimal lateFee;
    private BigDecimal totalMoney;
    private String payPropertyStatus;
    private Date payPropertyTime;
}
