package com.maigeiye.seckillsystem.service;

import com.maigeiye.seckillsystem.error.BusinessException;
import com.maigeiye.seckillsystem.service.model.OrderModel;

public interface OrderService {
    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount, String stockLogId) throws BusinessException;
}
