package com.VsmartEngine.MediaJungle.MailVerification;

import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import com.VsmartEngine.MediaJungle.LogManagement;
import com.VsmartEngine.MediaJungle.model.MailSetting;
import com.VsmartEngine.MediaJungle.repository.MailsettingRepository;

@Service
public class EmailService {
	
	 @Autowired
	 private JavaMailSender mailSender;
	 
	 @Autowired
	 private MailsettingRepository mailsettingrepository;
	 	 
	 private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
	// Send email with dynamic configuration and return success status
	 public boolean sendEmail(String to, String subject, String body) {
	     try {
	         Optional<MailSetting> mailConfigOpt = mailsettingrepository.findFirstByOrderByIdAsc();

	         if (mailConfigOpt.isPresent()) {
	             MailSetting mailConfig = mailConfigOpt.get();
	             JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
	             mailSenderImpl.setHost(mailConfig.getMailhostname());
	             mailSenderImpl.setPort(mailConfig.getMailportname());
	             mailSenderImpl.setUsername(mailConfig.getEmailid());
	             mailSenderImpl.setPassword(mailConfig.getPassword());

	             Properties props = mailSenderImpl.getJavaMailProperties();
	             props.put("mail.transport.protocol", "smtp");
	             props.put("mail.smtp.auth", "true");
	             props.put("mail.smtp.starttls.enable", "true");
	             props.put("mail.smtp.connectiontimeout", "3000");
	             props.put("mail.smtp.timeout", "3000");
	             props.put("mail.smtp.writetimeout", "3000");

	             if (mailConfig.getMailportname() == 465) {
	                 props.put("mail.smtp.ssl.enable", "true");
	             }

	             SimpleMailMessage message = new SimpleMailMessage();
	             message.setTo(to);
	             message.setSubject(subject);
	             message.setText(body);
	             message.setFrom(mailConfig.getEmailid());
	             mailSenderImpl.send(message);
	             return true;
	         } else if (mailSender != null) {
	             SimpleMailMessage message = new SimpleMailMessage();
	             message.setTo(to);
	             message.setSubject(subject);
	             message.setText(body);
	             mailSender.send(message);
	             return true;
	         } else {
	             logger.warn("No mail configuration found in database and default mailSender is null.");
	             return false;
	         }
	     } catch (Exception e) {
	         logger.error("Error sending email to {}: {}", to, e.getMessage());
	         return false;
	     }
	 }



}