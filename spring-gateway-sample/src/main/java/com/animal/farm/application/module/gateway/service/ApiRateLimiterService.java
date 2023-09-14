package com.animal.farm.application.module.gateway.service;

public interface ApiRateLimiterService {
  void saveApiRateLimiter(String key, Integer replenishRate, Integer time);

  void deleteApiRateLimiter(String key);

  void unLockUser(String key);
}