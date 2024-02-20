package com.eschool.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import com.eschool.beans.AttendaceDetails;
import com.eschool.beans.Attendance;
import com.eschool.beans.Event;
import com.eschool.beans.EventRegistration;
import com.eschool.beans.User;
import com.eschool.beans.UserForAttendance;
import com.eschool.repo.AttendanceRepository;
import com.eschool.repo.EventRegistrationRepository;
import com.eschool.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
@RestController
public class AttendaceController {
	@Autowired
	AttendanceRepository aRepo;
	@Autowired
	EventRegistrationRepository erRepo;
	@Autowired
	UserRepository uRepo;
	@GetMapping("event/registrations/{eventId}")
	public ResponseEntity<Object> fetchRegisteredUserByEvent(@PathVariable int eventId,@RequestHeader("Authorization") String authorizationHeader) {
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
				
			} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();	
			}
		}
		List<UserForAttendance> users=new ArrayList<UserForAttendance>();
		List<EventRegistration> eventRegistrations=erRepo.findAllByEventIdOrderByFamilyId(eventId);
		if(errorCode==0 && (role.equals("Super")||role.equals("Admin")))
		{
		for(EventRegistration eventRegistration:eventRegistrations)
		{
		User user=uRepo.findById(eventRegistration.getUserId());
		Attendance attendance=aRepo.findByUserIdAndEventId(eventRegistration.getUserId(),eventId);
		UserForAttendance userForAttendance;
		if(attendance!=null)		
			userForAttendance=new UserForAttendance(user,attendance.getPresent(),attendance.getHallNo(),eventRegistration.getSpecificRequirements());
		else
			userForAttendance=new UserForAttendance(user, false,"0",eventRegistration.getSpecificRequirements());	
		users.add(userForAttendance);
		}
		}
		data.put("users", users);
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
 	}
	
	void sendRoomBookingSMS(String pno,String message)
	{
		try {
            String user = "AjapaYog";
            String pass = "123456";
            String sender = "ASTOVD";
            String phone = pno;
            String text = message+" is your one time password for login Ajapa Yog Sansthan - Team Astrovedha";
            String priority = "ndnd";
            String stype = "normal";

            // URL encode the text parameter
            String encodedText = URLEncoder.encode(text, "UTF-8");

            // Construct the URL
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

            // Get the response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Print the response
            System.out.println("Response: " + response.toString());

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@PostMapping("attendance/save")
	public ResponseEntity<Object> saveAttendance(@RequestBody AttendaceDetails attendaceDetails,@RequestHeader("Authorization") String authorizationHeader) {
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
				
			} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();	
			}
		}
		if(errorCode==0 && (role.equals("Super")||role.equals("Admin")))
		{		
		int n=attendaceDetails.getEvents().size();
	    ArrayList<Integer> events=attendaceDetails.getEvents();
	    ArrayList<Integer> users=attendaceDetails.getUsers();
	    ArrayList<Boolean> isPresentList=attendaceDetails.getIsPresent();
	    ArrayList<String> hallNo=attendaceDetails.getHallNo();
	    for(int i=0;i<n;i++)
	    {
	    	Attendance attendance=aRepo.findByUserIdAndEventId(users.get(i),events.get(i));
	    	if(attendance==null)
	    	{	   
	    		Attendance at=new Attendance(events.get(i),users.get(i),hallNo.get(i),isPresentList.get(i));
	    		aRepo.save(at);	    		
	    	}
	    	else
	    	{
	    		attendance.setPresent(isPresentList.get(i));
	    		attendance.setHallNo(hallNo.get(i));
	    		aRepo.save(attendance);
	    	}
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

	@PostMapping("bookingStatus/send")
	public ResponseEntity<Object> sendRoomBookingStatus(@RequestParam("id") int id,@RequestParam("eventId") int eventId,@RequestParam("hallNo") String hallNo,@RequestParam("present") boolean present,@RequestHeader("Authorization") String authorizationHeader) 
	{	   
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
				
			} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();	
			}
		}
		try
		{
		if(errorCode==0 && (role.equals("Super")||role.equals("Admin")))
		{		
		
			int userId=id;
		    Attendance attendance=aRepo.findByUserIdAndEventId(userId,eventId);
	    	if(attendance==null)
	    	{	   
	    		Attendance at=new Attendance(eventId,userId,hallNo,present);
	    		aRepo.save(at);	    		
	    	}
	    	else
	    	{
	    		attendance.setPresent(present);
	    		attendance.setHallNo(hallNo);
	    		aRepo.save(attendance);
	    	}
	      	
	    		
	    }
		User user=uRepo.findById(id);
		String mobileNo=user.getMobileNumber();
		if(user.getMobileNumber().trim().length()==0)
		{
			int fId=user.getFamilyId();
			User headUser=uRepo.findByFamilyIdAndRole(fId,"User");
			mobileNo=headUser.getMobileNumber();			
		}		
		System.out.println(user.getName());
		sendRoomBookingSMS(mobileNo, user.getName()+":"+hallNo);
		message="User is notified about hall number";
		}
		catch(Exception ex)
		{
			errorCode=500;
			errorMessage=ex.getMessage();
		}
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
	}
	
	
	@PostMapping("attendance/save/one")
	public ResponseEntity<Object> saveOneAttendance(@RequestParam("id") int id,@RequestParam("eventId") int eventId,@RequestParam("hallNo") String hallNo,@RequestParam("present") boolean present,@RequestHeader("Authorization") String authorizationHeader) 
	{	   
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
				
			} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();	
			}
		}
		try
		{
		if(errorCode==0 && (role.equals("Super")||role.equals("Admin")))
		{		
			int userId=id;
		    Attendance attendance=aRepo.findByUserIdAndEventId(userId,eventId);
	    	if(attendance==null)
	    	{	   
	    		Attendance at=new Attendance(eventId,userId,hallNo,present);
	    		aRepo.save(at);	    		
	    	}
	    	else
	    	{
	    		attendance.setPresent(present);
	    		attendance.setHallNo(hallNo);
	    		aRepo.save(attendance);
	    	}    		
	    }
		//sendRoomBookingSMS(userForAttendance.getUser().getMobileNumber(), userForAttendance.getHallNo());
		message="Attendance Saved";
		}
		catch(Exception ex)
		{
			errorCode=500;
			errorMessage=ex.getMessage();
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
