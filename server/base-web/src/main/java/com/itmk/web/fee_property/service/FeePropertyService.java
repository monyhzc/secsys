package com.itmk.web.fee_property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.web.fee_property.entity.FeeProperty;

/**
 * 物业费Service
 */
public interface FeePropertyService extends IService<FeeProperty> {
    // 计算物业费
    FeeProperty calculateFee(FeeProperty feeProperty);
}
