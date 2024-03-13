package com.eschool.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.http.HttpHeaders;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.ErrorManager;

import org.apache.xmlbeans.impl.common.SystemCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eschool.beans.Event;
import com.eschool.beans.MessageTemplates;
import com.eschool.beans.OneTimePassword;
import com.eschool.beans.SMSHandler;
import com.eschool.beans.User;
import com.eschool.repo.EventRepository;
import com.eschool.repo.OneTimePasswordRepository;
import com.eschool.repo.UserRepository;
import com.eschool.service.EmailService;
import com.eschool.service.ExcelService;
import com.eschool.service.PDFService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.ServletContext;
@RestController
public class UserController {
	@Autowired
	UserRepository uRepo;
	@Autowired
	ServletContext context;
	@Autowired
	EmailService emailService;
	@Autowired
	OneTimePasswordRepository oRepo;
	@Autowired
	EventRepository eRepo;
	
	@PostMapping("verifyMobileNumberEmailUsingOTP")
    public ResponseEntity<Object> verifyOTP(@RequestParam("otp") String otp, @RequestParam("email") String email) {
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;
		Map<String, Object> data = new HashMap<>();
		try
		{    	
    	OneTimePassword oneTimePassword=oRepo.findByEmail(email);
       	if(oneTimePassword==null)
       	{
       		errorCode=400;
       		errorMessage="Invalid Email";
       	}
       	else if(oneTimePassword.getOtp().equals(otp))
       	{
       		message="Valid OTP";
       	}
       	else
       	{
       		errorCode=400;
       		errorMessage="Invalid OTP";
       	}    
		}
		catch(Exception e)
		{
			errorCode=500;
			errorMessage=e.getMessage();
			log.error("Inside verifyMobileNumberEmailUsingOTP"+errorMessage);
		}		
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }
	
	@PostMapping("verifyMobileNumberEmail")
	public ResponseEntity<Object> verifyMobileNumberEmail(@RequestParam("email") String email,@RequestParam("countryCode") String countryCode,@RequestParam("mobileNumber") String mobileNumber) {
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;
		Map<String, Object> data = new HashMap<>();		
		try {			
			if(uRepo.findByEmail(email)!=null) {
				errorMessage="Email Already Exists";
				errorCode=400;
			}
			else if(uRepo.findByMobileNumber(mobileNumber)!=null) {
				errorMessage="Mobile Number Already Exists";
				errorCode=400;
			}			
			else {	
					 Random random = new Random(); 
			    	 int number = 1000 + random.nextInt(9000);
			         String otp = ""+number;
			        try
			         {
			        	OneTimePassword oneTimePassword=oRepo.findByEmail(email);
			        	if(oneTimePassword!=null)
			        	{
			        		oneTimePassword.setOtp(otp);
			        		oneTimePassword.setTimeOTPGenerated(System.currentTimeMillis());
			        	}
			        	else
			        	oneTimePassword=new OneTimePassword(0, email, mobileNumber, countryCode,otp,System.currentTimeMillis());
			        	oRepo.save(oneTimePassword);
			        	emailService.sendEmail(email,"OTP", "Your OTP is"+otp);
			        	SMSHandler smsHandler=new SMSHandler();
			        	smsHandler.sendSMS(mobileNumber, otp);
			        	message = "OTP Sent Successfully";
			         }
			         catch(Exception e)
			         {
			        	 errorMessage=e.getMessage();
			        	 errorCode=500;
			        	 log.error("Inside verifyMobileNumberEmail"+errorMessage);
			         }         
			     }     
			} 
		catch (Exception e) {			
			errorMessage = e.getMessage();
			errorCode=500;
			log.error("Inside verifyMobileNumberEmail"+errorMessage);
		}		
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
   }
	
	@PostMapping("loginUsingOTP")
    public ResponseEntity<Object> loginUsingOTP(@RequestParam("otp") String otp, @RequestParam("email") String email,@RequestParam("mobileNumber") String mobileNumber,@RequestParam("countryCode") String countryCode) {
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;
		String token="";
    	Map<String, Object> data = new HashMap<>();
    	try
    	{
    	OneTimePassword oneTimePassword=null;
    	User user=null;
    	if(email.length()!=0)
    	{
    		oneTimePassword=oRepo.findByEmail(email);
    		user=uRepo.findByEmail(email);
    	}
    	else
    	{
    		oneTimePassword=oRepo.findByCountryCodeAndMobileNumber(countryCode, mobileNumber);
    		user=uRepo.findByCountryCodeAndMobileNumber(countryCode, mobileNumber);
    	}
       	if(oneTimePassword==null)
       	{
       		errorCode=400;
       		errorMessage="Invalid Email or Mobile number";
       	}
       	else if(oneTimePassword.getOtp().equals(otp))
       	{      		
       				token = Jwts.builder()
					.claim("id", user.getId())
					.claim("name", user.getName())
					.claim("email", user.getEmail())
					.claim("mobileNumber", user.getMobileNumber())						
					.claim("role", user.getRole())
					.claim("familyId", user.getFamilyId())
					.setIssuedAt(new Date())
					.setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
					.signWith(SignatureAlgorithm.HS256,"9wJYK7g67fTRC29iP6VnF89h5sW1rDcT3uXvA0qLmB4zE1pN8rS7zT0qF2eR5vJ3")
					.compact();       				
			message="success";
       	}
       	else
       	{
       		errorMessage="Invalid OTP";
       		errorCode=400;
       	}
       	if (user != null && message.equals("success")) 
       	{
			if(user.getStatus().equals("Pending")) {
				errorMessage="Unapproved User";
				emailService.sendEmail(user.getEmail(), "Regarding Ajapa Registration",new MessageTemplates().getPendingUserTryingLogin());
				message="";
				errorCode=400;
				token="";
			}
			else if(user.getStatus().equals("Deleted")) {
				errorMessage="Deleted User";
				message="";
				errorCode=400;
				token="";
			}	
			else if(user.getStatus().equals("Rejected")) {
				errorMessage="Rejected User";
				emailService.sendEmail(user.getEmail(), "Regarding Ajapa Registration",new MessageTemplates().getOnceAdminReject());
				message="";
				errorCode=400;
				token="";
			}	
			else
			{
				user.setPassword("");
				data.put("user",user);
			}
		}
    	}
    	catch(Exception e)
    	{
    		errorCode=500;
    		errorMessage=e.getMessage();
    		log.error("Inside loginUsingOTP"+errorMessage);
    	}       	
    	data.put("token",token);
    	data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }
		
