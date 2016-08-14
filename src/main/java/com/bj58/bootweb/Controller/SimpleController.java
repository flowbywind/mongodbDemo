package com.bj58.bootweb.Controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by admin on 2016/8/12.
 */
@Controller
@EnableAutoConfiguration
@RequestMapping(value = "/Simple",method = RequestMethod.GET)
public class SimpleController {

    @RequestMapping(value="/hello",method = RequestMethod.GET)
    @ResponseBody
    public String hello(){
        return "hello world";
    }


    public static void main(String[] args){
        SpringApplication.run(SimpleController.class,args);
    }


}
