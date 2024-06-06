package com.basanta.schedular.controller;

import com.basanta.schedular.payload.EmailRequestPojo;
import com.basanta.schedular.payload.EmailResponsePojo;
import com.basanta.schedular.quartz.job.EmailJob;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("schedular")
public class EmailSchedularController {

  @Autowired
  private Scheduler scheduler;

  @PostMapping(value = "/email", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EmailResponsePojo> scheduleEmail (@RequestBody EmailRequestPojo emailRequestPojo) {
    try {
      ZonedDateTime zonedDateTime = ZonedDateTime.of(emailRequestPojo.getDateTime(), emailRequestPojo.getTimeZone());
      if(zonedDateTime.isBefore(ZonedDateTime.now())){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new EmailResponsePojo(false, "Date Time should be after Curent time"));
      }

      JobDetail jobDetail = buildJobDetail(emailRequestPojo);
      Trigger trigger = buildTrigger(jobDetail, zonedDateTime);

      scheduler.scheduleJob(trigger);
      return ResponseEntity.ok(new EmailResponsePojo(true, jobDetail.getKey().getName(),
        jobDetail.getKey().getGroup(), "Email scheduled"));
    }catch (SchedulerException e) {
      log.error("Error while scheduling email", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new EmailResponsePojo(false,
          "Error scheduling email"));
    }
  }
  private JobDetail buildJobDetail(EmailRequestPojo emailRequestPojo) {
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("email", emailRequestPojo.getEmail());
    jobDataMap.put("subject", emailRequestPojo.getSubject());
    jobDataMap.put("body", emailRequestPojo.getBody());

    return JobBuilder.newJob(EmailJob.class)
      .withIdentity(UUID.randomUUID().toString(), "email-jobs")
      .withDescription("Send Email Job")
      .usingJobData(jobDataMap)
      .storeDurably()
      .build();
  }

  private Trigger buildTrigger(JobDetail jobDetail, ZonedDateTime zonedDateTime){
    return TriggerBuilder.newTrigger()
      .forJob(jobDetail)
      .withIdentity(jobDetail.getKey().getName(), "email-triggers")
      .withDescription("Send Email triggers")
      .startAt(Date.from(zonedDateTime.toInstant()))
      .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
      .build();
  }

  @GetMapping
  public ResponseEntity<String> getApiTest() {
    return ResponseEntity.ok("Get API TEST");
  }
}
