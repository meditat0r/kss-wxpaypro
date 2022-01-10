package com.kuangstudy.wxpay.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kuangstudy.wxpay.entity.UserPay;
import com.kuangstudy.wxpay.service.UserPayService;
import com.kuangstudy.wxpay.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * @author 徐柯
 * @Title:
 * @Package
 * @Description:
 * @date 2021/5/1022:59
 */
@Controller
@RequestMapping("/api")
public class MonitorController {
    @Autowired
    private UserPayService userPayService;
    /**
     * 定义监听类
     * @param courseid
     * @return
     */
    @ResponseBody
    @GetMapping("/paySuccess")
    public R paySuccess(String courseid) {
        QueryWrapper<UserPay> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("courseid", courseid);
        queryWrapper.eq("userid", 1);
        int count = userPayService.count(queryWrapper);
        return count > 0 ? R.ok() : R.error();
    }
}