package io.df.henry.example.dto;

public class UserEventVo {
  private String timestamp;
  private String userAgent;
  private String colorName;
  private String userName;

  public UserEventVo(String timestamp, String userAgent, String colorName, String userName) {
    this.timestamp = timestamp;
    this.userAgent = userAgent;
    this.colorName = colorName;
    this.userName = userName;
  }
}
