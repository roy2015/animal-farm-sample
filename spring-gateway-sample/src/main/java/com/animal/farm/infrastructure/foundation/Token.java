package com.animal.farm.infrastructure.foundation;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author : zhengyangyong
 */
public class Token {
  @JsonIgnore
  private String token;

  private String userId;

  private String userName;

  private String userNickname;

  private String organizationId;

  private String organizationName;

  private String namespaceId;

  private Long applicationId;

  private String loginType;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date expired;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getUserId() {
    return userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserNickname() {
    return userNickname;
  }

  public void setUserNickname(String userNickname) {
    this.userNickname = userNickname;
  }

  public String getOrganizationName() {
    return organizationName;
  }

  public void setOrganizationName(String organizationName) {
    this.organizationName = organizationName;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getNamespaceId() {
    return namespaceId;
  }

  public void setNamespaceId(String namespaceId) {
    this.namespaceId = namespaceId;
  }

  public Long getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Long applicationId) {
    this.applicationId = applicationId;
  }

  public String getLoginType() {
    return loginType;
  }

  public void setLoginType(String loginType) {
    this.loginType = loginType;
  }

  public Date getExpired() {
    return expired;
  }

  public void setExpired(Date expired) {
    this.expired = expired;
  }

  public String getOrganizationId() {
    return organizationId;
  }

  public void setOrganizationId(String organizationId) {
    this.organizationId = organizationId;
  }

  public Token() {
  }

  public Token(String userId, String userName, String userNickname, String organizationId, String organizationName,
      String namespaceId,
      Long applicationId, String loginType, Date expired) {
    this.userId = userId;
    this.userName = userName;
    this.userNickname = userNickname;
    this.organizationId = organizationId;
    this.organizationName = organizationName;
    this.namespaceId = namespaceId;
    this.applicationId = applicationId;
    this.loginType = loginType;
    this.expired = expired;
  }
}
