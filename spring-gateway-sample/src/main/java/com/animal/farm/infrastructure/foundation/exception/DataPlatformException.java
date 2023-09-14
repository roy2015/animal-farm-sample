package com.animal.farm.infrastructure.foundation.exception;


import com.animal.farm.infrastructure.foundation.web.ErrorCode;

/**
 * @author david 2020/11/16
 */
public class DataPlatformException extends Exception {
  private int code;

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public DataPlatformException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.code = errorCode.getCode();
  }

  public DataPlatformException(ErrorCode errorCode, Object... args) {
    super(String.format(errorCode.getMessage(), args));
    this.code = errorCode.getCode();
  }

  public DataPlatformException(ErrorCode errorCode, Throwable cause, Object... args) {
    super(String.format(errorCode.getMessage(), args), cause);
    this.code = errorCode.getCode();
  }

  public DataPlatformException(int code, String message) {
    super(message);
    this.code = code;
  }

  public DataPlatformException(int code, Throwable cause) {
    super(cause);
    this.code = code;
  }

  public DataPlatformException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.code = errorCode.getCode();
  }

  public DataPlatformException(int code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }
}
