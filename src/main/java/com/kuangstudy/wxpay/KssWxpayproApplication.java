package com.kuangstudy.wxpay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.kuangstudy.wxpay.mapper")
public class KssWxpayproApplication {

    public static void main(String[] args) {
        SpringApplication.run(KssWxpayproApplication.class, args);
    }

}
