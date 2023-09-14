package com.animal.farm.application.module.gateway.service;


import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.animal.farm.infrastructure.foundation.util.ExecutorUtil;

import io.github.resilience4j.ratelimiter.RateLimiterConfig.Builder;
import io.github.resilience4j.ratelimiter.internal.InMemoryRateLimiterRegistry;

/**
 * @author zhouzhiyuan
 * @date 2022/10/27 10:32
 */
@Service
public class ApiRateLimiterServiceImpl implements ApiRateLimiterService {

  private static final Logger LOG = LoggerFactory.getLogger(ApiRateLimiterServiceImpl.class);

  /**
   * api限速配置，key 数据api的 id, value 限速对象（每次更新配置时需要重建对象）
   */
  private final Map<String, InMemoryRateLimiterRegistry> cacheRateLimiter = new ConcurrentHashMap<>();

  /**
   * api限速配置，key 数据api的 id, value  数据api限速配置
   */
  private final Map<String, Config> apiAccessRateControl = new ConcurrentHashMap<>();

  /**
   * 判断用户是否被锁定, key 数据api id 加  用户 id ， value  key的创建时间
   */
  private final Map<String, Long> lockedUser = new ConcurrentHashMap<>();


  @Autowired
  public ApiRateLimiterServiceImpl() {


    //可以从库里load api限流的数据到 apiAccessRateControl lockedUser
//    for (ResourceApiConfigurationsEntity configurationEntity : configurationsRepository.findAll()) {
//      apiAccessRateControl.put(configurationEntity.getApiPath(),
//          new Config(configurationEntity.getReplenishRate(), configurationEntity.getLockTime() * 60 * 1000));
//    }
//    for (ResourceApiLockedUsersEntity lockedUsersEntity : lockedUsersRepositoty.findAll()) {
//      String key = lockedUsersEntity.getApiPath() + SEPARATOR + lockedUsersEntity.getUserId();
//      lockedUser.put(key, lockedUsersEntity.getUnlockTime().getTime());
//    }

    ScheduledExecutorService scheduler = ExecutorUtil.createScheduledExecutorService("delete_lock_user", 2);
    //删除缓存里过期的lockedUser信息
    scheduler.scheduleAtFixedRate(() -> {
      if (lockedUser.size() > 0) {
        long currentTimeMillis = System.currentTimeMillis();
        for (Entry<String, Long> entry : lockedUser.entrySet()) {
          if (currentTimeMillis > entry.getValue()) {
            lockedUser.remove(entry.getKey());
            LOG.info("删除过期锁定用户" + entry.getKey());
          }
        }
      }
    }, 1, 1, TimeUnit.MINUTES);
    //删除数据库过期的lockedUser信息
//    scheduler.scheduleAtFixedRate(() -> {
//      try {
//        lockedUsersRepositoty.deleteByunLockTime(new Date());
//        LOG.info("删除数据库过期锁定用户信息");
//      } catch (Exception e) {
//        LOG.error(e.getMessage(), e);
//      }
//    }, 0, 1, TimeUnit.DAYS);
  }


  /**
   * 
   * @param key
   * @param replenishRate
   * @param lockTime
   */
  @Override
  public void saveApiRateLimiter(String key, Integer replenishRate, Integer lockTime) {
    apiAccessRateControl.put(key, new Config(replenishRate, lockTime * 1000 ));
    cacheRateLimiter.put(key, new InMemoryRateLimiterRegistry(new Builder().build()));
  }

  @Override
  public void deleteApiRateLimiter(String key) {
    apiAccessRateControl.remove(key);
    cacheRateLimiter.remove(key);
  }

  @Override
  public void unLockUser(String key) {
    lockedUser.remove(key);
  }

  public Map<String, Config> getApiAccessRate() {
    return apiAccessRateControl;
  }

  public Map<String, Long> getLockedUser() {
    return lockedUser;
  }

  public Map<String, InMemoryRateLimiterRegistry> getCacheRateLimiter() {
    return cacheRateLimiter;
  }

  public static class Config {
    private int replenishRate;//范围时间内放几个令牌

    private long lockTime;

    public Config(int replenishRate, long unlockTime) {
      this.replenishRate = replenishRate;
      this.lockTime = unlockTime;
    }

    public int getReplenishRate() {
      return replenishRate;
    }

    public void setReplenishRate(int replenishRate) {
      this.replenishRate = replenishRate;
    }

    public long getLockTime() {
      return lockTime;
    }

    public void setTime(long time) {
      this.lockTime = time;
    }
  }
}
