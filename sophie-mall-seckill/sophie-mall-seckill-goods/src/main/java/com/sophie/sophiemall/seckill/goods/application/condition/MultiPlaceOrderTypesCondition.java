package com.sophie.sophiemall.seckill.goods.application.condition;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class MultiPlaceOrderTypesCondition extends AnyNestedCondition {

    public MultiPlaceOrderTypesCondition() {
        super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(name = "place.order.type", havingValue = "db")
    static class DbCondition{}

    @ConditionalOnProperty(name = "place.order.type", havingValue = "lock")
    static class LockCondition{}

    @ConditionalOnProperty(name = "place.order.type", havingValue = "lua")
    static class LuaCondition{}
}
