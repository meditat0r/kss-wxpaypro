package com.kuangstudy.wxpay.controller;

import com.kuangstudy.wxpay.entity.Course;
import com.kuangstudy.wxpay.service.CourseService;
import com.kuangstudy.wxpay.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    CourseService courseService;

    /**
     * 跳转课程首页
     *
     * @return
     */
    @GetMapping("index")
    public String index() {
        return "index";
    }

    /**
     * 跳转课程明细
     *
     * @param id
     * @param modelMap
     * @return
     */
    @GetMapping("coursedetail/{id}")
    public String coursedetail(@PathVariable("id") String id, ModelMap modelMap) {
        // 1：根据课程ID查询课程信息
        Course course = courseService.getById(id);
        // 2：把课程信息放入到作用域中
        modelMap.put("course", course);
        // 3：返回课程明细的视图
        return "coursedetail";
    }


    /**
     * @return com.kuangstudy.wxpay.vo.R
     * @Author xuke
     * @Description 查询产品课程接口
     * @Date 14:29 2021/5/16
     * @Param []
     **/
    @GetMapping("/loadcourse")
    @ResponseBody
    public R loadCourse() {
        // 1: 执行课程查询，把课程全部查询
        List<Course> courses = courseService.list();
        // 2 : 统一返回处理
        return R.ok().data("courses", courses);
    }
}