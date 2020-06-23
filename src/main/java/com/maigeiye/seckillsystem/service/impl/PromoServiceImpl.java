package com.maigeiye.seckillsystem.service.impl;

import com.maigeiye.seckillsystem.dao.PromoDOMapper;
import com.maigeiye.seckillsystem.dataobject.PromoDO;
import com.maigeiye.seckillsystem.error.BusinessException;
import com.maigeiye.seckillsystem.error.EmBusinessError;
import com.maigeiye.seckillsystem.service.ItemService;
import com.maigeiye.seckillsystem.service.PromoService;
import com.maigeiye.seckillsystem.service.UserService;
import com.maigeiye.seckillsystem.service.model.ItemModel;
import com.maigeiye.seckillsystem.service.model.PromoModel;
import com.maigeiye.seckillsystem.service.model.UserModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    private PromoDOMapper promoDOMapper;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        PromoDO promoDO = promoDOMapper.selectByItemId(itemId);
        PromoModel promoModel = convertFromPromoDO(promoDO);
        if (promoModel == null) return null;

        // 判断当前时间是否即将开始秒杀活动或正在进行
        if (promoModel.getStartDate().isAfterNow()) {
            promoModel.setStatus(1);
        } else if (promoModel.getEndDate().isBeforeNow()) {
            promoModel.setStatus(3);
        } else {
            promoModel.setStatus(2);
        }
        return promoModel;
    }

    @Override
    public void publishPromo(Integer promoId) {
        PromoDO promoDO = promoDOMapper.selectByPrimaryKey(promoId);
        if (promoDO.getItemId() == null || promoDO.getItemId().intValue() == 0) {
            return;
        }
        ItemModel itemModel = itemService.getItemById(promoDO.getItemId());
        // redis同步库存
        redisTemplate.opsForValue().set("promo_item_id_" + itemModel.getId(), itemModel.getStock());
        // 将秒杀大闸的限制数字设到redis内
        redisTemplate.opsForValue().set("promo_door_count_" + promoId, itemModel.getStock().intValue() * 5);
    }

    @Override
    public String generateSecondKillToken(Integer promoId, Integer userId, Integer itemId) {
        // 判断库存是否售罄
        if (redisTemplate.hasKey("promo_item_stock_invalid_" + itemId)) {
            return null;
        }
        PromoDO promoDO = promoDOMapper.selectByPrimaryKey(promoId);
        PromoModel promoModel = convertFromPromoDO(promoDO);
        if (promoModel == null) {
            return null;
        }
        if (promoModel.getStartDate().isAfterNow()) {
            promoModel.setStatus(1);
        } else if (promoModel.getEndDate().isBeforeNow()) {
            promoModel.setStatus(3);
        } else {
            promoModel.setStatus(2);
        }
        // 判断活动是否开始
        if (promoModel.getStatus().intValue() != 2) {
            return null;
        }
        // 判断item和user信息是否存在
        ItemModel itemModel = itemService.getItemByIdInCache(itemId);
        if (itemModel == null) {
            return null;
        }
        UserModel userModel = userService.getUserByIdInCache(userId);
        if (userModel == null) {
            return null;
        }
        // 获取秒杀大闸的count数量
        long result =  redisTemplate.opsForValue().increment("promo_door_count_" + promoId, -1);
        if (result < 0) {
            return null;
        }
        // 生成秒杀令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set("promo_token_" + promoId + "_user_id_" + userId + "_item_id_" + itemId, token);
        redisTemplate.expire("promo_token_" + promoId + "_user_id_" + userId + "_item_id_" + itemId, 5, TimeUnit.MINUTES);
        return token;
    }

    private PromoModel convertFromPromoDO(PromoDO promoDO) {
        if (promoDO == null) return null;
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO,promoModel);
        promoModel.setPromoItemPrice(new BigDecimal(promoDO.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));
        return promoModel;
    }
}
