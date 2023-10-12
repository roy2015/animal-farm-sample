package com.animal.farm.familytree.infrastructure.web;

/**
 * @author zhengyangyong
 */
public interface ErrorCode {
  /** 错误码编号 */
  int getCode();

  /** 错误码描述 */
  String getMessage();
}
