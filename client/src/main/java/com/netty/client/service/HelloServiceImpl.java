package com.netty.client.service;

import api.HelloService;
import dto.HelloDTO;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author : liuzg
 * @description 具体实现
 * @date : 2020-10-22 14:12
 * @since 1.0
 **/
@Service
public class HelloServiceImpl implements HelloService {

    @Override
    public HelloDTO hello() {
        HelloDTO helloDTO = new HelloDTO();

        helloDTO.setMsg("msg :" + ThreadLocalRandom.current().nextInt(10));
        helloDTO.setRemark("remar :" + ThreadLocalRandom.current().nextInt(10));

        return helloDTO;
    }
}
