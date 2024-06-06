package com.basanta.schedular.quartz.job;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class EmailJob extends QuartzJobBean {

  private static final Logger log = LoggerFactory.getLogger(EmailJob.class);
  @Autowired
  private JavaMailSender mailSender;

  @Autowired
  private MailProperties mailProperties;

  protected void executeInternal(JobExecutionContext context){
    JobDataMap jobDataMap = context.getMergedJobDataMap();
    String recepientEmail = jobDataMap.getString("email");
    String subject = jobDataMap.getString("subject");
    String body = jobDataMap.getString("body");

    sendEmail(mailProperties.getUsername(), recepientEmail, subject, body);

  }

  private void sendEmail(String to, String from,String subject, String body){
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper mimeMessageHelper =  new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.toString());
      mimeMessageHelper.setSubject(subject);
      mimeMessageHelper.setText(body);
      mimeMessageHelper.setFrom(from);
      mimeMessageHelper.setTo(to);

      mailSender.send(mimeMessage);
    }catch (MessagingException messagingException){
      log.error(messagingException.getMessage(), messagingException);
    }

  }
}