	@PostMapping("sendOTPForLogin")
	public ResponseEntity<Object> sendOTPForLogin(@RequestParam("email") String email,@RequestParam("countryCode") String countryCode,@RequestParam("mobileNumber") String mobileNumber) {
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;
		Map<String, Object> data = new HashMap<>();
		try
    	{
		User user=null;
		if(email.length()!=0)
		{
			user=uRepo.findByEmail(email);
		}
		else
		{
			user=uRepo.findByCountryCodeAndMobileNumber(countryCode,mobileNumber);
		}
		if(user!=null)
		{
					 Random random = new Random(); 
			    	 int number = 1000 + random.nextInt(9000);
			         String otp = ""+number;
			         OneTimePassword oneTimePassword=null;
			         if(email.length()!=0)
			         {
			        	oneTimePassword=oRepo.findByEmail(email);
			        	if(oneTimePassword!=null)
			        	{
			        		oneTimePassword.setOtp(otp);
			        		oneTimePassword.setTimeOTPGenerated(System.currentTimeMillis());
			        	}
			        	else
			        	oneTimePassword=new OneTimePassword(0, email, user.getMobileNumber(), user.getCountryCode(),otp,System.currentTimeMillis());
			         }
			         else
			         {
			        	    oneTimePassword=oRepo.findByCountryCodeAndMobileNumber(countryCode, mobileNumber);
				        	if(oneTimePassword!=null)
				        	{
				        		oneTimePassword.setOtp(otp);
				        		oneTimePassword.setTimeOTPGenerated(System.currentTimeMillis());
				        	}
				        	else
				        	oneTimePassword=new OneTimePassword(0, user.getEmail(),mobileNumber,countryCode,otp,System.currentTimeMillis());
				    
			         }
			        	oRepo.save(oneTimePassword);
			        	if(email.length()!=0)
			        	emailService.sendEmail(email,"OTP", "Your OTP is"+otp);
			        	else
			        	{
			        	SMSHandler smsHandler=new SMSHandler();
			        	smsHandler.sendSMS(mobileNumber, otp);
			        	}
			        	message = "OTP Sent Successfully";
		}
		else
		{
		errorMessage="Invalid Email or Mobile Number";
		errorCode=400;
		}
    	}
    	catch(Exception e)
    	{
    		errorMessage=e.getMessage();
    		errorCode=500;
    		log.error("Inside sendOTPForLogin"+errorMessage);
    	}
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    
	}
	
	@PostMapping("verifyOTP")
    public ResponseEntity<Object> verifyOTP(@RequestParam("otp") String otp, @RequestParam("email") String email,@RequestParam("mobileNumber") String mobileNumber,@RequestParam("countryCode") String countryCode) {
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;
		String token="";
    	Map<String, Object> data = new HashMap<>();
    	try
    	{
    	OneTimePassword oneTimePassword=null;
    	User user=null;
    	if(email.length()!=0)
    	{
    		oneTimePassword=oRepo.findByEmail(email);
    		user=uRepo.findByEmail(email);
    	}
    	else
    	{
    		oneTimePassword=oRepo.findByCountryCodeAndMobileNumber(countryCode, mobileNumber);
    		user=uRepo.findByCountryCodeAndMobileNumber(countryCode, mobileNumber);
    	}
       	if(oneTimePassword==null)
       	{
       		errorCode=400;
       		errorMessage="Invalid Email or Mobile number";
       	}
       	else if(oneTimePassword.getOtp().equals(otp))
       	{       		
       				token = Jwts.builder()
					.claim("id", user.getId())
					.claim("name", user.getName())
					.claim("email", user.getEmail())
					.claim("mobileNumber", user.getMobileNumber())						
					.claim("role", user.getRole())
					.claim("familyId", user.getFamilyId())
					.setIssuedAt(new Date())
					.setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
					.signWith(SignatureAlgorithm.HS256,"9wJYK7g67fTRC29iP6VnF89h5sW1rDcT3uXvA0qLmB4zE1pN8rS7zT0qF2eR5vJ3")
					.compact();       				
			message="success";
       	}
       	else
       	{
       		errorMessage="Invalid OTP";
       		errorCode=400;
       	}
    	}
    	catch(Exception e)
    	{
    		errorCode=500;
    		errorMessage=e.getMessage();
    		log.error("Inside verifyOTP"+errorMessage);
    	}    	
    	data.put("token",token);
    	data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }	
	
