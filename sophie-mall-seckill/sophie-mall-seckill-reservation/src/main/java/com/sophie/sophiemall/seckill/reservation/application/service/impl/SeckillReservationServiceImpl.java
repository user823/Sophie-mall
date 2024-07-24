package com.sophie.sophiemall.seckill.reservation.application.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sophie.sophiemall.seckill.common.cache.distribute.DistributedCacheService;
import com.sophie.sophiemall.seckill.common.cache.model.SeckillBusinessCache;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.exception.SeckillException;
import com.sophie.sophiemall.seckill.common.model.dto.goods.SeckillGoodsDTO;
import com.sophie.sophiemall.seckill.common.model.enums.SeckillReservationConfigStatus;
import com.sophie.sophiemall.seckill.common.model.enums.SeckillReservationUserStatus;
import com.sophie.sophiemall.seckill.common.utils.beans.BeanUtil;
import com.sophie.sophiemall.seckill.common.utils.id.SnowFlakeFactory;
import com.sophie.sophiemall.seckill.common.utils.string.StringUtil;
import com.sophie.sophiemall.seckill.dubbo.goods.SeckillGoodsDubboService;
import com.sophie.sophiemall.seckill.reservation.application.builder.SeckillReservationConfigBuilder;
import com.sophie.sophiemall.seckill.reservation.application.builder.SeckillReservationUserBuilder;
import com.sophie.sophiemall.seckill.reservation.application.cache.SeckillReservationConfigCacheService;
import com.sophie.sophiemall.seckill.reservation.application.cache.SeckillReservationUserCacheService;
import com.sophie.sophiemall.seckill.reservation.application.command.SeckillReservationConfigCommand;
import com.sophie.sophiemall.seckill.reservation.application.command.SeckillReservationUserCommand;
import com.sophie.sophiemall.seckill.reservation.application.service.SeckillReservationService;
import com.sophie.sophiemall.seckill.reservation.domain.model.entity.SeckillReservationConfig;
import com.sophie.sophiemall.seckill.reservation.domain.model.entity.SeckillReservationUser;
import com.sophie.sophiemall.seckill.reservation.domain.sevice.SeckillReservationDomainService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class SeckillReservationServiceImpl implements SeckillReservationService {
    private final Logger logger = LoggerFactory.getLogger(SeckillReservationServiceImpl.class);
    @Autowired
    private SeckillReservationConfigCacheService seckillReservationConfigCacheService;
    @Autowired
    private SeckillReservationUserCacheService seckillReservationUserCacheService;
    @Autowired
    private SeckillReservationDomainService seckillReservationDomainService;
    @Autowired
    private DistributedCacheService distributedCacheService;
    @DubboReference(version = "1.0.0", check = false)
    private SeckillGoodsDubboService seckillGoodsDubboService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveSeckillReservationConfig(SeckillReservationConfigCommand seckillReservationConfigCommand) {
        if (seckillReservationConfigCommand == null || seckillReservationConfigCommand.isEmpty()){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillGoodsDTO seckillGoods = seckillGoodsDubboService.getSeckillGoods(seckillReservationConfigCommand.getGoodsId(), 0L);
        if (seckillGoods == null){
            throw new SeckillException(ErrorCode.GOODS_NOT_EXISTS);
        }
        SeckillReservationConfig seckillReservationConfig = null;
        SeckillBusinessCache<SeckillReservationConfig> seckillReservationConfigCache = seckillReservationConfigCacheService.getSeckillReservationConfig(seckillReservationConfigCommand.getGoodsId(), 0L);
        if (seckillReservationConfigCache.isExist() && seckillReservationConfigCache.getData() != null){
            seckillReservationConfig = seckillReservationConfigCache.getData();
        }
        if (seckillReservationConfig != null){
            throw new SeckillException(ErrorCode.GOODS_RESERVATION_CONFIG_EXISTS);
        }
        if (new Date().after(seckillReservationConfigCommand.getReserveEndTime()) || seckillReservationConfigCommand.getReserveEndTime().after(seckillGoods.getStartTime())){
            throw new SeckillException(ErrorCode.GOODS_RESERVATION_TIME_NOT_INVALIDATE);
        }
        String luaKey = StringUtil.append(SeckillConstants.RESERVATION_CONFIG, seckillReservationConfigCommand.getGoodsId());
        Long result = distributedCacheService.checkExecute(luaKey, SeckillConstants.SUBMIT_DATA_EXECUTE_EXPIRE_SECONDS);
        //已经预约过，操作过于频繁，处理幂等性问题
        if (NumberUtil.equals(result, SeckillConstants.CHECK_RECOVER_STOCK_HAS_EXECUTE)){
            logger.info("reserveGoods|已经执行过预约方法|{}", JSONObject.toJSONString(seckillReservationConfigCommand));
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }
        seckillReservationConfig = SeckillReservationConfigBuilder.toSeckillReservationConfig(seckillReservationConfigCommand);
        seckillReservationConfig.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        seckillReservationConfig.setGoodsName(seckillGoods.getGoodsName());
        seckillReservationConfig.setSeckillStartTime(seckillGoods.getStartTime());
        seckillReservationConfig.setSeckillEndTime(seckillGoods.getEndTime());
        seckillReservationConfig.setReserveCurrentUserCount(0);
        seckillReservationConfig.setStatus(SeckillReservationConfigStatus.PUBLISHED.getCode());
        boolean success = seckillReservationDomainService.saveSeckillReservationConfig(seckillReservationConfig);
        if (!success){
            logger.info("saveSeckillReservationConfig|添加商品预约配置失败|{}", JSON.toJSONString(seckillReservationConfigCommand));
        }
        return success;
    }

    @Override
    public boolean updateSeckillReservationConfig(SeckillReservationConfigCommand seckillReservationConfigCommand) {
        if (seckillReservationConfigCommand == null || seckillReservationConfigCommand.isEmpty()){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillReservationConfig seckillReservationConfig = null;
        SeckillBusinessCache<SeckillReservationConfig> seckillReservationConfigCache = seckillReservationConfigCacheService.getSeckillReservationConfig(seckillReservationConfigCommand.getGoodsId(), 0L);
        if (seckillReservationConfigCache.isExist() && seckillReservationConfigCache.getData() != null){
            seckillReservationConfig = seckillReservationConfigCache.getData();
        }
        if (seckillReservationConfig == null){
            throw new SeckillException(ErrorCode.GOODS_RESERVATION_CONFIG_NOT_EXISTS);
        }
        if (seckillReservationConfigCommand.getReserveMaxUserCount() < seckillReservationConfig.getReserveCurrentUserCount()){
            throw new SeckillException(ErrorCode.GOODS_RESERVATION_COUNT_INVALIDATE);
        }
        if (new Date().after(seckillReservationConfigCommand.getReserveEndTime()) || seckillReservationConfigCommand.getReserveEndTime().after(seckillReservationConfig.getSeckillStartTime())){
            throw new SeckillException(ErrorCode.GOODS_RESERVATION_TIME_NOT_INVALIDATE);
        }
        BeanUtil.copyProperties(seckillReservationConfigCommand, seckillReservationConfig);
        return seckillReservationDomainService.updateSeckillReservationConfig(seckillReservationConfig);
    }

    @Override
    public boolean updateConfigStatus(Integer status, Long goodsId) {
        if (status == null || goodsId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillReservationDomainService.updateConfigStatus(status, goodsId);
    }

    @Override
    public List<SeckillReservationConfig> getConfigList(Long version) {
        SeckillBusinessCache<List<SeckillReservationConfig>> seckillReservationConfigListCache = seckillReservationConfigCacheService.getSeckillReservationConfigList(version);
        //稍后再试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后再试
        if (seckillReservationConfigListCache.isRetryLater()){
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }
        //缓存中不存在配置数据
        if (!seckillReservationConfigListCache.isExist()){
            throw new SeckillException(ErrorCode.GOODS_RESERVATION_CONFIG_NOT_EXISTS);
        }
        return seckillReservationConfigListCache.getData();
    }

    @Override
    public SeckillReservationConfig getConfigDetail(Long goodsId, Long version) {
        if (goodsId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillBusinessCache<SeckillReservationConfig> seckillReservationConfigCache = seckillReservationConfigCacheService.getSeckillReservationConfig(goodsId, version);
        //稍后再试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后再试
        if (seckillReservationConfigCache.isRetryLater()){
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }
        //缓存中不存在配置数据
        if (!seckillReservationConfigCache.isExist()){
            throw new SeckillException(ErrorCode.GOODS_RESERVATION_CONFIG_NOT_EXISTS);
        }
        return seckillReservationConfigCache.getData();
    }

    @Override
    public List<SeckillReservationUser> getUserListByGoodsId(Long goodsId, Long version) {
        if (goodsId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillBusinessCache<List<SeckillReservationUser>> seckillReservationUserListCache = seckillReservationUserCacheService.getUserListCacheByGoodsId(goodsId, version);
        //稍后再试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后再试
        if (seckillReservationUserListCache.isRetryLater()){
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }
        //缓存中不存在预约数据
        if (!seckillReservationUserListCache.isExist()){
            throw new SeckillException(ErrorCode.GOODS_RESERVATION_USER_NOT_EXISTS);
        }
        return seckillReservationUserListCache.getData();
    }

    @Override
    public List<SeckillReservationUser> getGoodsListByUserId(Long userId, Long version) {
        if (userId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillBusinessCache<List<SeckillReservationUser>> seckillReservationUserListCache = seckillReservationUserCacheService.getGoodsListCacheByUserId(userId, version);
        //稍后再试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后再试
        if (seckillReservationUserListCache.isRetryLater()){
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }
        //缓存中不存在预约数据
        if (!seckillReservationUserListCache.isExist()){
            throw new SeckillException(ErrorCode.GOODS_RESERVATION_USER_NOT_EXISTS);
        }
        return seckillReservationUserListCache.getData();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reserveGoods(SeckillReservationUserCommand seckillReservationUserCommand) {
        if (seckillReservationUserCommand == null || seckillReservationUserCommand.isEmpty()){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillReservationConfig seckillReservationConfig = null;
        SeckillBusinessCache<SeckillReservationConfig> seckillReservationConfigCache = seckillReservationConfigCacheService.getSeckillReservationConfig(seckillReservationUserCommand.getGoodsId(), 0L);
        if (seckillReservationConfigCache.isExist() && seckillReservationConfigCache.getData() != null){
            seckillReservationConfig = seckillReservationConfigCache.getData();
        }
        if (seckillReservationConfig == null){
            throw new SeckillException(ErrorCode.GOODS_RESERVATION_CONFIG_NOT_EXISTS);
        }
        if (!SeckillReservationConfigStatus.isOnline(seckillReservationConfig.getStatus())){
            throw new SeckillException(ErrorCode.GOODS_RESERVATION_CONFIG_NOT_ONLINE);
        }
        if (seckillReservationConfig.getReserveCurrentUserCount() >= seckillReservationConfig.getReserveMaxUserCount()){
            throw new SeckillException(ErrorCode.GOODS_RESERVATION_USER);
        }
        Date date = new Date();
        if (date.before(seckillReservationConfig.getReserveStartTime()) || date.after(seckillReservationConfig.getReserveEndTime())){
            throw new SeckillException(ErrorCode.GOODS_RESERVATION_NOT_TIME);
        }
        SeckillReservationUser seckillReservationUser = null;
        SeckillBusinessCache<SeckillReservationUser> seckillReservationUserCache = seckillReservationUserCacheService.getSeckillReservationUserCacheByUserIdAndGoodsId(seckillReservationUserCommand.getUserId(), seckillReservationUserCommand.getGoodsId(), 0L);
        //logger.info(JSON.toJSONString(seckillReservationUserCache));
        //重试场景
        if (seckillReservationUserCache.isRetryLater()){
            return reserveGoods(seckillReservationUserCommand);
        }
        if (seckillReservationUserCache.isExist() && seckillReservationUserCache.getData() != null){
            seckillReservationUser = seckillReservationUserCache.getData();
        }
        if (seckillReservationUser != null && SeckillReservationUserStatus.isNormal(seckillReservationUser.getStatus())){
            throw new SeckillException(ErrorCode.GOODS_RESERVATION_USER_EXISTS);
        }
        String luaKey = StringUtil.append(SeckillConstants.RESERVATION_USER, seckillReservationUserCommand.getUserId(), seckillReservationUserCommand.getGoodsId());
        Long result = distributedCacheService.checkExecute(luaKey, SeckillConstants.SUBMIT_DATA_EXECUTE_EXPIRE_SECONDS);
        //已经预约过，操作过于频繁，处理幂等性问题
        if (NumberUtil.equals(result, SeckillConstants.CHECK_RECOVER_STOCK_HAS_EXECUTE)){
            logger.info("reserveGoods|已经执行过预约方法|{}", JSONObject.toJSONString(seckillReservationUserCommand));
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }
        boolean success = false;
        try{
            seckillReservationUser = SeckillReservationUserBuilder.toSeckillReservationUser(seckillReservationUserCommand);
            seckillReservationUser.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
            seckillReservationUser.setReserveConfigId(seckillReservationConfig.getId());
            seckillReservationUser.setGoodsName(seckillReservationConfig.getGoodsName());
            seckillReservationUser.setReserveTime(new Date());
            seckillReservationUser.setStatus(SeckillReservationUserStatus.NORMAL.getCode());
            success = seckillReservationDomainService.reserveGoods(seckillReservationUser);
        }catch (Exception e){
            distributedCacheService.delete(luaKey);
            throw e;
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelReserveGoods(SeckillReservationUserCommand seckillReservationUserCommand) {
        if (seckillReservationUserCommand == null || seckillReservationUserCommand.isEmpty()){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillReservationUser seckillReservationUser = this.getSeckillReservationUser(seckillReservationUserCommand);
        if (seckillReservationUser == null || SeckillReservationUserStatus.isDeleted(seckillReservationUser.getStatus())){
            throw new SeckillException(ErrorCode.GOODS_RESERVATION_USER_NOT_EXISTS);
        }
        boolean success = seckillReservationDomainService.cancelReserveGoods(seckillReservationUserCommand.getGoodsId(), seckillReservationUserCommand.getUserId());
        if (success){
            String luaKey = StringUtil.append(SeckillConstants.RESERVATION_USER, seckillReservationUserCommand.getUserId(), seckillReservationUserCommand.getGoodsId());
            if (distributedCacheService.hasKey(luaKey)){
                distributedCacheService.delete(luaKey);
            }
        }
        return success;
    }

    @Override
    public SeckillReservationUser getSeckillReservationUser(SeckillReservationUserCommand seckillReservationUserCommand) {
        if (seckillReservationUserCommand == null || seckillReservationUserCommand.isEmpty()){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillBusinessCache<SeckillReservationUser> seckillReservationUserCache = seckillReservationUserCacheService.getSeckillReservationUserCacheByUserIdAndGoodsId(seckillReservationUserCommand.getUserId(), seckillReservationUserCommand.getGoodsId(), 0L);
        //稍后再试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后再试
        if (seckillReservationUserCache.isRetryLater()){
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }
        //缓存中不存在预约数据
        if (!seckillReservationUserCache.isExist()){
            throw new SeckillException(ErrorCode.GOODS_RESERVATION_USER_NOT_EXISTS);
        }
        return seckillReservationUserCache.getData();
    }
}
