package com.kuangstudy.wxpay.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kuangstudy.wxpay.entity.UserPay;
import com.kuangstudy.wxpay.mapper.UserPayMapper;
import org.springframework.stereotype.Service;
/**
 * @author 徐柯
 * @Title:
 * @Package
 * @Description:
 * @date 2021/5/1022:43
 */
@Service
public class UserPayServiceImpl extends ServiceImpl<UserPayMapper,UserPay> implements UserPayService {
}