	@PostMapping("signup")
	public ResponseEntity<Object> saveUser(@ModelAttribute User user,@RequestParam("file") MultipartFile file) {
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;
		int age=0;
		try {
        	 // Parse the birthdate string to a LocalDate object
            LocalDate birthdate = LocalDate.parse(user.getDob());

            // Get the current date
            LocalDate currentDate = LocalDate.now();

            // Calculate the period between the birthdate and current date
            Period period = Period.between(birthdate, currentDate);

            // Extract years, months, and days from the period
            int years = period.getYears();
            int months = period.getMonths();
            int days = period.getDays();
            age=years;
        }
        catch(Exception e)
        {
        	errorCode=500;        	
        	errorMessage=e.getMessage();
        	log.error("Inside signup"+errorMessage);
        }
		Map<String, Object> data = new HashMap<>();		
		try {			
			if(age>=15 && uRepo.findByEmail(user.getEmail())!=null ) {
				errorMessage="Email Already Exists";
				errorCode=400;
			}
			else if(age>=15 && uRepo.findByCountryCodeAndMobileNumber(user.getCountryCode(),user.getMobileNumber())!=null) {
				errorMessage="Mobile Number Already Exists";
				errorCode=400;
			}
			else if(age<15 && user.getEmail().length()!=0 && uRepo.findByEmail(user.getEmail())!=null ) {
				errorMessage="Email Already Exists";
				errorCode=400;
			}
			else if(age<15 && user.getMobileNumber().length()!=0 && uRepo.findByCountryCodeAndMobileNumber(user.getCountryCode(),user.getMobileNumber())!=null) {
				errorMessage="Mobile Number Already Exists";
				errorCode=400;
			}
			else {
			if(user.getRole()==null)
			user.setRole("User");
			
			if(user.getStatus()==null || user.getStatus().length()==0)
			user.setStatus("Pending");
			
			String fname=System.currentTimeMillis()+".jpg";
			user.setProfileImage(fname);
			uRepo.save(user);
			
			if(age>=15)
			{
			User savedUser=null;
			savedUser= uRepo.findByEmail(user.getEmail());
			if(user.getFamilyId()==0)
			{
			savedUser.setFamilyId(savedUser.getId());			
			uRepo.save(savedUser);
			}
			}
			if(file!=null)
			{
			String path = context.getRealPath("/") + "\\images";
			File fl = new File(path);
			if (!fl.exists())
				fl.mkdir();
			Path root = Paths.get(path + "\\" + fname);
			if (Files.exists(root)) {
				Files.delete(root);
			}
			Files.copy(file.getInputStream(), root);
			}
			emailService.sendEmail(user.getEmail(),"Regarding AJAPA Registration",new MessageTemplates().getSignupEmailMessage());
			message = "Data saved successfully";
			}
		} catch (Exception e) {			
			errorMessage = e.getMessage();
			errorCode=500;
			log.error("Inside signup"+errorMessage);
		}		
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }
	
