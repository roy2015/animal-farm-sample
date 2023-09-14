package com.animal.farm.application.module.gateway.config;

import java.util.Arrays;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.stereotype.Component;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
/**
 * @author : zhengyangyong
 */
@Component
@EnableOpenApi
public class SwaggerInitializer implements BeanFactoryAware {
  @Value("${swagger.ui.enabled:false}")
  public Boolean enabled;

  @Value("${swagger.ui.title:未知}")
  public String title;

  @Value("${swagger.ui.version:未知}")
  public String version;

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
    if (enabled) {
      configurableBeanFactory.registerSingleton("docket", new Docket(DocumentationType.SWAGGER_2)
          .apiInfo(new ApiInfoBuilder().title(title).version(version).build()).select()
          .apis(RequestHandlerSelectors.any())
        //  .paths(Predicates.not(PathSelectors.regex("/error.*")))
          .paths(PathSelectors.any())
          .build()
          .globalRequestParameters(Arrays.asList(new RequestParameterBuilder().name("x-token").description("login token")
                 .in("header").required(false).build())));


    }
  }
}
