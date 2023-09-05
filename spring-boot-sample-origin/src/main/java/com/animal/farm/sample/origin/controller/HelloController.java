package com.animal.farm.sample.origin.controller;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animal.farm.sample.origin.service.HelloWorldService;
import com.animal.farm.sample.origin.util.RedisUtil;

/**
 * @author guojun
 * @date 2023/9/5 15:54
 */
@RestController
@RequestMapping("/api")
public class HelloController {

  @Autowired
  private HelloWorldService helloWorldService;

  @Autowired
  private RedisUtil redisUtil;

  @RequestMapping(value = "getSysinfo")
  public String getSysInfo() {
//        redisUtil.incr("idGenerater", 10); 不用时候注释掉，要用是打开
    return helloWorldService.sayHello();
  }

  @RequestMapping(value = "doNoting")
  public void doNoting(HttpServletRequest request) throws InterruptedException {
    System.out.println(String.format("远程地址：%s",request.getRemoteAddr()));
    System.out.println(String.format("线程名：%s",Thread.currentThread().getName()));
    TimeUnit.SECONDS.sleep(5);
  }
}
