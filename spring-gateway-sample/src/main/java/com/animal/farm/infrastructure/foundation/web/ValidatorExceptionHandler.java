package com.animal.farm.infrastructure.foundation.web;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.animal.farm.infrastructure.foundation.exception.DataPlatformException;


/**
 * 拦截参数校验异常，统一进行返回处理
 *
 * @author zhangws 2019/07/08
 */
@RestControllerAdvice
public class ValidatorExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {

    List<String> errors = ex.getBindingResult().getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.toList());

    String requestPath;
    if (request instanceof ServletWebRequest) {
      requestPath = ((ServletWebRequest) request).getRequest().getRequestURI();
    } else {
      requestPath = request.getContextPath();
    }

    logger.error(String.format("请求：%s 发生异常：%s", requestPath, errors));
    return new ResponseEntity<>(
        new Response(HttpStatus.BAD_REQUEST.value(), String.join("\n", errors)), headers,
        HttpStatus.OK);
  }

  @Override
  @Nullable
  protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {

    String requestPath;
    if (request instanceof ServletWebRequest) {
      requestPath = ((ServletWebRequest) request).getRequest().getRequestURI();
    } else {
      requestPath = request.getContextPath();
    }
    logger.error(String.format("请求：%s 发生异常：%s", requestPath, ex.getMessage()));

    return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(), ex.getMessage()),
        headers, HttpStatus.OK);
  }

  @ExceptionHandler(DataPlatformException.class)
  public Response handleDataPlatformException(DataPlatformException e, HttpServletRequest request,
      HttpServletResponse response) {
    logger.error(e.getMessage(), e);
    response.setStatus(200);
    return new Response(e.getCode(), e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public Response handleDataPlatformException(Exception e, HttpServletRequest request,
      HttpServletResponse response) {
    logger.error(e.getMessage(), e);
    response.setStatus(200);
    return new Response(MessageCode.ERROR_500.getCode(), MessageCode.ERROR_500.getMessage());
  }
}
