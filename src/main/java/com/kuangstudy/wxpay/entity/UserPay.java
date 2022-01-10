package com.kuangstudy.wxpay.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import java.util.Date;
/**
 * @author 徐柯
 * @Title:
 * @Package
 * @Description:
 * @date 2021/5/1022:43
 */
@Data
@Accessors(chain = true)
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TableName("kss_user_pay")
public class UserPay {

    // 主键
    @TableId(type = IdType.AUTO)
    private Integer id;
    // 购买课程
    private String courseid;
    // 支付用户
    private String userid;
    // 支付用户名称
    private String nickname;
    // 课程价格
    private String price;
    // 交易流水号
    private String tradeno;
    // 创建时间
    private Date createTime;
    // 更新时间
    private Date updateTime;
}