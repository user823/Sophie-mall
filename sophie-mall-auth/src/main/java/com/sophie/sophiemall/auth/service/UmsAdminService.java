package com.sophie.sophiemall.auth.service;

import com.sophie.sophiemall.common.domain.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 后台用户服务远程调用service
 */
@FeignClient("mall-admin")
public interface UmsAdminService {
    @GetMapping("/admin/loadByUsername")
    UserDto loadUserByUsername(@RequestParam String username);
}
