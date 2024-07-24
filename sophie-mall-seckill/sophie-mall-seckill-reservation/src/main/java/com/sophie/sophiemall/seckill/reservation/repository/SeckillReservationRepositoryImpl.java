package com.sophie.sophiemall.seckill.reservation.repository;

import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.exception.SeckillException;
import com.sophie.sophiemall.seckill.reservation.domain.model.entity.SeckillReservationConfig;
import com.sophie.sophiemall.seckill.reservation.domain.model.entity.SeckillReservationUser;
import com.sophie.sophiemall.seckill.reservation.domain.repository.SeckillReservationRepository;
import com.sophie.sophiemall.seckill.reservation.mapper.SeckillReservationConfigMapper;
import com.sophie.sophiemall.seckill.reservation.mapper.SeckillReservationGoodsMapper;
import com.sophie.sophiemall.seckill.reservation.mapper.SeckillReservationUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeckillReservationRepositoryImpl implements SeckillReservationRepository {

    @Autowired
    private SeckillReservationConfigMapper seckillReservationConfigMapper;
    @Autowired
    private SeckillReservationUserMapper seckillReservationUserMapper;
    @Autowired
    private SeckillReservationGoodsMapper seckillReservationGoodsMapper;

    @Override
    public boolean saveSeckillReservationConfig(SeckillReservationConfig seckillReservationConfig) {
        if (seckillReservationConfig == null || seckillReservationConfig.isEmpty()){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillReservationConfigMapper.saveSeckillReservationConfig(seckillReservationConfig) == 1;
    }

    @Override
    public boolean updateSeckillReservationConfig(SeckillReservationConfig seckillReservationConfig) {
        if (seckillReservationConfig == null || seckillReservationConfig.isEmpty()){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillReservationConfigMapper.updateSeckillReservationConfig(seckillReservationConfig) == 1;
    }

    @Override
    public boolean updateConfigStatus(Integer status, Long goodsId) {
        if (status == null || goodsId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillReservationConfigMapper.updateStatus(status, goodsId) == 1;
    }

    @Override
    public int updateReserveCurrentUserCount(Integer reserveCurrentUserCount, Long goodsId) {
        if (reserveCurrentUserCount == null || goodsId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillReservationConfigMapper.updateReserveCurrentUserCount(reserveCurrentUserCount, goodsId);
    }

    @Override
    public List<SeckillReservationConfig> getConfigList() {
        return seckillReservationConfigMapper.getConfigList();
    }

    @Override
    public SeckillReservationConfig getConfigDetail(Long goodsId) {
        if (goodsId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillReservationConfigMapper.getConfigDetail(goodsId);
    }

    @Override
    public List<SeckillReservationUser> getUserListByGoodsId(Long goodsId, Integer status) {
        if (goodsId == null || status == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillReservationGoodsMapper.getUserListByGoodsId(goodsId, status);
    }

    @Override
    public List<SeckillReservationUser> getGoodsListByUserId(Long userId, Integer status) {
        if (userId == null || status == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillReservationUserMapper.getGoodsListByUserId(userId, status);
    }

    @Override
    public boolean reserveGoods(SeckillReservationUser seckillReservationUser) {
        if (seckillReservationUser == null || seckillReservationUser.isEmpty()){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        boolean userResult = seckillReservationUserMapper.reserveGoods(seckillReservationUser) == 1;
        boolean goodsResult = seckillReservationGoodsMapper.reserveGoods(seckillReservationUser) == 1;
        return userResult && goodsResult;
    }

    @Override
    public boolean cancelReserveGoods(Long goodsId, Long userId) {
        if (goodsId == null || userId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        boolean userResult = seckillReservationUserMapper.cancelReserveGoods(goodsId, userId) == 1;
        boolean goodsResult = seckillReservationGoodsMapper.cancelReserveGoods(goodsId, userId) == 1;
        return userResult && goodsResult;
    }

    @Override
    public SeckillReservationUser getSeckillReservationUser(Long userId, Long goodsId, Integer status) {
        if (goodsId == null || userId == null || status == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillReservationUserMapper.getSeckillReservationUser(userId, goodsId, status);
    }
}
