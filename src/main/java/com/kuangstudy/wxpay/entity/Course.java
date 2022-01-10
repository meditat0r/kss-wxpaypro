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
 * @date 2021/5/1012:39
 */
@Data
@Accessors(chain = true)
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TableName("kss_courses")
public class Course {
    // 课程id
    @TableId(type = IdType.ID_WORKER_STR)
    private String courseid;
    // 课程标题
    private String title;
    // 课程介绍
    private String intro;
    // 课程封面
    private String img;
    // 课程价格
    private String price;
    // 课程状态 0未发布1发布
    private Integer status;
    // 创建时间
    private Date createTime;
    // 更新时间
    private Date updateTime;
}