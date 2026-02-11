package com.itmk.web.live_user.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AssignHouseParm implements Serializable {
    // 业主ID
    private Long userId;
    
    // 房屋ID (用于回显)
    private Long houseId;
    
    // 车位ID (用于回显)
    private Long parkId;
    
    // ID集合 (用于接收前端分配的房屋或车位ID列表)
    private List<Long> list;
}