package com.netty.server.controller;

import api.HelloService;
import com.netty.server.annotation.RemarkingReference;
import dto.HelloDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : liuzg
 * @description todo
 * @date : 2020-10-22 13:37
 * @since 1.0
 **/
@RequestMapping("/api")
@RestController
public class HelloController {

    @RemarkingReference
    private HelloService helloService;

    @GetMapping("/hello/{schoolId}")
    public HelloDTO hello() {
        return helloService.hello();
    }
}
