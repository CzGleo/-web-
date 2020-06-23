package com.maigeiye.seckillsystem.service;

import com.maigeiye.seckillsystem.error.BusinessException;
import com.maigeiye.seckillsystem.service.model.ItemModel;

import java.util.List;

/**
 * 商品业务层接口
 */
public interface ItemService {
    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    List<ItemModel> listItem();

    ItemModel getItemById(Integer id);

    // item及promo model缓存模型
    ItemModel getItemByIdInCache(Integer id);

    boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException;

    // 回补库存
    boolean increaseStock(Integer itemId, Integer amount) throws BusinessException;

    // 异步更新库存
    boolean asyncDecreaseStock(Integer itemId, Integer amount);

    void increaseSales(Integer itemId, Integer amount) throws BusinessException;

    // 初始化库存流水
    String initStockLog(Integer itemId, Integer amount);
}
