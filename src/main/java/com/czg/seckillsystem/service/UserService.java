package com.czg.seckillsystem.service;

import com.czg.seckillsystem.error.BusinessException;
import com.czg.seckillsystem.service.model.UserModel;

/**
 * User的Service接口
 */
public interface UserService {
    /**
     * 通过用户id获取用户对象
     * @param id
     * @return
     */
    UserModel getUserById(Integer id);

    /**
     * 在缓存中获取用户信息
     * @param id
     * @return
     */
    UserModel getUserByIdInCache(Integer id);

    /**
     * 用户注册接口
     * @param userModel
     * @throws BusinessException
     */
    void register(UserModel userModel) throws BusinessException;

    /**
     * 用户登录接口，传入电话和加密后的密码
     * @param telephone
     * @param encrptPassword
     * @return
     */
    UserModel validateLogin(String telephone, String encrptPassword) throws BusinessException;
}
