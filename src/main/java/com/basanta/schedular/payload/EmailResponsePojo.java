package com.basanta.schedular.payload;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmailResponsePojo {
  private boolean success;
  private String jobId;
  private String jobGroup;
  private String message;

  public EmailResponsePojo(boolean success, String message) {
    this.success = success;
    this.message = message;
  }
}
