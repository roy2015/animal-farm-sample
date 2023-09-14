package com.animal.farm.application.module.gateway.dto;

/**
 * @author zhouzhiyuan
 * @date 2022/11/1 11:12
 */
public class LockedUserInformationDto {
  private String apiPath;//需要限流的apiPath
  private String userId;//需要限流的userId
  private Long unlockedTiem;//解锁时间
  private Long startLockTime;//锁定时间
  private Long lockTime;//锁定时间

  public LockedUserInformationDto(String apiPath, String userId, Long unlockedTiem,Long startLockTime,Long lockTime) {
    this.apiPath = apiPath;
    this.userId = userId;
    this.unlockedTiem = unlockedTiem;
    this.startLockTime = startLockTime;
    this.lockTime = lockTime;
  }

  public Long getStartLockTime() {
    return startLockTime;
  }

  public void setStartLockTime(Long startLockTime) {
    this.startLockTime = startLockTime;
  }

  public Long getLockTime() {
    return lockTime;
  }

  public void setLockTime(Long lockTime) {
    this.lockTime = lockTime;
  }

  public String getApiPath() {
    return apiPath;
  }

  public void setApiPath(String apiPath) {
    this.apiPath = apiPath;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Long getUnlockedTiem() {
    return unlockedTiem;
  }

  public void setUnlockedTiem(Long unlockedTiem) {
    this.unlockedTiem = unlockedTiem;
  }

  @Override
  public String toString() {
    return "LockedUserInformationDto{" +
        "apiPath='" + apiPath + '\'' +
        ", userId='" + userId + '\'' +
        ", unlockedTiem=" + unlockedTiem +
        ", startLockTime=" + startLockTime +
        ", lockTime=" + lockTime +
        '}';
  }
}
