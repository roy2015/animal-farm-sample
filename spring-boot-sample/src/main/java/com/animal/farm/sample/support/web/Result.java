package com.animal.farm.sample.support.web;

/**
 * @author david
 * @date 2020/11/17 8:14 下午
 */
public class Result<T> extends Response {
  /**
   * 数据
   */
  private T data;

  /**
   * 构造函数 空
   */
  public Result() {
  }

  /**
   * 构造函数 数据
   * @param data
   */
  public Result(T data) {
    this.data = data;
  }

  /**
   * 构造失败的返回
   * @param errorCode
   */
  public Result(ErrorCode errorCode) {
    super(errorCode);
  }

  /**
   * 构造函数
   * @param code
   * @param error
   * @param data
   */
  public Result(int code, String error, T data) {
    super(code, error);
    this.data = data;
  }

  /**
   * 构造成功的返回
   * @param data
   * @param <T>
   * @return
   */
  public static <T> Result<T> success(T data) {
    return new Result<>(SUCCESS_CODE, SUCCESS_MSG, data);
  }

  /**
   * 构造失败的返回
   * @param code
   * @param message
   * @param <T>
   * @return
   */
  public static <T> Result<T> error(int code, String message) {
    return new Result<T>(code, message, null);
  }

  /**
   * 构造失败的返回
   * @param errorCode
   * @param <T>
   * @return
   */
  public static <T> Result<T> error(ErrorCode errorCode) {
    return new Result<>(errorCode);
  }

  /**
   * 获取数据
   * @return
   */
  public T getData() {
    return data;
  }

  /**
   * 设置数据
   * @param data
   */
  public void setData(T data) {
    this.data = data;
  }
}
