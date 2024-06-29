package com.sophie.sophiemall.demo.controller;

import com.sophie.sophiemall.common.api.CommonResult;
import com.sophie.sophiemall.demo.service.FeignMainService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Feign调用mall-portal接口示例
 * Created by macro on 2019/10/18.
 */
@Api(tags = "FeignPortalController", description = "Feign调用mall-portal接口示例")
@RestController
@RequestMapping("/feign/portal")
public class FeignMainController {

    @Autowired
    private FeignMainService portalService;

    @PostMapping("/login")
    public CommonResult login(@RequestParam String username, @RequestParam String password) {
        return portalService.login(username,password);
    }

    @GetMapping("/cartList")
    public CommonResult cartList() {
        return portalService.list();
    }
}
