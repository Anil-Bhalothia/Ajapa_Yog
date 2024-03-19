package com.eschool.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.eschool.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.eschool.beans.DataFirstReport;
import com.eschool.beans.DataSecondReport;
import com.eschool.repo.EventRepository;

@RestController
public class ReportController {
	@Autowired
	UserRepository urepo;
	@Autowired
	EventRepository erepo;	
	@GetMapping("report1/arrival/event/registrations/{eventId}/{attendingShivir}")
	public ResponseEntity<Object> getArrivalReportOfEventRegistration_1(@PathVariable int eventId,@PathVariable boolean attendingShivir,@RequestHeader("Authorization") String authorizationHeader) {	
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;		
		Map<String, Object> data = new HashMap<>();	
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
				if(role.equals("Super")||role.equals("Admin"))
				{					
				}
				else
				{
					errorCode=400;
					errorMessage="Invalid Token Values";
					System.out.println("Hello3");
				}
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();	
				log.error("Inside report1/arrival/event/registrations/{eventId}/{attendingShivir}"+errorMessage);
			}
		}
		if(errorCode==0)
		{		
	List<DataFirstReport> reportData=new ArrayList<>();
	String query_1="select distinct arrival_date from event_registration where event_id=? and attending_shivir=?";
	Connection cn=null;
	try
	{
		Class.forName("com.mysql.cj.jdbc.Driver");
		cn=DriverManager.getConnection("jdbc:mysql://localhost/ajapa_yog?user=root&password=root");
		PreparedStatement ps1=cn.prepareStatement(query_1);
		ps1.setInt(1, eventId);
		ps1.setBoolean(2, attendingShivir);
		ResultSet rs1=ps1.executeQuery();
		while(rs1.next())
		{
			System.out.println("Hello1");
			String arrival_date=rs1.getString(1);			
			String query_2="select count(distinct er.family_id),COUNT(IF(TIMESTAMPDIFF(year,dob,now())>45, 1, NULL)),COUNT(IF(TIMESTAMPDIFF(year,dob,now())<10, 1, NULL)),COUNT(IF(gender='Male', 1, NULL)),COUNT(IF(gender='Female', 1, NULL)) from event_registration er,user u  where u.id=er.user_id and event_id=? and attending_shivir=? and arrival_date=?";
			PreparedStatement ps2=cn.prepareStatement(query_2);
			ps2.setInt(1, eventId);			
			ps2.setBoolean(2, attendingShivir);
			ps2.setString(3, arrival_date);
			ResultSet rs2=ps2.executeQuery();
			rs2.next();
			int familyCount=rs2.getInt(1);			
			int totalSeniors=rs2.getInt(2);			
			int totalKids=rs2.getInt(3);
			int totalMale=rs2.getInt(4);			
			int totalFemale=rs2.getInt(5);	
			DataFirstReport record=new DataFirstReport(arrival_date, familyCount, totalMale, totalFemale, totalKids, totalSeniors);			
			reportData.add(record);
		}
	}
	catch(Exception e)
	{
		errorCode=500;
		errorMessage=e.getMessage();
		log.error("Inside report1/arrival/event/registrations/{eventId}/{attendingShivir}"+errorMessage);
	}
	finally {
		try {
			cn.close();
		} catch (SQLException e) {
			errorCode=500;
			errorMessage=e.getMessage();
			log.error("Inside report1/arrival/event/registrations/{eventId}/{attendingShivir}"+errorMessage);
		}
	}
	data.put("data", reportData);
	}
	data.put("message",message);
	data.put("errorMessage",errorMessage);	
	data.put("errorCode",errorCode);		
	if(errorCode==0)
		return new ResponseEntity<>(data, HttpStatus.OK);
	else 
		return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);	
	
	}
	@GetMapping("report1/arrival/event/registrations/{eventId}")
	public ResponseEntity<Object> getArrivalReportOfEventRegistration_1(@PathVariable int eventId,@RequestHeader("Authorization") String authorizationHeader) {	
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;		
		Map<String, Object> data = new HashMap<>();	
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
				if(role.equals("Super")||role.equals("Admin"))
				{
					
				}
				else
				{
					errorCode=400;
					errorMessage="Invalid Token Values";
					System.out.println("Hello3");
				}
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();	
				log.error("Inside report1/arrival/event/registrations/{eventId}"+errorMessage);
			}
		}
		if(errorCode==0)
		{		
	List<DataFirstReport> reportData=new ArrayList<>();
	String query_1="select distinct arrival_date from event_registration where event_id=?";
	Connection cn=null;
	try
	{
		Class.forName("com.mysql.cj.jdbc.Driver");
		cn=DriverManager.getConnection("jdbc:mysql://localhost/ajapa_yog?user=root&password=root");
		PreparedStatement ps1=cn.prepareStatement(query_1);
		ps1.setInt(1, eventId);		
		ResultSet rs1=ps1.executeQuery();
		while(rs1.next())
		{
			System.out.println("Hello1");
			String arrival_date=rs1.getString(1);			
			String query_2="select count(distinct er.family_id),COUNT(IF(TIMESTAMPDIFF(year,dob,now())>45, 1, NULL)),COUNT(IF(TIMESTAMPDIFF(year,dob,now())<10, 1, NULL)),COUNT(IF(gender='Male', 1, NULL)),COUNT(IF(gender='Female', 1, NULL)) from event_registration er,user u  where u.id=er.user_id and event_id=? and arrival_date=?";
			PreparedStatement ps2=cn.prepareStatement(query_2);
			ps2.setInt(1, eventId);			
			ps2.setString(2, arrival_date);
			ResultSet rs2=ps2.executeQuery();
			rs2.next();
			int familyCount=rs2.getInt(1);			
			int totalSeniors=rs2.getInt(2);			
			int totalKids=rs2.getInt(3);
			int totalMale=rs2.getInt(4);			
			int totalFemale=rs2.getInt(5);	
			DataFirstReport record=new DataFirstReport(arrival_date, familyCount, totalMale, totalFemale, totalKids, totalSeniors);			
			reportData.add(record);
		}
	}
	catch(Exception e)
	{
		errorCode=500;
		errorMessage=e.getMessage();	
		log.error("Inside report1/arrival/event/registrations/{eventId}"+errorMessage);

	}
	finally {
		try {
			cn.close();
		} catch (SQLException e) {
			errorCode=500;
			errorMessage=e.getMessage();	
			log.error("Inside report1/arrival/event/registrations/{eventId}"+errorMessage);
		}
	}
	data.put("data", reportData);
	}
	data.put("message",message);
	data.put("errorMessage",errorMessage);	
	data.put("errorCode",errorCode);		
	if(errorCode==0)
		return new ResponseEntity<>(data, HttpStatus.OK);
	else 
		return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);	
	
	}
