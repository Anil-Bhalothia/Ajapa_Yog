package com.eschool.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.eschool.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.eschool.beans.DataFirstReport;
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

	//----------------- Report 1 ends here-----------------------
	
	//----------------- Report 2 starts here-----------------------
	/*
	@GetMapping("getTravelReportDateWise2/{eventId}")
	List<TravelDateWiseReportData2> getTravelReportDateWiseByEventId2(@PathVariable int eventId) {	
	List<TravelDateWiseReportData2> data=new ArrayList<>();
	String query1="select distinct arrival_date from event_registration where event_id=? order by arrival_date";
	Connection cn=null;
	try
	{
		Class.forName("com.mysql.cj.jdbc.Driver");
		cn=DriverManager.getConnection("jdbc:mysql://localhost/ajapa?user=root&password=root");
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
			TravelDateWiseReportData2 record=new TravelDateWiseReportData2();
			java.sql.Date arrival_date=rs1.getDate(1);
			String query2="select count(*),count(distinct travel.family_id) from travel where event_id=? and arrival_date=?";
			PreparedStatement ps2=cn.prepareStatement(query2);
			ps2.setInt(1, eventId);			
			ps2.setDate(2,arrival_date);			
			ResultSet rs2=ps2.executeQuery();
			rs2.next();
			int totalPerson=rs2.getInt(1);			
			int totalFamilies=rs2.getInt(2);
			String query4="select t.arrival_mode_of_transport,t.arrival_train_number,t.arrival_time from travel t where t.arrival_date=? and t.event_id=?";
			PreparedStatement ps4=cn.prepareStatement(query4);
			ps4.setDate(1,arrival_date);	
			ps4.setInt(2, eventId);			
			record.setTotalFamilies(totalFamilies);
			record.setTotalPersons(totalPerson);
			record.setTravelDate(arrival_date);			
			ResultSet rs4=ps4.executeQuery();
		
		while(rs4.next())
		{
			String travelMode=rs4.getString(1);			
			String travelTrainDetails=rs4.getString(2);
			String arrivalTime=rs4.getString(3);
			int slots=Integer.parseInt(arrivalTime.split(":")[0]);
			int index;
			if(slots>0 && slots<=7)
				index=0;
			else if(slots>7 && slots<=12)
				index=1;
			else if(slots>12 && slots<=17)
				index=2;
			else 
				index=3;
			System.out.println(travelMode+":"+travelMode.length());				
			if(travelMode.equals("Train"))
			{
			trainPerson[index]++;
			if(!trainNames[index].contains(travelTrainDetails))
				trainNames[index]=trainNames[index]+","+travelTrainDetails;
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
		record.setFlightPerson(flightPerson);
		record.setRoadPerson(roadPerson);
		record.setTrainNames(trainNames);
		record.setTrainPerson(trainPerson);		
		data.add(record);
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
		}
	}
		return data;
	}
	*/
	
}
