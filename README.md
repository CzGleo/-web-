# Seckill-System

> 本项目为模拟商城秒杀活动后台的商品秒杀系统，具备用户，商品，订单模块
>

- 基于SpringBoot完成系统的搭建，Mybatis实现数据库的映射
- 分层缓存，使用Guava包的Cache类实现本地缓存以及Redis实现全局缓存，防止大量请求落入MySQL数据库
- 基于RocketMQ的事务型消息保证库存在Redis缓存和MySQL数据库中的一致性
- 基于自定义的秒杀令牌和秒杀大闸以及固定数量的线程池完成流量削峰，防止大量请求同时进入应用

