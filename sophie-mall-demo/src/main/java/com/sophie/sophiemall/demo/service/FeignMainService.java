package com.sophie.sophiemall.demo.service;

import com.sophie.sophiemall.common.api.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("mall-portal")
public interface FeignMainService {

    @PostMapping("/sso/login")
    CommonResult login(@RequestParam String username, @RequestParam String password);

    @GetMapping("/cart/list")
    CommonResult list();
}
