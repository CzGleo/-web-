package com.czg.seckillsystem.service;

import com.czg.seckillsystem.error.BusinessException;
import com.czg.seckillsystem.service.model.OrderModel;

public interface OrderService {
    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount, String stockLogId) throws BusinessException;
}
