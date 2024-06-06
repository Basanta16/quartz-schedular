package com.basanta.schedular.payload;

import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Getter
@Setter
public class EmailRequestPojo {


  @NonNull
  private String email;

  @NonNull
  private String subject;

  @NonNull
  private String body;
  @NonNull
  private LocalDateTime dateTime;
  @NonNull
  private ZoneId timeZone;

}