//-----------Report 1 According to departure------------//
	@GetMapping("report1/departure/event/registrations/{eventId}/{attendingShivir}")
	public ResponseEntity<Object> getDepartureReportOfEventRegistration_1(@PathVariable int eventId,@PathVariable boolean attendingShivir,@RequestHeader("Authorization") String authorizationHeader) {	
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;		
		Map<String, Object> data = new HashMap<>();	
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
				if(role.equals("Super")||role.equals("Admin"))
				{
					
				}
				else
				{
					errorCode=400;
					errorMessage="Invalid Token Values";
					System.out.println("Hello3");
				}
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();
				log.error("Inside report1/departure/event/registrations/{eventId}/{attendingShivir}"+errorMessage);
				
			}
		}
		if(errorCode==0)
		{		
	List<DataFirstReport> reportData=new ArrayList<>();
	String query_1="select distinct departure_date from event_registration where event_id=? and attending_shivir=?";
	Connection cn=null;
	try
	{
		Class.forName("com.mysql.cj.jdbc.Driver");
		cn=DriverManager.getConnection("jdbc:mysql://localhost/ajapa_yog?user=root&password=root");
		PreparedStatement ps1=cn.prepareStatement(query_1);
		ps1.setInt(1, eventId);
		ps1.setBoolean(2, attendingShivir);
		ResultSet rs1=ps1.executeQuery();
		while(rs1.next())
		{
			System.out.println("Hello1");
			String departure_date=rs1.getString(1);			
			String query_2="select count(distinct er.family_id),COUNT(IF(TIMESTAMPDIFF(year,dob,now())>45, 1, NULL)),COUNT(IF(TIMESTAMPDIFF(year,dob,now())<10, 1, NULL)),COUNT(IF(gender='Male', 1, NULL)),COUNT(IF(gender='Female', 1, NULL)) from event_registration er,user u  where u.id=er.user_id and event_id=? and attending_shivir=? and departure_date=?";
			PreparedStatement ps2=cn.prepareStatement(query_2);
			ps2.setInt(1, eventId);			
			ps2.setBoolean(2, attendingShivir);
			ps2.setString(3, departure_date);
			ResultSet rs2=ps2.executeQuery();
			rs2.next();
			int familyCount=rs2.getInt(1);			
			int totalSeniors=rs2.getInt(2);			
			int totalKids=rs2.getInt(3);
			int totalMale=rs2.getInt(4);			
			int totalFemale=rs2.getInt(5);	
			DataFirstReport record=new DataFirstReport(departure_date, familyCount, totalMale, totalFemale, totalKids, totalSeniors);			
			reportData.add(record);
		}
	}
	catch(Exception e)
	{
		System.out.println(e.getMessage());
	}
	finally {
		try {
			cn.close();
		} catch (SQLException e) {
			errorCode=500;
			errorMessage=e.getMessage();
			log.error("Inside report1/departure/event/registrations/{eventId}/{attendingShivir}"+errorMessage);
		}
	}
	data.put("data", reportData);
	}
	data.put("message",message);
	data.put("errorMessage",errorMessage);	
	data.put("errorCode",errorCode);		
	if(errorCode==0)
		return new ResponseEntity<>(data, HttpStatus.OK);
	else 
		return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);	
	
	}
	@GetMapping("report1/departure/event/registrations/{eventId}")
	public ResponseEntity<Object> getDepartureReportOfEventRegistration_1(@PathVariable int eventId,@RequestHeader("Authorization") String authorizationHeader) {	
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;		
		Map<String, Object> data = new HashMap<>();	
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
				if(role.equals("Super")||role.equals("Admin"))
				{
					
				}
				else
				{
					errorCode=400;
					errorMessage="Invalid Token Values";
					System.out.println("Hello3");
				}
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();	
				log.error("Inside report1/departure/event/registrations/{eventId}"+errorMessage);
				
			}
		}
		if(errorCode==0)
		{		
	List<DataFirstReport> reportData=new ArrayList<>();
	String query_1="select distinct departure_date from event_registration where event_id=?";
	Connection cn=null;
	try
	{
		Class.forName("com.mysql.cj.jdbc.Driver");
		cn=DriverManager.getConnection("jdbc:mysql://localhost/ajapa_yog?user=root&password=root");
		PreparedStatement ps1=cn.prepareStatement(query_1);
		ps1.setInt(1, eventId);		
		ResultSet rs1=ps1.executeQuery();
		while(rs1.next())
		{
			System.out.println("Hello1");
			String departure_date=rs1.getString(1);			
			String query_2="select count(distinct er.family_id),COUNT(IF(TIMESTAMPDIFF(year,dob,now())>45, 1, NULL)),COUNT(IF(TIMESTAMPDIFF(year,dob,now())<10, 1, NULL)),COUNT(IF(gender='Male', 1, NULL)),COUNT(IF(gender='Female', 1, NULL)) from event_registration er,user u  where u.id=er.user_id and event_id=? and departure_date=?";
			PreparedStatement ps2=cn.prepareStatement(query_2);
			ps2.setInt(1, eventId);			
			ps2.setString(2, departure_date);
			ResultSet rs2=ps2.executeQuery();
			rs2.next();
			int familyCount=rs2.getInt(1);			
			int totalSeniors=rs2.getInt(2);			
			int totalKids=rs2.getInt(3);
			int totalMale=rs2.getInt(4);			
			int totalFemale=rs2.getInt(5);	
			DataFirstReport record=new DataFirstReport(departure_date, familyCount, totalMale, totalFemale, totalKids, totalSeniors);			
			reportData.add(record);
		}
	}
	catch(Exception e)
	{
		errorCode=500;
		errorMessage=e.getMessage();	
		log.error("Inside report1/departure/event/registrations/{eventId}"+errorMessage);
		
	}
	finally {
		try {
			cn.close();
		} catch (SQLException e) {
			errorCode=500;
			errorMessage=e.getMessage();	
			log.error("Inside report1/departure/event/registrations/{eventId}"+errorMessage);
			
		}
	}
	data.put("data", reportData);
	}
	data.put("message",message);
	data.put("errorMessage",errorMessage);	
	data.put("errorCode",errorCode);		
	if(errorCode==0)
		return new ResponseEntity<>(data, HttpStatus.OK);
	else 
		return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);	
	
	}

	boolean containsDigitsOtherThanZero(String text)
	{
		boolean isPresent=false;
		String digits="123456789";
		for(int i=0;i<digits.length();i++)
		{
			if(text.contains(""+digits.charAt(i)))
				return true;
		}
		
		return isPresent;
		
	}
	//----------------- Report 1 ends here-----------------------
	
	//----------------- Report 2 starts here-----------------------
	
	@GetMapping("report2/arrival/event/registrations/{eventId}")
	public ResponseEntity<Object> getArrivalTravelReport2DateWiseByEventId(@PathVariable int eventId,@RequestHeader("Authorization") String authorizationHeader)
	{	
		System.out.println("Entered");
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;		
		Map<String, Object> dataToBeSent = new HashMap<>();	
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
				if(role.equals("Super")||role.equals("Admin"))
				{
					
				}
				else
				{
					errorCode=400;
					errorMessage="Invalid Token Values";
					System.out.println("Hello3");
				}
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();	
				log.error("Inside report2/arrival/event/registrations/{eventId}"+errorMessage);
				
			}
		}
		if(errorCode==0)
		{	
	List<DataSecondReport> data=new ArrayList<>();
	String query1="select distinct arrival_date from event_registration where event_id=? order by arrival_date";
	Connection cn=null;
	try
	{
		Class.forName("com.mysql.cj.jdbc.Driver");
		cn=DriverManager.getConnection("jdbc:mysql://localhost/ajapa_yog?user=root&password=root");
		PreparedStatement ps1=cn.prepareStatement(query1);
		ps1.setInt(1, eventId);
		ResultSet rs1=ps1.executeQuery();		
		while(rs1.next())
		{
			String trainNames[]=new String[4];
			String userNames[]=new String[4];
			String userIds[]=new String[4];
			trainNames[0]="";
			trainNames[1]="";
			trainNames[2]="";
			trainNames[3]="";			
			int trainPerson[]=new int[4];
			int flightPerson[]=new int[4];
			int roadPerson[]=new int[4];			
			String arrival_date=rs1.getString(1);
			String query4="select t.arrival_mode_of_transport,t.arrival_train_number,t.arrival_time from event_registration t where t.arrival_date=? and t.event_id=?";
			PreparedStatement ps4=cn.prepareStatement(query4);
			ps4.setString(1,arrival_date);	
			ps4.setInt(2, eventId);						
			ResultSet rs4=ps4.executeQuery();			
			List<HashMap<String,Integer>> trainCount=new ArrayList<HashMap<String,Integer>>();
			trainCount.add(new HashMap<String,Integer>());
			trainCount.add(new HashMap<String,Integer>());
			trainCount.add(new HashMap<String,Integer>());
			trainCount.add(new HashMap<String,Integer>());			
		    while(rs4.next())
		    {
			String travelMode=rs4.getString(1);			
			String travelTrainDetails=rs4.getString(2);
			String arrivalTime=rs4.getString(3);
			int slots=Integer.parseInt(arrivalTime.split(":")[0]);
			int minutes=Integer.parseInt(arrivalTime.split(":")[1]);
			int index;
			if((slots>0 || (slots==0 && minutes>0)) && (slots<7 || (slots==7 && minutes==0)))
				index=0;			
			else if((slots>7 || (slots==7 && minutes>0))  && (slots<12 || (slots==12 && minutes==0)))
				index=1;
			else if((slots>12 || (slots==12 && minutes>0)) && (slots<18 || (slots==18 && minutes==0)))
				index=2;
			else 
				index=3;
							
			if(travelMode.equals("Train"))
			{
			trainPerson[index]++;			
			int tc=0;
			HashMap<String,Integer> tCount=trainCount.get(index);
			if(tCount.get(travelTrainDetails)!=null)
				tc=tCount.get(travelTrainDetails);
			tc++;
			tCount.put(travelTrainDetails, tc);
			}
			else if(travelMode.equals("Flight"))
			{
			flightPerson[index]++;
			}
			else
			{
			roadPerson[index]++;
			}			
		}	
		    DataSecondReport slot1=new DataSecondReport();
		    DataSecondReport slot2=new DataSecondReport();
		    DataSecondReport slot3=new DataSecondReport();
		    DataSecondReport slot4=new DataSecondReport();
		    slot1.setDate(arrival_date);
		    slot2.setDate(arrival_date);
		    slot3.setDate(arrival_date);
		    slot4.setDate(arrival_date);
		    slot1.setTimeSlot("12:01AM - 7:00AM");
		    slot2.setTimeSlot("07:01AM - 12:00Noon");
		    slot3.setTimeSlot("12:01Noon - 06:00PM");
		    slot4.setTimeSlot("06:01PM - 12:00AM");
		    slot1.setModeOfTransport("T:"+trainPerson[0]+",F:"+flightPerson[0]+",R:"+roadPerson[0]);
		    slot2.setModeOfTransport("T:"+trainPerson[1]+",F:"+flightPerson[1]+",R:"+roadPerson[1]);
		    slot3.setModeOfTransport("T:"+trainPerson[2]+",F:"+flightPerson[2]+",R:"+roadPerson[2]);
		    slot4.setModeOfTransport("T:"+trainPerson[3]+",F:"+flightPerson[3]+",R:"+roadPerson[3]);
		    String slot1TrainNumber="";
		    Iterator<Map.Entry<String, Integer>> iterator = trainCount.get(0).entrySet().iterator();
	        while (iterator.hasNext()) {
	            Map.Entry<String, Integer> entry = iterator.next();
	            String key = entry.getKey();
	            int value = entry.getValue();
	            slot1TrainNumber=slot1TrainNumber+key+"("+value+"),";
	        }
	        if(slot1TrainNumber.length()>0)
			slot1TrainNumber=slot1TrainNumber.substring(0,slot1TrainNumber.length()-1);
			String slot2TrainNumber="";
		    iterator = trainCount.get(1).entrySet().iterator();
	        while (iterator.hasNext()) {
	            Map.Entry<String, Integer> entry = iterator.next();
	            String key = entry.getKey();
	            int value = entry.getValue();
	            slot2TrainNumber=slot2TrainNumber+key+"("+value+"),";
	        }
	        if(slot2TrainNumber.length()>0)
			slot2TrainNumber=slot2TrainNumber.substring(0,slot2TrainNumber.length()-1);
			String slot3TrainNumber="";
		    iterator = trainCount.get(2).entrySet().iterator();
	        while (iterator.hasNext()) {
	            Map.Entry<String, Integer> entry = iterator.next();
	            String key = entry.getKey();
	            int value = entry.getValue();
	            slot3TrainNumber=slot3TrainNumber+key+"("+value+"),";
	        }
	        if(slot3TrainNumber.length()>0)
			slot3TrainNumber=slot3TrainNumber.substring(0,slot3TrainNumber.length()-1);
			String slot4TrainNumber="";
		    iterator = trainCount.get(3).entrySet().iterator();
	        while (iterator.hasNext()) {
	            Map.Entry<String, Integer> entry = iterator.next();
	            String key = entry.getKey();
	            int value = entry.getValue();
	            slot4TrainNumber=slot4TrainNumber+key+"("+value+"),";
	        }
	        if(slot4TrainNumber.length()>0)
			slot4TrainNumber=slot4TrainNumber.substring(0,slot4TrainNumber.length()-1);
			slot1.setTrainDetails(slot1TrainNumber);
			slot2.setTrainDetails(slot2TrainNumber);
			slot3.setTrainDetails(slot3TrainNumber);
			slot4.setTrainDetails(slot4TrainNumber);
			if(containsDigitsOtherThanZero(slot1.getModeOfTransport()))
			data.add(slot1);
			if(containsDigitsOtherThanZero(slot2.getModeOfTransport()))
			data.add(slot2);
			if(containsDigitsOtherThanZero(slot3.getModeOfTransport()))
			data.add(slot3);
			if(containsDigitsOtherThanZero(slot4.getModeOfTransport()))
			data.add(slot4);			
		}
	}
	catch(Exception e)
	{
		System.out.println(e.getMessage());
		
	}
	finally {
		try {
			cn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("Inside report2/arrival/event/registrations/{eventId}"+errorMessage);
		}
		
	}
	dataToBeSent.put("data", data);
	}
	dataToBeSent.put("message",message);
	dataToBeSent.put("errorMessage",errorMessage);	
	dataToBeSent.put("errorCode",errorCode);		
	if(errorCode==0)
		return new ResponseEntity<>(dataToBeSent, HttpStatus.OK);
	else 
		return new ResponseEntity<>(dataToBeSent, HttpStatus.BAD_REQUEST);	
	}
	@GetMapping("report2/departure/event/registrations/{eventId}")
	public ResponseEntity<Object> getdepartureTravelReport2DateWiseByEventId(@PathVariable int eventId,@RequestHeader("Authorization") String authorizationHeader)
	{	
		Logger log=LoggerFactory.getLogger(getClass());
		String message="";
		String errorMessage="";
		int errorCode=0;		
		Map<String, Object> dataToBeSent = new HashMap<>();	
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
				if(role.equals("Super")||role.equals("Admin"))
				{
					
				}
				else
				{
					errorCode=400;
					errorMessage="Invalid Token Values";
					System.out.println("Hello3");
				}
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();	
				log.error("Inside report2/departure/event/registrations/{eventId}"+errorMessage);
				
			}
		}
		if(errorCode==0)
		{	
	List<DataSecondReport> data=new ArrayList<>();
	String query1="select distinct departure_date from event_registration where event_id=? order by departure_date";
	Connection cn=null;
	try
	{
		Class.forName("com.mysql.cj.jdbc.Driver");
		cn=DriverManager.getConnection("jdbc:mysql://localhost/ajapa_yog?user=root&password=root");
		PreparedStatement ps1=cn.prepareStatement(query1);
		ps1.setInt(1, eventId);
		ResultSet rs1=ps1.executeQuery();		
		while(rs1.next())
		{
			String trainNames[]=new String[4];
			String userNames[]=new String[4];
			String userIds[]=new String[4];
			trainNames[0]="";
			trainNames[1]="";
			trainNames[2]="";
			trainNames[3]="";			
			int trainPerson[]=new int[4];
			int flightPerson[]=new int[4];
			int roadPerson[]=new int[4];			
			String departure_date=rs1.getString(1);
			String query4="select t.departure_mode_of_transport,t.departure_train_number,t.departure_time from event_registration t where t.departure_date=? and t.event_id=?";
			PreparedStatement ps4=cn.prepareStatement(query4);
			ps4.setString(1,departure_date);	
			ps4.setInt(2, eventId);						
			ResultSet rs4=ps4.executeQuery();			
			List<HashMap<String,Integer>> trainCount=new ArrayList<HashMap<String,Integer>>();
			trainCount.add(new HashMap<String,Integer>());
			trainCount.add(new HashMap<String,Integer>());
			trainCount.add(new HashMap<String,Integer>());
			trainCount.add(new HashMap<String,Integer>());			
		    while(rs4.next())
		    {
			String travelMode=rs4.getString(1);			
			String travelTrainDetails=rs4.getString(2);
			String arrivalTime=rs4.getString(3);
			int slots=Integer.parseInt(arrivalTime.split(":")[0]);
			int minutes=Integer.parseInt(arrivalTime.split(":")[1]);
			int index;
			if((slots>0 || (slots==0 && minutes>0)) && (slots<7 || (slots==7 && minutes==0)))
				index=0;			
			else if((slots>7 || (slots==7 && minutes>0))  && (slots<12 || (slots==12 && minutes==0)))
				index=1;
			else if((slots>12 || (slots==12 && minutes>0)) && (slots<18 || (slots==18 && minutes==0)))
				index=2;
			else 
				index=3;
							
			if(travelMode.equals("Train"))
			{
			trainPerson[index]++;			
			int tc=0;
			HashMap<String,Integer> tCount=trainCount.get(index);
			if(tCount.get(travelTrainDetails)!=null)
				tc=tCount.get(travelTrainDetails);
			tc++;
			tCount.put(travelTrainDetails, tc);
			}
			else if(travelMode.equals("Flight"))
			{
			flightPerson[index]++;
			}
			else
			{
			roadPerson[index]++;
			}			
		}	
		    DataSecondReport slot1=new DataSecondReport();
		    DataSecondReport slot2=new DataSecondReport();
		    DataSecondReport slot3=new DataSecondReport();
		    DataSecondReport slot4=new DataSecondReport();
		    slot1.setDate(departure_date);
		    slot2.setDate(departure_date);
		    slot3.setDate(departure_date);
		    slot4.setDate(departure_date);
		    slot1.setTimeSlot("12:01AM - 7:00AM");
		    slot2.setTimeSlot("07:01AM - 12:00Noon");
		    slot3.setTimeSlot("12:01Noon - 06:00PM");
		    slot4.setTimeSlot("06:01PM - 12:00AM");
		    slot1.setModeOfTransport("T:"+trainPerson[0]+",F:"+flightPerson[0]+",R:"+roadPerson[0]);
		    slot2.setModeOfTransport("T:"+trainPerson[1]+",F:"+flightPerson[1]+",R:"+roadPerson[1]);
		    slot3.setModeOfTransport("T:"+trainPerson[2]+",F:"+flightPerson[2]+",R:"+roadPerson[2]);
		    slot4.setModeOfTransport("T:"+trainPerson[3]+",F:"+flightPerson[3]+",R:"+roadPerson[3]);
		    String slot1TrainNumber="";
		    Iterator<Map.Entry<String, Integer>> iterator = trainCount.get(0).entrySet().iterator();
	        while (iterator.hasNext()) {
	            Map.Entry<String, Integer> entry = iterator.next();
	            String key = entry.getKey();
	            int value = entry.getValue();
	            slot1TrainNumber=slot1TrainNumber+key+"("+value+"),";
	        }
	        if(slot1TrainNumber.length()>0)
			slot1TrainNumber=slot1TrainNumber.substring(0,slot1TrainNumber.length()-1);
			String slot2TrainNumber="";
		    iterator = trainCount.get(1).entrySet().iterator();
	        while (iterator.hasNext()) {
	            Map.Entry<String, Integer> entry = iterator.next();
	            String key = entry.getKey();
	            int value = entry.getValue();
	            slot2TrainNumber=slot2TrainNumber+key+"("+value+"),";
	        }
	        if(slot2TrainNumber.length()>0)
			slot2TrainNumber=slot2TrainNumber.substring(0,slot2TrainNumber.length()-1);
			String slot3TrainNumber="";
		    iterator = trainCount.get(2).entrySet().iterator();
	        while (iterator.hasNext()) {
	            Map.Entry<String, Integer> entry = iterator.next();
	            String key = entry.getKey();
	            int value = entry.getValue();
	            slot3TrainNumber=slot3TrainNumber+key+"("+value+"),";
	        }
	        if(slot3TrainNumber.length()>0)
			slot3TrainNumber=slot3TrainNumber.substring(0,slot3TrainNumber.length()-1);
			String slot4TrainNumber="";
		    iterator = trainCount.get(3).entrySet().iterator();
	        while (iterator.hasNext()) {
	            Map.Entry<String, Integer> entry = iterator.next();
	            String key = entry.getKey();
	            int value = entry.getValue();
	            slot4TrainNumber=slot4TrainNumber+key+"("+value+"),";
	        }
	        if(slot4TrainNumber.length()>0)
			slot4TrainNumber=slot4TrainNumber.substring(0,slot4TrainNumber.length()-1);
			slot1.setTrainDetails(slot1TrainNumber);
			slot2.setTrainDetails(slot2TrainNumber);
			slot3.setTrainDetails(slot3TrainNumber);
			slot4.setTrainDetails(slot4TrainNumber);
			if(containsDigitsOtherThanZero(slot1.getModeOfTransport()))
			data.add(slot1);
			if(containsDigitsOtherThanZero(slot2.getModeOfTransport()))
			data.add(slot2);
			if(containsDigitsOtherThanZero(slot3.getModeOfTransport()))
			data.add(slot3);
			if(containsDigitsOtherThanZero(slot4.getModeOfTransport()))
			data.add(slot4);			
		}
	}
	catch(Exception e)
	{
		System.out.println(e.getMessage());
		
	}
	finally {
		try {
			cn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("Inside report2/departure/event/registrations/{eventId}"+errorMessage);
		}
		
	}
	dataToBeSent.put("data", data);
	}
	dataToBeSent.put("message",message);
	dataToBeSent.put("errorMessage",errorMessage);	
	dataToBeSent.put("errorCode",errorCode);		
	if(errorCode==0)
		return new ResponseEntity<>(dataToBeSent, HttpStatus.OK);
	else 
		return new ResponseEntity<>(dataToBeSent, HttpStatus.BAD_REQUEST);	
	}

}
