package com.animal.farm.familytree.infrastructure.web;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author zhangws
 */
public class Response {

  public static final int SUCCESS_CODE = 200;

  public static final String SUCCESS_MSG = "success";

  private int code = SUCCESS_CODE;

  private String error = null;

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  @JsonIgnore
  public boolean isSuccess() {
    return code == SUCCESS_CODE;
  }

  @JsonIgnore
  public boolean isFail() {
    return code != SUCCESS_CODE;
  }

  public Response() {
  }

  public Response(int code, String error) {
    this.code = code;
    this.error = error;
  }

  public Response(ErrorCode errorCode) {
    this.code = errorCode.getCode();
    this.error = errorCode.getMessage();
  }
}
