package com.czg.seckillsystem.controller;

import com.czg.seckillsystem.error.BusinessException;
import com.czg.seckillsystem.error.EmBusinessError;
import com.czg.seckillsystem.mq.MqProducer;
import com.czg.seckillsystem.response.CommonReturnType;
import com.czg.seckillsystem.service.ItemService;
import com.czg.seckillsystem.service.OrderService;
import com.czg.seckillsystem.service.PromoService;
import com.czg.seckillsystem.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.*;

@Controller("order")
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MqProducer mqProducer;

    @Autowired
    private ItemService itemService;

    @Autowired
    private PromoService promoService;

    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(20);
    }

    @RequestMapping(value = "/token", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType generateToken(@RequestParam(name = "itemId")Integer itemId,
                                          @RequestParam(name = "promoId")Integer promoId) throws BusinessException {
        // 根据token获取用户信息，验证登录信息
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if (StringUtils.isEmpty(token)) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户未登录");
        }
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户未登录");
        }
        // 获取秒杀令牌
        String promoToken = promoService.generateSecondKillToken(promoId, userModel.getId(), itemId);
        if (promoToken == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "生成令牌失败");
        }
        return CommonReturnType.create(promoToken);
    }

    @RequestMapping(value = "/create", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId")Integer itemId,
                                        @RequestParam(name = "amount")Integer amount,
                                        @RequestParam(name = "promoId", required = false)Integer promoId,
                                        @RequestParam(name = "promoToken", required = false)String promoToken) throws BusinessException {
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if (StringUtils.isEmpty(token)) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户未登录");
        }
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户未登录");
        }

        // 检验秒杀活动下单的秒杀令牌是否正确
        if (promoId != null) {
            String promoTokenInRedis = (String) redisTemplate.opsForValue().get("promo_token_" + promoId + "_user_id_" +
                    userModel.getId() + "_item_id_" + itemId);
            if (promoTokenInRedis == null) {
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "秒杀令牌验证失败");
            }
            if (!org.apache.commons.lang3.StringUtils.equals(promoToken, promoTokenInRedis)) {
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "秒杀令牌验证失败");
            }
        }

        // 同步调用线程池的submit方法
        Future<Object> future = executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                //加入库存流水init状态
                String stockLogId = itemService.initStockLog(itemId, amount);

                // 完成下单事务型消息队列
                if (!mqProducer.transactionAsyncReduceStock(userModel.getId(), itemId, promoId, amount, stockLogId)) {
                    throw new BusinessException(EmBusinessError.UNKNOWN_ERROR, "下单失败");
                }
                ;
                return null;
            }
        });
        try {
            future.get();
        } catch (InterruptedException e) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
        } catch (ExecutionException e) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
        }
        return CommonReturnType.create(null);
    }
}