	@PostMapping("/updateUserWithImage")
	public ResponseEntity<Object> updateUserWithImage(@ModelAttribute User user,@RequestParam("file") MultipartFile file,@RequestHeader("Authorization") String authorizationHeader) {
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;
		int sid=0;
		String email=null,role=null;
		int familyId=0;
		Map<String, Object> data = new HashMap<>();
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String jwtToken = authorizationHeader.substring(7); // Removing "Bearer " prefix
			try {
				Base64.Decoder decoder = Base64.getUrlDecoder();
				String chunks[] = jwtToken.split("\\.");
				String payload = new String(decoder.decode(chunks[1]));
				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> map = mapper.readValue(payload, Map.class);
				email = map.get("email").toString();
				role=map.get("role").toString();
				sid=Integer.valueOf(map.get("id").toString());				
				familyId=Integer.valueOf(map.get("familyId").toString());
				errorCode=0;
			} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();
				log.error("Inside updateUserWithImage"+errorMessage);
			}
		}
		
		int age=0;
		try {
       	 // Parse the birthdate string to a LocalDate object
           LocalDate birthdate = LocalDate.parse(user.getDob());

           // Get the current date
           LocalDate currentDate = LocalDate.now();

           // Calculate the period between the birthdate and current date
           Period period = Period.between(birthdate, currentDate);

           // Extract years, months, and days from the period
           int years = period.getYears();
           int months = period.getMonths();
           int days = period.getDays();
           age=years;
       }
       catch(Exception e)
       {
       	errorCode=500;        	
       	errorMessage=e.getMessage();
       	log.error("Inside updateUserWithImage "+errorMessage);
       }
		User existingUserByEmail=uRepo.findByEmail(user.getEmail());
		User existingUserByMobileNumber=uRepo.findByCountryCodeAndMobileNumber(user.getCountryCode(),user.getMobileNumber());
				
		if(age>=15 && (existingUserByEmail!=null && existingUserByEmail.getId()!=user.getId())) {
			errorMessage="Email Already Exists";
			errorCode=400;
		}
		else if(age>=15 && (existingUserByMobileNumber!=null && existingUserByMobileNumber.getId()!=user.getId())) {
			errorMessage="Mobile Number Already Exists";
			errorCode=400;
		}
		if(age<15 && user.getEmail().length()!=0 && (existingUserByEmail!=null && existingUserByEmail.getId()!=user.getId())) {
			errorMessage="Email Already Exists";
			errorCode=400;
		}
		else if(age<15 && user.getMobileNumber().length()!=0 && (existingUserByMobileNumber!=null && existingUserByMobileNumber.getId()!=user.getId())) {
			errorMessage="Mobile Number Already Exists";
			errorCode=400;
		}
		
		
		if(errorCode==0 && (role.equals("Super")||role.equals("Admin")||sid==user.getId()||(familyId==user.getFamilyId() && role.equals("User"))))
		{
		try {	
			String fname=System.currentTimeMillis()+".jpg";
		    user.setProfileImage(fname);			
			uRepo.save(user);
			if(file!=null)
			{
			String path = context.getRealPath("/") + "\\images";
			File fl = new File(path);
			if (!fl.exists())
				fl.mkdir();
			Path root = Paths.get(path + "\\" + fname);
			if (Files.exists(root)) {
				Files.delete(root);
			}
			Files.copy(file.getInputStream(), root);
			}
			message = "Data updated successfully";
		} catch (Exception e) {			
			errorMessage = e.getMessage();
			errorCode=500;
			log.error("Inside updateUserWithImage"+errorMessage);
		}
		}
		
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }
	
	@PostMapping("/updateUser")
	public ResponseEntity<Object> updateUser(@ModelAttribute User user,@RequestHeader("Authorization") String authorizationHeader) {
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;
		int sid=0;
		String email=null,role=null;
		int familyId=0;
		Map<String, Object> data = new HashMap<>();
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String jwtToken = authorizationHeader.substring(7); // Removing "Bearer " prefix
			try {
				Base64.Decoder decoder = Base64.getUrlDecoder();
				String chunks[] = jwtToken.split("\\.");
				String payload = new String(decoder.decode(chunks[1]));
				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> map = mapper.readValue(payload, Map.class);
				email = map.get("email").toString();
				role=map.get("role").toString();
				sid=Integer.valueOf(map.get("id").toString());				
				familyId=Integer.valueOf(map.get("familyId").toString());
				errorCode=0;
			} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();
				log.error("Inside updateUser"+errorMessage);
			}
		}
		
		int age=0;
		try {
       	 // Parse the birthdate string to a LocalDate object
           LocalDate birthdate = LocalDate.parse(user.getDob());

           // Get the current date
           LocalDate currentDate = LocalDate.now();

           // Calculate the period between the birthdate and current date
           Period period = Period.between(birthdate, currentDate);

           // Extract years, months, and days from the period
           int years = period.getYears();
           int months = period.getMonths();
           int days = period.getDays();
           age=years;
       }
       catch(Exception e)
       {
       	errorCode=500;        	
       	errorMessage=e.getMessage();
       	log.error("Inside updateUser"+errorMessage);
       }
		User existingUserByEmail=null;
		User existingUserByMobileNumber=null;
		try
		{
		existingUserByEmail=uRepo.findByEmail(user.getEmail());	
		existingUserByMobileNumber=uRepo.findByCountryCodeAndMobileNumber(user.getCountryCode(),user.getMobileNumber());
		}
		catch(Exception ex)
		{
			log.error("Inside updateUser"+errorMessage);
		}
		if(age>=15 && (existingUserByEmail!=null && existingUserByEmail.getId()!=user.getId())) {
			errorMessage="Email Already Exists";
			errorCode=400;
		}
		else if(age>=15 && (existingUserByMobileNumber!=null && existingUserByMobileNumber.getId()!=user.getId())) {
			errorMessage="Mobile Number Already Exists";
			errorCode=400;
		}
		if(age<15 && user.getEmail().length()!=0 && (existingUserByEmail!=null && existingUserByEmail.getId()!=user.getId())) {
			errorMessage="Email Already Exists";
			errorCode=400;
		}
		else if(age<15 && user.getMobileNumber().length()!=0 && (existingUserByMobileNumber!=null && existingUserByMobileNumber.getId()!=user.getId())) {
			errorMessage="Mobile Number Already Exists";
			errorCode=400;
		}	
		
		if(errorCode==0 && (role.equals("Super")||role.equals("Admin")||sid==user.getId()||(familyId==user.getFamilyId() && role.equals("User"))))
		{
			try {			
			uRepo.save(user);			
			message = "Data updated successfully";
		} catch (Exception e) {			
			errorMessage = e.getMessage();
			errorCode=500;
			log.error("Inside updateUser"+errorMessage);
		}
		}
		
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }
	
	
	@PostMapping("login")
	public ResponseEntity<Object> login(@RequestParam("email") String email,@RequestParam("countryCode") String countryCode,@RequestParam("mobileNumber") String mobileNumber,@RequestParam("password") String password) {		
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;
		String token="";
		User user=null;
		Map<String, Object> data = new HashMap<>();
		try
		{			
		if(email!=null && email.length()!=0)
		{
			user=uRepo.findByEmail(email);
			if(user==null)
			{
				errorMessage="Invalid Email Id";
				errorCode=400;
			}
			else if(!user.getPassword().equals(password))
			{
				errorMessage="Invalid Password";
				errorCode=400;
			}
			else
			{				
				token = Jwts.builder()
						.claim("id", user.getId())
						.claim("name", user.getName())
						.claim("email", user.getEmail())
						.claim("mobileNumber", user.getMobileNumber())						
						.claim("role", user.getRole())
						.claim("familyId", user.getFamilyId())
						.setIssuedAt(new Date())
						.setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
						.signWith(SignatureAlgorithm.HS256,"9wJYK7g67fTRC29iP6VnF89h5sW1rDcT3uXvA0qLmB4zE1pN8rS7zT0qF2eR5vJ3")
						.compact();
				message="success";				
			}
		}
		else
		{
			user=uRepo.findByCountryCodeAndMobileNumber(countryCode, mobileNumber);
			if(user==null)
			{
				errorMessage="Invalid Mobile Number/Country Code";
				errorCode=400;
			}
			else if(!user.getPassword().equals(password))
			{
				errorMessage="Invalid Password";
				errorCode=400;
			}
			else
			{				
				token = Jwts.builder()
						.claim("id", user.getId())
						.claim("name", user.getName())
						.claim("email", user.getEmail())
						.claim("mobileNumber", user.getMobileNumber())						
						.claim("role", user.getRole())
						.claim("familyId", user.getFamilyId())
						.setIssuedAt(new Date())
						.setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
						.signWith(SignatureAlgorithm.HS256,"9wJYK7g67fTRC29iP6VnF89h5sW1rDcT3uXvA0qLmB4zE1pN8rS7zT0qF2eR5vJ3")
						.compact();
				message="success";
				
			}
		}	
		
		if (user != null && message.equals("success")) {
			if(user.getStatus().equals("Pending")) {
				errorMessage="Unapproved User";
				emailService.sendEmail(user.getEmail(),"Regarding AJAPA Registration",new MessageTemplates().getPendingUserTryingLogin());
				message="";
				errorCode=400;
				token="";
			}
			else if(user.getStatus().equals("Deleted")) {
				errorMessage="Deleted User";
				message="";
				errorCode=400;
				token="";
			}	
			else if(user.getStatus().equals("Rejected")) {
				errorMessage="Rejected User";
				emailService.sendEmail(user.getEmail(),"Regarding AJAPA Registration",new MessageTemplates().getOnceAdminReject());
				message="";
				errorCode=400;
				token="";
			}	
			else
			{
				user.setPassword("");
				data.put("user",user);
			}
		}
		}
		catch(Exception e)
		{
			errorCode=500;
			errorMessage=e.getMessage();
			log.error("Inside login"+errorMessage);
		}
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);
		data.put("token",token);
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
  	}

	@PostMapping("loginUsingToken")
	public ResponseEntity<Object> loginUsingToken(@RequestHeader("Authorization") String authorizationHeader) {
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;
		String token="";		
		String email = "";
		
		Map<String, Object> data = new HashMap<>();		
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String jwtToken = authorizationHeader.substring(7); // Removing "Bearer " prefix
			try {
				Base64.Decoder decoder = Base64.getUrlDecoder();
				String chunks[] = jwtToken.split("\\.");
				String payload = new String(decoder.decode(chunks[1]));
				ObjectMapper mapper = new ObjectMapper();
				Map<String, String> map = mapper.readValue(payload, Map.class);
				email = map.get("email");
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();
				log.error("Inside loginUsingToken"+errorMessage);
			}
		}
		try
		{
		User user = uRepo.findByEmail(email);
		if(user!=null)
		{			
			if(user.getStatus().equals("Pending")) {
				errorCode=400;
				errorMessage="Unapproved User";
				emailService.sendEmail(user.getEmail(),"Regarding AJAPA Registration",new MessageTemplates().getPendingUserTryingLogin());
			}
			else if(user.getStatus().equals("Deleted")) {
				errorCode=400;
				errorMessage="Deleted User";
			}
			else if(user.getStatus().equals("Rejected")) {
				errorMessage="Rejected User";
				emailService.sendEmail(user.getEmail(),"Regarding AJAPA Registration",new MessageTemplates().getOnceAdminReject());
				message="";
				errorCode=400;
				token="";
			}	
			else
			{
				message="success";
				data.put("message",message);
				token = Jwts.builder()
					.claim("id", user.getId())
					.claim("name", user.getName())
					.claim("email", user.getEmail())
					.claim("mobileNumber", user.getMobileNumber())						
					.claim("role", user.getRole())
					.claim("familyId", user.getFamilyId())
					.setIssuedAt(new Date())
					.setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
					.signWith(SignatureAlgorithm.HS256,"9wJYK7g67fTRC29iP6VnF89h5sW1rDcT3uXvA0qLmB4zE1pN8rS7zT0qF2eR5vJ3")
					.compact();
				data.put("token",token);
				user.setPassword("");
				data.put("user",user);
			}			
		}
		else
		{
			errorCode=400;
			errorMessage="Invalid Token";
		}
		}
		catch(Exception e)
		{
			errorCode=500;
			errorMessage=e.getMessage();
			log.error("Inside loginUsingToken"+errorMessage);
		}
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
  	
	}  
		
	@PostMapping("changePassword")
	public ResponseEntity<Object> changePassword(@RequestHeader("Authorization") String authorizationHeader,@RequestParam("password") String password) {
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;
		String email = "";
		String role="";
		int id=0;
		Map<String, Object> data = new HashMap<>();		
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String jwtToken = authorizationHeader.substring(7); // Removing "Bearer " prefix
			try {
				Base64.Decoder decoder = Base64.getUrlDecoder();
				String chunks[] = jwtToken.split("\\.");
				String payload = new String(decoder.decode(chunks[1]));
				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> map = mapper.readValue(payload, Map.class);
				email = map.get("email").toString();
				role=map.get("role").toString();
				id=Integer.valueOf(map.get("id").toString());
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();
				log.error("Inside changePassword"+errorMessage);
			}
		}
		try
		{
		User user = uRepo.findById(id);
		if(user!=null && errorCode==0)
		{		
			user.setPassword(password);
			uRepo.save(user);
			message="success";
		}
		else
		{
			errorCode=400;
			errorMessage="Invalid Token";
		}
		}
		catch(Exception e)
		{
			errorCode=500;
			errorMessage=e.getMessage();
			log.error("Inside changePassword"+errorMessage);
		}
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);  	
	}  	
	
	@PostMapping("changeStatus")
	public ResponseEntity<Object> changeStatus(@RequestHeader("Authorization") String authorizationHeader,@RequestParam("status") String status,@RequestParam("id") String id) {
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;
		String email = "";
		String role="";
		int sid=0;
		int familyId=0;
		Map<String, Object> data = new HashMap<>();		
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String jwtToken = authorizationHeader.substring(7); // Removing "Bearer " prefix
			try {
				Base64.Decoder decoder = Base64.getUrlDecoder();
				String chunks[] = jwtToken.split("\\.");
				String payload = new String(decoder.decode(chunks[1]));
				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> map = mapper.readValue(payload, Map.class);
				email = map.get("email").toString();
				role=map.get("role").toString();	
				sid=Integer.valueOf(map.get("id").toString());
				familyId=Integer.valueOf(map.get("familyId").toString());
				errorCode=0;
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();	
				log.error("Inside changeStatus"+errorMessage);
			}
		}
		try
		{
		User user = uRepo.findById(Integer.parseInt(id));
		if(user!=null && errorCode==0 && (role.equals("Super") || role.equals("Admin") || (role.equals("User") && familyId==user.getFamilyId())))
		{	
			user.setStatus(status);			
			uRepo.save(user);		
			if(status.equals("Approved"))
				emailService.sendEmail(user.getEmail(),"Regarding AJAPA Registration",new MessageTemplates().getOnceAdminApprove());
			else if(status.equals("Rejected"))
				emailService.sendEmail(user.getEmail(),"Regarding AJAPA Registration",new MessageTemplates().getOnceAdminReject());
			message="User is "+user.getStatus();
		}
		else
		{
			errorCode=400;
			errorMessage="Invalid Token";
		}
		}
		catch(Exception e)
		{
			errorCode=500;
			errorMessage=e.getMessage();
			log.error("Inside changeStatus"+errorMessage);
		}
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);  	
	}  	
	
	@PostMapping("changeHead")
	public ResponseEntity<Object> changeHead(@RequestHeader("Authorization") String authorizationHeader,@RequestParam("id") String id) {
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;
		String email = "";
		String role="";
		int sid=0;
		int familyId=0;
		Map<String, Object> data = new HashMap<>();		
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String jwtToken = authorizationHeader.substring(7); // Removing "Bearer " prefix
			try {
				Base64.Decoder decoder = Base64.getUrlDecoder();
				String chunks[] = jwtToken.split("\\.");
				String payload = new String(decoder.decode(chunks[1]));
				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> map = mapper.readValue(payload, Map.class);
				email = map.get("email").toString();
				role=map.get("role").toString();	
				sid=Integer.valueOf(map.get("id").toString());			
				familyId=Integer.valueOf(map.get("familyId").toString());
				errorCode=0;
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();	
				log.error("Inside changeHead"+errorMessage);
			}
		}
		try
		{
			User newHead = uRepo.findById(Integer.parseInt(id));
			User oldHead = uRepo.findById(sid);
			
		if(oldHead!=null && errorCode==0 && (role.equals("User")&& familyId==newHead.getFamilyId()))
		{		
			newHead.setRole("User");
			oldHead.setRole("Member");
			uRepo.save(newHead);
			uRepo.save(oldHead);			
			message="Your family head has been changed";
		}
		else
		{
			errorCode=400;
			errorMessage="Invalid Token";
		}
		}
		catch(Exception e)
		{
			errorCode=500;
			errorMessage=e.getMessage();
			log.error("Inside changeHead"+errorMessage);
		}
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);  	
	}  	
	
	
	@GetMapping("/user/list")
	public ResponseEntity<Object> getUsers(@RequestParam int page, @RequestParam int rowsPerPage,@RequestParam String searchText,@RequestParam String status,@RequestParam String country,@RequestParam String state,@RequestParam String city,@RequestHeader("Authorization") String authorizationHeader) {
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		String email=null,role=null;		
		int errorCode=0;
		int start=(page-1)*rowsPerPage;
		int end=start+rowsPerPage;
		List<User> myUsers=null;
		List<User> users=null;
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String jwtToken = authorizationHeader.substring(7); // Removing "Bearer " prefix
			try {
				Base64.Decoder decoder = Base64.getUrlDecoder();
				String chunks[] = jwtToken.split("\\.");
				String payload = new String(decoder.decode(chunks[1]));
				ObjectMapper mapper = new ObjectMapper();
				Map<String, String> map = mapper.readValue(payload, Map.class);
				email = map.get("email");
				role = map.get("role");	
				if(role.equals("User")||role.equals("Super")||role.equals("Admin"))
				{
				}
				else
				{
					errorCode=400;
					errorMessage="Invalid Token Values";
				}
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();	
				log.error("Inside /user/list"+errorMessage);
			}
		}
		
		if(errorCode==0 && (role.equals("Super")||role.equals("Admin")))
		{				
		try
		{
			if(country.equals("All") && state.equals("All") && city.equals("All"))
				users=uRepo.findByStatusAndNameOrEmailOrMobileNumberOrderByFamilyId(status, searchText, searchText,searchText,"Super");
			else if(state.equals("All") && city.equals("All"))
				users=uRepo.findByCountryAndStatusAndNameOrEmailOrMobileNumberOrderByFamilyId(country,status, searchText, searchText,searchText,"Super");
			else if(city.equals("All"))
				users=uRepo.findByCountryAndStateAndStatusAndNameOrEmailOrMobileNumberOrderByFamilyId(country,state,status, searchText, searchText,searchText,"Super");
			else
				users=uRepo.findByCountryAndStateAndCityAndStatusAndNameOrEmailOrMobileNumberOrderByFamilyId(country,state,city,status, searchText, searchText,searchText,"Super");
			
			if (start >= 0 && end < users.size() && start <= end) {
	    	errorCode=0;
	    	message="success";
	    	
	        myUsers=users.subList(start, end);
			} 
			else if (start >= 0 && start <= end) 
			{
	    	errorCode=0;
	    	message="success";
	    	myUsers=users.subList(start, users.size());
			}
			else{
	    	errorCode=400;
	    	errorMessage="No more events";
	    	myUsers=Collections.emptyList();
			}
		}
	   catch(Exception e)
	   {
		   errorCode=500;
		   errorMessage=e.getMessage();
		   myUsers=Collections.emptyList();
		   log.error("Inside /user/list"+errorMessage);
	   }
	}
		Map<String, Object> data = new HashMap<>();	
		data.put("data", myUsers);
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);
		if(users!=null)
		data.put("totalElement", users.size());		    
		data.put("pageNumber", page);		
		data.put("rowPerPage", rowsPerPage);
		if(users!=null && users.size()%rowsPerPage==0)
			data.put("totalNoOfPages", users.size()/rowsPerPage);	
		else if(users!=null)
			data.put("totalNoOfPages", users.size()/rowsPerPage+1);	
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
	}
	@GetMapping("report/user/list")
	public ResponseEntity<Object> reportGetUsers(@RequestParam String searchText,@RequestParam String status,@RequestParam String country,@RequestParam String state,@RequestParam String city,@RequestHeader("Authorization") String authorizationHeader) {
		Logger log=LoggerFactory.getLogger(getClass());
		String fileName=context.getRealPath("/")+"/reports/"+System.currentTimeMillis()+".xlsx";
		String message="";
		String errorMessage="";
		String email=null,role=null;		
		int errorCode=0;
		List<User> users=null;
		Map<String, Object> data = new HashMap<>();
		
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String jwtToken = authorizationHeader.substring(7); // Removing "Bearer " prefix
			try {
				Base64.Decoder decoder = Base64.getUrlDecoder();
				String chunks[] = jwtToken.split("\\.");
				String payload = new String(decoder.decode(chunks[1]));
				ObjectMapper mapper = new ObjectMapper();
				Map<String, String> map = mapper.readValue(payload, Map.class);
				email = map.get("email");
				role = map.get("role");	
				if(role.equals("User")||role.equals("Super")||role.equals("Admin"))
				{
				}
				else
				{
					errorCode=400;
					errorMessage="Invalid Token Values";
				}
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();	
				log.error("Inside report/user/list"+errorMessage);
			}
		}
		
		if(errorCode==0 && (role.equals("Super")||role.equals("Admin")))
		{				
		try
		{
			if(country.equals("All") && state.equals("All") && city.equals("All"))
				users=uRepo.findByStatusAndNameOrEmailOrMobileNumberOrderByFamilyId(status, searchText, searchText,searchText,"Super");
			else if(state.equals("All") && city.equals("All"))
				users=uRepo.findByCountryAndStatusAndNameOrEmailOrMobileNumberOrderByFamilyId(country,status, searchText, searchText,searchText,"Super");
			else if(city.equals("All"))
				users=uRepo.findByCountryAndStateAndStatusAndNameOrEmailOrMobileNumberOrderByFamilyId(country,state,status, searchText, searchText,searchText,"Super");
			else
				users=uRepo.findByCountryAndStateAndCityAndStatusAndNameOrEmailOrMobileNumberOrderByFamilyId(country,state,city,status, searchText, searchText,searchText,"Super");
			
			
			ExcelService service=new ExcelService();
			service.generateExcelFile(users,fileName);
			data.put("fileName",fileName);
		}
	   catch(Exception e)
	   {
		   errorCode=500;
		   errorMessage=e.getMessage();
		   log.error("Inside report/user/list"+errorMessage);		  
	   }
	}
		
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);	
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
      
	}
	
	@GetMapping("reportPdf/user/list")
	public ResponseEntity<Object> reportPDFGetUsers(@RequestParam String searchText,@RequestParam String status,@RequestParam String country,@RequestParam String state,@RequestParam String city,@RequestHeader("Authorization") String authorizationHeader) {
		Logger log=LoggerFactory.getLogger(getClass());
		String fileName=context.getRealPath("/")+"/reports/"+System.currentTimeMillis()+".pdf";
		byte[] pdfBytes=null;
		String message="";
		String errorMessage="";
		String email=null,role=null;		
		int errorCode=0;
		List<User> users=null;
		Map<String, Object> data = new HashMap<>();
		
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String jwtToken = authorizationHeader.substring(7); // Removing "Bearer " prefix
			try {
				Base64.Decoder decoder = Base64.getUrlDecoder();
				String chunks[] = jwtToken.split("\\.");
				String payload = new String(decoder.decode(chunks[1]));
				ObjectMapper mapper = new ObjectMapper();
				Map<String, String> map = mapper.readValue(payload, Map.class);
				email = map.get("email");
				role = map.get("role");	
				if(role.equals("User")||role.equals("Super")||role.equals("Admin"))
				{
				}
				else
				{
					errorCode=400;
					errorMessage="Invalid Token Values";
				}
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();
				log.error("Inside reportPdf/user/list"+errorMessage);
			}
		}
		
		if(errorCode==0 && (role.equals("Super")||role.equals("Admin")))
		{				
		try
		{
			if(country.equals("All") && state.equals("All") && city.equals("All"))
				users=uRepo.findByStatusAndNameOrEmailOrMobileNumberOrderByFamilyId(status, searchText, searchText,searchText,"Super");
			else if(state.equals("All") && city.equals("All"))
				users=uRepo.findByCountryAndStatusAndNameOrEmailOrMobileNumberOrderByFamilyId(country,status, searchText, searchText,searchText,"Super");
			else if(city.equals("All"))
				users=uRepo.findByCountryAndStateAndStatusAndNameOrEmailOrMobileNumberOrderByFamilyId(country,state,status, searchText, searchText,searchText,"Super");
			else
				users=uRepo.findByCountryAndStateAndCityAndStatusAndNameOrEmailOrMobileNumberOrderByFamilyId(country,state,city,status, searchText, searchText,searchText,"Super");
			
			PDFService pdfGeneratorService=new PDFService();
			pdfBytes = pdfGeneratorService.generatePdfFile(users);
            convertByteArrayToFile(pdfBytes, fileName);
            data.put("fileName",fileName);
		}
	   catch(Exception e)
	   {
		   errorCode=500;
		   errorMessage=e.getMessage();
		   log.error("Inside reportPdf/user/list"+errorMessage);
		  
	   }
	}
	
		
		data.put("message",message);
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);	
		
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
      
	}
	
	public static void convertByteArrayToFile(byte[] bytes, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(bytes);
        }
        
    }	
	
	@GetMapping("/getUser/{id}")
	public ResponseEntity<Object> getUser(@RequestHeader("Authorization") String authorizationHeader,@PathVariable("id") int id) {
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;
		String email = "";
		String role="";
		int sid=0;
		int fId=0;
		Map<String, Object> data = new HashMap<>();		
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String jwtToken = authorizationHeader.substring(7); // Removing "Bearer " prefix
			try {
				Base64.Decoder decoder = Base64.getUrlDecoder();
				String chunks[] = jwtToken.split("\\.");
				String payload = new String(decoder.decode(chunks[1]));
				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> map = mapper.readValue(payload, Map.class);
				email = map.get("email").toString();
				role=map.get("role").toString();
				sid=Integer.valueOf(map.get("id").toString());
				fId=Integer.valueOf(map.get("familyId").toString());
				errorCode=0;
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();				
			}
		}
		try
		{
		User user = null;
		user=uRepo.findById(id);
		if(errorCode==0 && (role.equals("Super") || role.equals("Admin") ||role.equals("User") || sid==id || user.getFamilyId()==fId ))
		{				
			data.put("user", user);
			message="success";			
		}
		else
		{
			errorCode=400;
			errorMessage="Invalid Token";
		}
		}
		catch(Exception e)
		{
			errorCode=500;
			errorMessage=e.getMessage();
			log.error("Inside /getUser/{id}"+errorMessage);
		}		
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);  	
	}  	
	@GetMapping("/getApprovedUsersByFamilyId")
	public ResponseEntity<Object> getAproovedUsersByFamilyId(@RequestHeader("Authorization") String authorizationHeader) {
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;
		String email = "";
		String role="";
		int sid=0;
		int familyId=0;
		Map<String, Object> data = new HashMap<>();		
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String jwtToken = authorizationHeader.substring(7); // Removing "Bearer " prefix
			try {
				Base64.Decoder decoder = Base64.getUrlDecoder();
				String chunks[] = jwtToken.split("\\.");
				String payload = new String(decoder.decode(chunks[1]));
				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> map = mapper.readValue(payload, Map.class);
				email = map.get("email").toString();
				role=map.get("role").toString();
				sid=Integer.valueOf(map.get("id").toString());
				familyId=Integer.valueOf(map.get("familyId").toString());
				errorCode=0;
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();
				log.error("Inside getApprovedUsersByFamilyId"+errorMessage);
			}
		}
		try
		{
		List<User> users = null;
		if(errorCode==0 && (role.equals("Super") || role.equals("Admin") || role.equals("User")||role.equals("Member")))
		{		
			users=uRepo.findAllByFamilyIdAndStatusAndRoleNot(familyId,"Approved","Super");
			data.put("users", users);
			message="success";			
		}
		else
		{
			errorCode=400;
			errorMessage="Invalid Token";
		}
		}
		catch(Exception e)
		{
			errorCode=500;
			errorMessage=e.getMessage();
			log.error("Inside getApprovedUsersByFamilyId"+errorMessage);
		}		
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);  	
	}  	
	
	@GetMapping("/getAllApprovedUsersByFamilyId")
	public ResponseEntity<Object> getAllAproovedUsersByFamilyId(@RequestHeader("Authorization") String authorizationHeader) {
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;
		String email = "";
		String role="";
		int sid=0;
		int familyId=0;
		Map<String, Object> data = new HashMap<>();		
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String jwtToken = authorizationHeader.substring(7); // Removing "Bearer " prefix
			try {
				Base64.Decoder decoder = Base64.getUrlDecoder();
				String chunks[] = jwtToken.split("\\.");
				String payload = new String(decoder.decode(chunks[1]));
				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> map = mapper.readValue(payload, Map.class);
				email = map.get("email").toString();
				role=map.get("role").toString();
				sid=Integer.valueOf(map.get("id").toString());
				familyId=Integer.valueOf(map.get("familyId").toString());
				errorCode=0;
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();
				log.error("Inside getAllApprovedUsersByFamilyId"+errorMessage);
			}
		}
		try
		{
		List<User> users = null;
		if(errorCode==0 && (role.equals("Super") || role.equals("Admin") || role.equals("User")||role.equals("Member")))
		{		
			users=uRepo.findAllByFamilyIdAndStatusAndRoleNot(familyId,"Approved","Super");
			data.put("users", users);
			message="success";			
		}
		else
		{
			errorCode=400;
			errorMessage="Invalid Token";
		}
		}
		catch(Exception e)
		{
			errorCode=500;
			errorMessage=e.getMessage();
			log.error("Inside getAllApprovedUsersByFamilyId"+errorMessage);
		}		
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);  	
	}  	
	
	@GetMapping("/getCardsDetails")
	public ResponseEntity<Object> getCardsDetails(@RequestHeader("Authorization") String authorizationHeader) {
	
	    Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;
		String email = "";
		String role="";
		int sid=0;
		int familyId=0;
		Map<String, Object> data = new HashMap<>();		
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String jwtToken = authorizationHeader.substring(7); // Removing "Bearer " prefix
			try {
				Base64.Decoder decoder = Base64.getUrlDecoder();
				String chunks[] = jwtToken.split("\\.");
				String payload = new String(decoder.decode(chunks[1]));
				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> map = mapper.readValue(payload, Map.class);
				email = map.get("email").toString();
				role=map.get("role").toString();
				sid=Integer.valueOf(map.get("id").toString());
				familyId=Integer.valueOf(map.get("familyId").toString());
				errorCode=0;
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();	
				log.error("Inside getCardsDetails"+errorMessage);
			}
		}
		try
		{
		List<User> users = null;
		if(errorCode==0 && (role.equals("Super") || role.equals("Admin")))
		{		
			long familyCount=uRepo.countDistinctFamilyId();
			long discipleCount=uRepo.countDiscipleUsers();
			long nonDiscipleCount=uRepo.countNonDiscipleUsers();
			long totalEvents=eRepo.countAllEvent();
			long countActiveEvents=eRepo.countActiveEvents();
			long countPendingUsers=uRepo.countPendingUsers();
			long countRejectedUsers=uRepo.countRejectedUsers();
			long countApprovedUsers=uRepo.countApprovedUsers();
			
			data.put("familyCount", familyCount);
			data.put("discipleCount", discipleCount);
			data.put("nonDiscipleCount", nonDiscipleCount);
			data.put("totalEvents", totalEvents);
			data.put("countActiveEvents", countActiveEvents);
			data.put("countPendingUsers", countPendingUsers);
			data.put("countRejectedUsers", countRejectedUsers);
			data.put("countApprovedUsers", countApprovedUsers);
			
			
			message="success";			
		}
		else
		{
			errorCode=400;
			errorMessage="Invalid Token";
		}
		}
		catch(Exception e)
		{
			errorCode=500;
			errorMessage=e.getMessage();
			log.error("Inside getCardsDetails"+errorMessage);
		}		
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);  	
	}  	
	
}
