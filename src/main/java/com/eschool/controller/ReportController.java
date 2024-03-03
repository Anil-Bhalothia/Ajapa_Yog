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
				System.out.println("Hello4");
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
			String arrival_date=rs1.getString(1);			
			String query_2="select count(distinct family_id),COUNT(IF(TIMESTAMPDIFF(year,dob,now())>45, 1, NULL)),COUNT(IF(TIMESTAMPDIFF(year,dob,now())<10, 1, NULL)),COUNT(IF(gender='Male', 1, NULL)),COUNT(IF(gender='Female', 1, NULL)) from event_registration er,user u  where u.id=er.user_id and event_id=? and attending_shivir=? and arrival_date=?";
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
				System.out.println("Hello4");
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
			String arrival_date=rs1.getString(1);			
			String query_2="select count(distinct family_id),COUNT(IF(TIMESTAMPDIFF(year,dob,now())>45, 1, NULL)),COUNT(IF(TIMESTAMPDIFF(year,dob,now())<10, 1, NULL)),COUNT(IF(gender='Male', 1, NULL)),COUNT(IF(gender='Female', 1, NULL)) from event_registration er,user u  where u.id=er.user_id and event_id=? and arrival_date=?";
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
				System.out.println("Hello4");
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
			String departure_date=rs1.getString(1);			
			String query_2="select count(distinct family_id),COUNT(IF(TIMESTAMPDIFF(year,dob,now())>45, 1, NULL)),COUNT(IF(TIMESTAMPDIFF(year,dob,now())<10, 1, NULL)),COUNT(IF(gender='Male', 1, NULL)),COUNT(IF(gender='Female', 1, NULL)) from event_registration er,user u  where u.id=er.user_id and event_id=? and attending_shivir=? and departure_date=?";
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				System.out.println("Hello4");
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
			String departure_date=rs1.getString(1);			
			String query_2="select count(distinct family_id),COUNT(IF(TIMESTAMPDIFF(year,dob,now())>45, 1, NULL)),COUNT(IF(TIMESTAMPDIFF(year,dob,now())<10, 1, NULL)),COUNT(IF(gender='Male', 1, NULL)),COUNT(IF(gender='Female', 1, NULL)) from event_registration er,user u  where u.id=er.user_id and event_id=? and departure_date=?";
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
	
	
	
}
