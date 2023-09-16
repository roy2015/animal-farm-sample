package com.animal.farm.application.module.gateway.simple.config;

import java.util.function.Consumer;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.HasRouteId;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import com.animal.farm.application.module.gateway.simple.config.CustomGatewayFilterFactory.Config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author guojun
 * @date 2023/9/15 21:50
 */

@Component
@Slf4j
public class CustomGatewayFilterFactory extends AbstractGatewayFilterFactory<Config> {

  public CustomGatewayFilterFactory() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      log.info("-----DemoGatewayFilterFactory start-----");
      ServerHttpRequest request = exchange.getRequest();
      log.info("RemoteAddress: [{}]", request.getRemoteAddress());
      log.info("Path: [{}]", request.getURI().getPath());
      log.info("Method: [{}]", request.getMethod());
      log.info("-----DemoGatewayFilterFactory end-----");
      return chain.filter(exchange);
    };
  }

  @Override
  public String name() {
    return "CustomFilter";
  }

  public static class Config implements HasRouteId  {

    private String routeId;

    private String var1;

    private String var2;

    private String var3;

    public String getVar1() {
      return var1;
    }

    public void setVar1(String var1) {
      this.var1 = var1;
    }

    public String getVar2() {
      return var2;
    }

    public void setVar2(String var2) {
      this.var2 = var2;
    }

    public String getVar3() {
      return var3;
    }

    public void setVar3(String var3) {
      this.var3 = var3;
    }

    @Override
    public void setRouteId(String routeId) {
      this.routeId = routeId;
    }

    @Override
    public String getRouteId() {
      return routeId;
    }
  }
}
