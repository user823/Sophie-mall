package com.sophie.sophiemall.seckill.activity.application.service.impl;

import com.sophie.sophiemall.seckill.activity.application.builder.SeckillActivityBuilder;
import com.sophie.sophiemall.seckill.activity.application.cache.SeckillActivityCacheService;
import com.sophie.sophiemall.seckill.activity.application.cache.SeckillActivityListCacheService;
import com.sophie.sophiemall.seckill.activity.application.command.SeckillActivityCommand;
import com.sophie.sophiemall.seckill.activity.application.service.SeckillActivityService;
import com.sophie.sophiemall.seckill.activity.domain.model.entity.SeckillActivity;
import com.sophie.sophiemall.seckill.activity.domain.service.SeckillActivityDomainService;
import com.sophie.sophiemall.seckill.common.cache.model.SeckillBusinessCache;
import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.exception.SeckillException;
import com.sophie.sophiemall.seckill.common.model.dto.activity.SeckillActivityDTO;
import com.sophie.sophiemall.seckill.common.model.enums.SeckillActivityStatus;
import com.sophie.sophiemall.seckill.common.utils.beans.BeanUtil;
import com.sophie.sophiemall.seckill.common.utils.id.SnowFlakeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeckillActivityServiceImpl implements SeckillActivityService {
    @Autowired
    private SeckillActivityDomainService seckillActivityDomainService;

    @Autowired
    private SeckillActivityCacheService seckillActivityCacheService;
    @Autowired
    private SeckillActivityListCacheService seckillActivityListCacheService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSeckillActivity(SeckillActivityCommand seckillActivityCommand) {
        if (seckillActivityCommand == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillActivity seckillActivity = SeckillActivityBuilder.toSeckillActivity(seckillActivityCommand);
        seckillActivity.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        seckillActivity.setStatus(SeckillActivityStatus.PUBLISHED.getCode());
        seckillActivityDomainService.saveSeckillActivity(seckillActivity);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityList(Integer status) {
        return seckillActivityDomainService.getSeckillActivityList(status);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityListBetweenStartTimeAndEndTime(Date currentTime, Integer status) {
        return seckillActivityDomainService.getSeckillActivityListBetweenStartTimeAndEndTime(currentTime, status);
    }

    @Override
    public List<SeckillActivityDTO> getSeckillActivityList(Integer status, Long version) {
        SeckillBusinessCache<List<SeckillActivity>> seckillActivitiyListCache = seckillActivityListCacheService.getCachedActivities(status, version);
        //稍后再试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后再试
        if (seckillActivitiyListCache.isRetryLater()){
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }

        if (!seckillActivitiyListCache.isExist()){
            throw new SeckillException(ErrorCode.ACTIVITY_NOT_EXISTS);
        }
        List<SeckillActivityDTO> seckillActivityDTOList = seckillActivitiyListCache.getData().stream().map((seckillActivity) -> {
            SeckillActivityDTO seckillActivityDTO = new SeckillActivityDTO();
            BeanUtil.copyProperties(seckillActivity, seckillActivityDTO);
            seckillActivityDTO.setVersion(seckillActivitiyListCache.getVersion());
            return seckillActivityDTO;
        }).collect(Collectors.toList());
        return seckillActivityDTOList;
    }

    @Override
    public List<SeckillActivityDTO> getSeckillActivityListBetweenStartTimeAndEndTime(Date currentTime, Integer status, Long version) {
        SeckillBusinessCache<List<SeckillActivity>> seckillActivitiyListCache = seckillActivityListCacheService.getCachedActivities(currentTime, status, version);
        //稍后再试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后再试
        if (seckillActivitiyListCache.isRetryLater()){
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }
        if (!seckillActivitiyListCache.isExist()){
            throw new SeckillException(ErrorCode.ACTIVITY_NOT_EXISTS);
        }
        List<SeckillActivityDTO> seckillActivityDTOList = seckillActivitiyListCache.getData().stream().map((seckillActivity) -> {
            SeckillActivityDTO seckillActivityDTO = new SeckillActivityDTO();
            BeanUtil.copyProperties(seckillActivity, seckillActivityDTO);
            seckillActivityDTO.setVersion(seckillActivitiyListCache.getVersion());
            return seckillActivityDTO;
        }).collect(Collectors.toList());
        return seckillActivityDTOList;
    }

    @Override
    public SeckillActivity getSeckillActivityById(Long id) {
        return seckillActivityDomainService.getSeckillActivityById(id);
    }

    @Override
//    @SentinelResource(value = "QUEUE-DATA-FLOW",
//            blockHandlerClass = SeckillActivityFallbackService.class, blockHandler = "getSeckillActivityBlockHandler",
//            fallbackClass = SeckillActivityFallbackService.class, fallback = "getSeckillActivityFallback")
    public SeckillActivityDTO getSeckillActivity(Long id, Long version) {
        if (id == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillBusinessCache<SeckillActivity> seckillActivityCache = seckillActivityCacheService.getCachedSeckillActivity(id, version);
        //稍后再试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后再试
        if (seckillActivityCache.isRetryLater()){
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }
        //缓存中的活动数据不存在
        if (!seckillActivityCache.isExist()){
            throw new SeckillException(ErrorCode.ACTIVITY_NOT_EXISTS);
        }
        SeckillActivityDTO seckillActivityDTO = SeckillActivityBuilder.toSeckillActivityDTO(seckillActivityCache.getData());
        seckillActivityDTO.setVersion(seckillActivityCache.getVersion());
//        int i = 1 / 0;
        return seckillActivityDTO;
    }

    @Override
    public void updateStatus(Integer status, Long id) {
        seckillActivityDomainService.updateStatus(status, id);
    }
}

