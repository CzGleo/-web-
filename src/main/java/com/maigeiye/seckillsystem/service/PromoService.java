package com.maigeiye.seckillsystem.service;

import com.maigeiye.seckillsystem.service.model.PromoModel;

public interface PromoService {
    PromoModel getPromoByItemId(Integer itemId);

    /**
     * 秒杀活动发布接口
     * @param promoId
     */
    void publishPromo(Integer promoId);

    /**
     * 生成秒杀令牌
     * @param promoId
     * @return
     */
    String generateSecondKillToken(Integer promoId, Integer userId, Integer itemId);
}
