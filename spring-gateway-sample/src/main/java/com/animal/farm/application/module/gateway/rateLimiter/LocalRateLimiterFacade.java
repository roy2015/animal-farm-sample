package com.animal.farm.application.module.gateway.rateLimiter;


import static com.animal.farm.application.module.gateway.constant.GatewayConstant.SEPARATOR;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

import com.animal.farm.application.module.gateway.constant.AuthenticationHeaderName;
import com.animal.farm.application.module.gateway.dto.LockedUserInformationDto;
import com.animal.farm.application.module.gateway.rateLimiter.LocalRateLimiter.Config;
import com.animal.farm.application.module.gateway.service.ApiRateLimiterServiceImpl;
import com.animal.farm.infrastructure.foundation.JwtTokenUtil;
import com.animal.farm.infrastructure.foundation.Token;

import reactor.core.publisher.Mono;

/**
 * @author zhouzhiyuan
 * @date 2022/10/26 11:20
 */
@Configuration
public class LocalRateLimiterFacade {

  @Bean(name = "rateLimiterBlockingQueue")
  BlockingQueue<LockedUserInformationDto> blockingQueue() {
    return new ArrayBlockingQueue<>(1000);
  }

  @Bean
  KeyResolver userKeyResolver() {

    return new KeyResolver() {
      @Override
      public Mono<String> resolve(ServerWebExchange exchange) {
        String apiPath = ServerWebExchangeUtils.getUriTemplateVariables(exchange).get("id");
        HttpHeaders headers = exchange.getRequest().getHeaders();
        //这个只获取userId，不检查token是否有效
        List<String> tokens = headers.getOrDefault(AuthenticationHeaderName.X_TOKEN, null);
        if (tokens != null && tokens.size() == 1) {
          try {
            Token token = JwtTokenUtil.fromString(tokens.get(0));
            apiPath = apiPath + SEPARATOR + token.getUserId();
          }catch (Exception ignore){
            return Mono.just("____EMPTY_KEY__");//没有token，返回空key
          }
        }
        return Mono.just(apiPath);
      }
    };
  }

  @Bean
  @Primary
  RateLimiter<Config> localRateLimiter(ApiRateLimiterServiceImpl apiRateLimiterService, BlockingQueue<LockedUserInformationDto> rateLimiterBlockingQueue) {
    return new LocalRateLimiter(1, apiRateLimiterService, rateLimiterBlockingQueue);
  }


}
