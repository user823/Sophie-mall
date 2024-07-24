package com.sophie.sophiemall.seckill.common.model.dto.stock;

import java.io.Serializable;

public class SeckillStockDTO implements Serializable {
    private static final long serialVersionUID = 6707252274621460974L;
    //库存总量
    private Integer totalStock;
    //可用库存量
    private Integer availableStock;

    public SeckillStockDTO() {
    }

    public SeckillStockDTO(Integer totalStock, Integer availableStock) {
        this.totalStock = totalStock;
        this.availableStock = availableStock;
    }

    public Integer getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(Integer totalStock) {
        this.totalStock = totalStock;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }
}
