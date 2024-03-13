package com.eschool.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import com.eschool.beans.AttendaceDetails;
import com.eschool.beans.Attendance;
import com.eschool.beans.Event;
import com.eschool.beans.EventRegistration;
import com.eschool.beans.SMSHandler;
import com.eschool.beans.User;
import com.eschool.beans.UserForAttendance;
import com.eschool.repo.AttendanceRepository;
import com.eschool.repo.EventRegistrationRepository;
import com.eschool.repo.EventRepository;
import com.eschool.repo.UserRepository;
import com.eschool.service.EmailService;
import com.eschool.service.ExcelService;
import com.eschool.service.PDFService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletContext;
@RestController
public class AttendaceController {
	@Autowired
	AttendanceRepository aRepo;
	@Autowired
	EventRegistrationRepository erRepo;
	@Autowired
	UserRepository uRepo;
	@Autowired
	EventRepository eRepo;
	@Autowired
	ServletContext context;
	@Autowired
	EmailService emailService;
	// To get details of attendance and hall nos 
	@GetMapping("event/registrations/{eventId}")
	public ResponseEntity<Object> fetchRegisteredUserByEvent(@RequestParam int page, @RequestParam int rowsPerPage,@PathVariable int eventId,@RequestHeader("Authorization") String authorizationHeader) {
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;
		int sid=0;
		String email=null,role=null;
		int familyId=0;
		int start=(page-1)*rowsPerPage;
		int end=start+rowsPerPage;
		Map<String, Object> data = new HashMap<>();
		List<UserForAttendance> myUsers=new ArrayList<UserForAttendance>();
		List<UserForAttendance> users=new ArrayList<UserForAttendance>();
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
				log.error("Inside event/registrations/{eventId}"+errorMessage);
			}
		}
		try
		{
		
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
		}
		catch(Exception ex)
		{
			errorMessage=ex.getMessage();
			errorCode=500;
			myUsers=Collections.emptyList();
			log.error("Inside event/registrations/{eventId}"+errorMessage);
		}
		data.put("users", myUsers);
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
	
	@GetMapping("reportPdf/event/registrations/{eventId}")
	public ResponseEntity<Object> reportPdfFetchRegisteredUserByEvent(@PathVariable int eventId,@RequestHeader("Authorization") String authorizationHeader) {
		Logger log=LoggerFactory.getLogger(getClass());
		String fileName=context.getRealPath("/")+"/reports/"+System.currentTimeMillis()+".pdf";
		byte[] pdfBytes=null;
		String message="";
		String errorMessage="";
		int errorCode=0;
		int sid=0;
		String email=null,role=null;
		int familyId=0;
		Map<String, Object> data = new HashMap<>();
		List<UserForAttendance> myUsers=new ArrayList<UserForAttendance>();
		List<UserForAttendance> users=new ArrayList<UserForAttendance>();
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
				log.error("Inside reportPdf/event/registrations/{eventId}"+errorMessage);
			}
		}
		try
		{
		
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
		
		PDFService pdfGeneratorService=new PDFService();
		pdfBytes = pdfGeneratorService.generatePdfFileForAttendance(users);
        convertByteArrayToFile(pdfBytes, fileName);
        data.put("fileName",fileName);
		}
		}
		catch(Exception ex)
		{
			errorMessage=ex.getMessage();
			errorCode=500;
			log.error("Inside reportPdf/event/registrations/{eventId}"+errorMessage);
			
		}	
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
	
	@GetMapping("reportExcel/event/registrations/{eventId}")
	public ResponseEntity<Object> reportExcelFetchRegisteredUserByEvent(@PathVariable int eventId,@RequestHeader("Authorization") String authorizationHeader) {
		Logger log=LoggerFactory.getLogger(getClass());
		String fileName=context.getRealPath("/")+"/reports/"+System.currentTimeMillis()+".xlsx";
		String message="";
		String errorMessage="";
		int errorCode=0;
		int sid=0;
		String email=null,role=null;
		int familyId=0;
		Map<String, Object> data = new HashMap<>();
		List<UserForAttendance> myUsers=new ArrayList<UserForAttendance>();
		List<UserForAttendance> users=new ArrayList<UserForAttendance>();
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
				log.error("Inside reportExcel/event/registrations/{eventId}"+errorMessage);
			}
		}
		try
		{
		
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
		
		ExcelService service=new ExcelService();
		service.generateExcelFileForAttendanceAndHallNo(users,fileName);
		data.put("fileName",fileName);
		}
		}
		catch(Exception ex)
		{
			errorMessage=ex.getMessage();
			errorCode=500;
			log.error("Inside reportExcel/event/registrations/{eventId}"+errorMessage);
		}	
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
		Logger log=LoggerFactory.getLogger(getClass());
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

         
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Inside attendance Controller send room booking message"+e.getMessage());
        }
	}
	
	@PostMapping("attendance/save")
	public ResponseEntity<Object> saveAttendance(@RequestBody AttendaceDetails attendaceDetails,@RequestHeader("Authorization") String authorizationHeader) {
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
				
			} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();
				log.error("Inside attendance/save"+e.getMessage());
			}
		}
		if(errorCode==0 && (role.equals("Super")||role.equals("Admin")))
		{
			try
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
			catch(Exception e)
			{
				errorCode=500;
				errorMessage=e.getMessage();				
				log.error("Inside attendance/save"+e.getMessage());
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
				
			} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();
				log.error("Inside bookingStatus/send"+e.getMessage());
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
		Event event=eRepo.findByEventId(eventId);
		String mobileNo=user.getMobileNumber();
		if(user.getMobileNumber().trim().length()==0)
		{
			int fId=user.getFamilyId();
			User headUser=uRepo.findByFamilyIdAndRole(fId,"User");
			mobileNo=headUser.getMobileNumber();			
		}		
		String msg="Jai Guru. Your room/hall number for "+event.getEventName()+" is "+hallNo+". Please visit reception desk once you reach Ashram.";
		new SMSHandler().sendCustom(mobileNo, msg);
		emailService.sendEmail(user.getEmail(),"Regarding Booking", msg);
		message="User is notified about hall number";
		}
		catch(Exception ex)
		{
			errorCode=500;
			errorMessage=ex.getMessage();
			log.error("Inside bookingStatus/send"+ex.getMessage());
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
				
			} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();	
				log.error("Inside attendance/save/one"+errorMessage);
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
			log.error("Inside attendance/save/one"+errorMessage);
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

