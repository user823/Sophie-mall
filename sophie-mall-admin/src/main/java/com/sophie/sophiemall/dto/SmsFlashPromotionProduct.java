package com.sophie.sophiemall.dto;

import com.sophie.sophiemall.model.PmsProduct;
import com.sophie.sophiemall.model.SmsFlashPromotionProductRelation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 限时购及商品信息封装
 */
public class SmsFlashPromotionProduct extends SmsFlashPromotionProductRelation{
    @Getter
    @Setter
    @ApiModelProperty("关联商品")
    private PmsProduct product;
}
