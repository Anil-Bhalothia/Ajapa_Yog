package com.eschool.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.eschool.beans.Attendance;
import com.eschool.beans.Event;
import com.eschool.beans.EventRegistration;
import com.eschool.beans.GroupSMS;
import com.eschool.beans.User;
import com.eschool.repo.AttendanceRepository;
import com.eschool.repo.EventRegistrationRepository;
import com.eschool.repo.GroupSMSRepo;
import com.eschool.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
@RestController
public class SMSController {
	@Autowired
	EventRegistrationRepository erRepo;
	@Autowired
	UserRepository uRepo;
	@Autowired
	AttendanceRepository aRepo;
	@Autowired
	GroupSMSRepo gRepo;
	
	String message;
	String errorMessage;
	int errorCode;
	int count;
	void sendCustomSMS(String pno,String message)
	{
		try {
            String user = "AjapaYog";
            String pass = "123456";
            String sender = "ASTOVD";
            String phone = pno;
            String text = message;
            String priority = "ndnd";
            String stype = "normal";
            String encodedText = URLEncoder.encode(text, "UTF-8");
            String urlString = "https://trans.smsfresh.co/api/sendmsg.php?" +
                    "user=" + user +
                    "&pass=" + pass +
                    "&sender=" + sender +
                    "&phone=" + phone +
                    "&text=" + encodedText +
                    "&priority=" + priority +
                    "&stype=" + stype;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println("Response: " + response.toString());
            connection.disconnect();
            count++;
        } catch (Exception e) {
            errorCode=500;
            errorMessage=e.getMessage();
        }
	}
	@GetMapping("groupSMS/list")
    public ResponseEntity<Object> listGroupSMS(@RequestHeader("Authorization") String authorizationHeader) 
		{			
			Logger log=LoggerFactory.getLogger(getClass());
			message="";
			errorMessage="";
			errorCode=0;
			count=0;
			String email=null,role=null;
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
					if(!(role.equals("Super")||role.equals("Admin")))
					{
						errorCode=400;
						errorMessage="Invalid Token Values";
					}
					
					} 
				catch (Exception e) {
					errorCode=500;
					errorMessage=e.getMessage();
					log.error("Inside groupSMS/list"+errorMessage);
					}
			}
			Map<String, Object> data = new HashMap<>();	
			if(errorCode==0)
			{
				
			try {
				data.put("messageTemplates",gRepo.findAll());
			}
			catch(Exception ex)
			{
				errorCode=500;
				errorMessage=ex.getMessage();
				log.error("Inside groupSMS/list"+errorMessage);				
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
	@PostMapping("sendCusmtomSMS/{eventId}/{check}/{text}")
    public ResponseEntity<Object> sendCusmtomSMS(@PathVariable int eventId,@PathVariable int check,@PathVariable String text,@RequestHeader("Authorization") String authorizationHeader) {
		Logger log=LoggerFactory.getLogger(getClass());
		message="";
		errorMessage="";
		errorCode=0;
		count=0;
		String email=null,role=null;
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
				if(!(role.equals("Super")||role.equals("Admin")))
				{
					errorCode=400;
					errorMessage="Invalid Token Values";
				}
				
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();
				log.error("Inside sendCusmtomSMS/{eventId}/{check}/{text}"+errorMessage);
				}
		}
		if(errorCode==0)
		{
     List<User> users=new ArrayList<>();
     if(check==1)
     {
    	List<EventRegistration> eventRegistrations=erRepo.findAllByEventIdOrderByFamilyId(eventId);
    	for(EventRegistration ev:eventRegistrations)
    	{
    		User user=uRepo.findById(ev.getUserId());
    		users.add(user);
    	}
     }
     else if(check==2)
     {
    	List<EventRegistration> eventRegistrations=erRepo.findAllByEventIdOrderByFamilyId(eventId);
    	for(EventRegistration ev:eventRegistrations)
    	{
    		User user=uRepo.findById(ev.getUserId());
    		if(user.getRole().equals("User"))
    		users.add(user);
    	}    	
     }   
     else if(check==3)
     {
    	List<EventRegistration> eventRegistrations=erRepo.findAllByEventIdOrderByFamilyId(eventId);
    	for(EventRegistration ev:eventRegistrations)
    	{
    		User user=uRepo.findById(ev.getUserId());
    		Attendance attendance=aRepo.findByUserIdAndEventId(user.getId(), eventId);
    		if(attendance!=null && attendance.getPresent()==true)
     		users.add(user);
    	}     	
     }   
     else if(check==4)
     {
    	List<EventRegistration> eventRegistrations=erRepo.findAllByEventIdOrderByFamilyId(eventId);
     	for(EventRegistration ev:eventRegistrations)
     	{
     		User user=uRepo.findById(ev.getUserId());
     		Attendance attendance=aRepo.findByUserIdAndEventId(user.getId(), eventId);
      		if(attendance!=null && attendance.getPresent()==true && user.getRole().equals("User"))
      		users.add(user);
     	}     	
     } 
     else if(check==5)
     {
    	List<EventRegistration> eventRegistrations=erRepo.findAllByEventIdOrderByFamilyId(eventId);
     	for(EventRegistration ev:eventRegistrations)
     	{
     		User user=uRepo.findById(ev.getUserId());
     		if(ev.getAttendingShivir())
     		users.add(user);
     	}   	 
    	
     } 
     else if(check==6)
     {
    	users=uRepo.findAllByStatus("Approved");
     } 
     else if(check==7)
     {
    List<User> allUsers=uRepo.findAllByStatus("Approved");
    for(User user:allUsers)
    {
    	if(user.getRole().equals("User"))
    		users.add(user);
    }    	
    
     }     
     for(User user:users)
     {
    	 sendCustomSMS(user.getMobileNumber(),text);
     }         
	}		
 		Map<String, Object> data = new HashMap<>();	
		message="Delivered "+count+" messages successfully";
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
	   	return new ResponseEntity<>(data, HttpStatus.OK);
	}
	
}
