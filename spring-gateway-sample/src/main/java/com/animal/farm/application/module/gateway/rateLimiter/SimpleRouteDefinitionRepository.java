package com.animal.farm.application.module.gateway.rateLimiter;

import static java.util.Collections.synchronizedMap;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.animal.farm.infrastructure.foundation.ServiceConfiguration;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author zhouzhiyuan
 * @date 2022/10/26 16:52
 */
@Component
public class SimpleRouteDefinitionRepository implements RouteDefinitionRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleRouteDefinitionRepository.class);

  private final ServiceConfiguration configuration;

  private final Map<String, RouteDefinition> routes = synchronizedMap(
      new LinkedHashMap<String, RouteDefinition>());



  public SimpleRouteDefinitionRepository(ServiceConfiguration configuration)  {
    this.configuration = configuration;
    setRouteDefinition("default");
  }

  @Override
  public Mono<Void> save(Mono<RouteDefinition> route) {
    return route.flatMap(r -> {
      if (StringUtils.isEmpty(r.getId())) {
        return Mono.error(new IllegalArgumentException("id may not be empty"));
      }
      routes.put(r.getId(), r);
      return Mono.empty();
    });
  }

  @Override
  public Mono<Void> delete(Mono<String> routeId) {
    return routeId.flatMap(id -> {
      if (routes.containsKey(id)) {
        routes.remove(id);
        return Mono.empty();
      }
      return Mono.defer(() -> Mono.error(
          new NotFoundException("RouteDefinition not found: " + routeId)));
    });
  }



  @Override
  public Flux<RouteDefinition> getRouteDefinitions() {
    return Flux.fromIterable(routes.values());
  }


  private   void setRouteDefinition(String routeId) {
    RouteDefinition definition = getRouteDefinition(routeId);
    routes.put(routeId,definition);
  }

  //特殊的 route ，需要结合自定义的 bean使用。
  private   RouteDefinition getRouteDefinition(String routeId)  {
    RouteDefinition definition = new RouteDefinition();
    definition.setId(routeId);

    String targetUrl = configuration
        .get("gateway.default.target-url", null, String.class);
    String predicatePath = configuration
        .get("gateway.default.predicate-path",null, String.class);
    if (targetUrl == null || predicatePath== null ){
      LOGGER.error("gateway默认的路由配置为空，请检查配置 service.gateway.default.route-path 和 service.gateway.default.predicate-path");
      throw new RuntimeException("gateway默认的路由配置为空，请检查配置 service.gateway.default.route-path 和 service.gateway.default.predicate-path");
    }

    URI uri = UriComponentsBuilder.fromHttpUrl(targetUrl).build().toUri();
    definition.setUri(uri);
    //定义第一个断言
    PredicateDefinition predicate = new PredicateDefinition();
    predicate.setName("Path");
    Map<String, String> predicateParams = new HashMap<>(2);
    predicateParams.put("pattern", predicatePath);
    predicate.setArgs(predicateParams);
    //定义Filter
    FilterDefinition filter = new FilterDefinition();
    filter.setName("LocalRateLimiter");
    Map<String, String> filterParams = new HashMap<>(3);
    filterParams.put("rate-limiter", "#{@localRateLimiter}");
    filterParams.put("key-resolver", "#{@userKeyResolver}");
    filter.setArgs(filterParams);

    definition.setFilters(Collections.singletonList(filter));
    definition.setPredicates(Collections.singletonList(predicate));
    return definition;
  }
}
