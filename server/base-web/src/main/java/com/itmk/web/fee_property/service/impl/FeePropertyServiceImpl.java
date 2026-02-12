package com.itmk.web.fee_property.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.fee_property.entity.FeeProperty;
import com.itmk.web.fee_property.mapper.FeePropertyMapper;
import com.itmk.web.fee_property.service.FeePropertyService;
import com.itmk.web.fee_property_rule.entity.FeePropertyRule;
import com.itmk.web.fee_property_rule.service.FeePropertyRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 物业费Service实现
 */
@Service
public class FeePropertyServiceImpl extends ServiceImpl<FeePropertyMapper, FeeProperty> implements FeePropertyService {
    @Autowired
    private FeePropertyRuleService feePropertyRuleService;

    /**
     * 计算物业费
     */
    @Override
    public FeeProperty calculateFee(FeeProperty feeProperty) {
        // 获取规则
        FeePropertyRule rule = feePropertyRuleService.getById(feeProperty.getRuleId());
        if (rule == null) {
            throw new RuntimeException("规则不存在");
        }

        // 计算基础物业费
        BigDecimal baseMoney = feeProperty.getHouseArea().multiply(rule.getPricePerSquare()).setScale(2, RoundingMode.HALF_UP);
        feeProperty.setBaseMoney(baseMoney);

        // 计算滞纳金（这里简化处理，实际应该根据具体的滞纳天数计算）
        BigDecimal lateFee = baseMoney.multiply(rule.getLateFeeRate()).setScale(2, RoundingMode.HALF_UP);
        feeProperty.setLateFee(lateFee);

        // 计算总金额
        BigDecimal totalMoney = baseMoney.add(lateFee).setScale(2, RoundingMode.HALF_UP);
        feeProperty.setTotalMoney(totalMoney);

        return feeProperty;
    }
}
