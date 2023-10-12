package com.animal.farm.familytree.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.animal.farm.familytree.dto.FamilyPersonDto;
import com.animal.farm.familytree.dto.request.AddFamilyPersonDto;
import com.animal.farm.familytree.infrastructure.exception.DataPlatformException;
import com.animal.farm.familytree.infrastructure.web.MessageCode;
import com.animal.farm.familytree.infrastructure.web.Result;
import com.animal.farm.familytree.service.FamilyTreeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author guojun
 * @date 2022/10/27 12:02
 */
@Api(tags = "家谱管理")
@Validated
@RestController
@RequestMapping(value = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
public class ApiController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApiController.class);


  @Autowired
  private FamilyTreeService familyTreeService;



  /**
   * @return
   */
  @ApiOperation("list all")
  @GetMapping("/list")
  public Result<List<FamilyPersonDto>> listAllData() {
    List<FamilyPersonDto> familyPersonDtos = familyTreeService.listAll();
    return Result.success(familyPersonDtos);
  }

  @ApiOperation("同步tree")
  @GetMapping("/syncTreeData")
  public Result<Boolean> syncTreeData() throws DataPlatformException {
    familyTreeService.syncData();
    return Result.success(true);
  }

  @ApiOperation("新增家族成员")
  @PostMapping("/add_family_person")
  public Result<Boolean> addFamilyPerson(@Valid  @RequestBody AddFamilyPersonDto addFamilyPersonDto)
      throws DataPlatformException {
    familyTreeService.addFamilyPerson(addFamilyPersonDto);
    return Result.success(true);
  }


  @ApiOperation("删除家族成员")
  @DeleteMapping("/{id}")
  public Result<Boolean> addFamilyPerson(@NotNull @PathVariable("id") Long id)
      throws DataPlatformException {
    familyTreeService.deleteFamilyPerson(id);
    return Result.success(true);
  }


//  @ApiOperation("创建api限流")
//  @PostMapping("rate/limiter")
//  public Result<Boolean> createApiRateLimiter(@Valid @RequestBody ApiRateLimiterDto apiRateLimiterDto) {
//    try {
//      apiRateLimiterService.saveApiRateLimiter(apiRateLimiterDto.getApiPathId(), apiRateLimiterDto.getReplenishRate(),
//          apiRateLimiterDto.getLockTime());
//      return new Result<>(true);
//    } catch (Exception e) {
//      LOGGER.error(e.getMessage(), e);
//      return Result.error(MessageCode.ERROR_500.getCode(), MessageCode.ERROR_500.getMessage());
//    }
//  }




}
