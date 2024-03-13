package com.eschool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	@Autowired
    private JavaMailSender javaMailSender;
    public void sendEmail(String receiver, String subject, String body) {
    	Logger log=null;
    	try {
    	log=LoggerFactory.getLogger(getClass());
    	SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(receiver);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
       }
       catch(Exception ex)
       {
    	   log.error("While Sending Mail"+ex.getMessage());
       }
    }
}
