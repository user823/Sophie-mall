package com.sophie.sophiemall.component;

import com.sophie.sophiemall.service.UmsResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 资源与角色访问对应关系操作组件
 */
@Component
public class ResourceRoleRulesHolder {

    @Autowired
    private UmsResourceService resourceService;

    @PostConstruct
    public void initResourceRolesMap(){
        resourceService.initResourceRolesMap();
    }
}
