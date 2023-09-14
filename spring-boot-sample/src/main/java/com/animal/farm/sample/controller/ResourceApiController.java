package com.animal.farm.sample.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animal.farm.sample.support.web.Result;

/**
 * 访问资源接口
 *
 * @author roy
 */
@RestController
@RequestMapping(value = "/api-market", produces = {MediaType.APPLICATION_JSON_VALUE})
public class ResourceApiController {

  @GetMapping("{resourceType}/{resourceId}")
  public Result<String> getApiResult(
      @PathVariable(name = "resourceType") Long resourceType,
      @PathVariable(name = "resourceId") String resourceId) {

    return Result.success(String.format("resouceType: [%d], resourceId: [%s]", resourceType,  resourceId));
  }

}
