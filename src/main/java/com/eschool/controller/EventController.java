package com.eschool.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eschool.beans.Event;
import com.eschool.beans.User;
import com.eschool.repo.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletContext;

@RestController
public class EventController {
	@Autowired
	EventRepository eRepo;
	@Autowired
	ServletContext context;
	
	@PostMapping("saveEventWithImage")
	public ResponseEntity<Object> saveEventWithImage(@ModelAttribute Event event,@RequestParam("file") MultipartFile eventImage,@RequestHeader("Authorization") String authorizationHeader) {
		System.out.println("Hello");
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
				if(role.equals("User")||role.equals("Super")||role.equals("Admin"))
				{
					System.out.println("Hello2"+role);
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
		try {			
			String fname=System.currentTimeMillis()+".jpg";
			event.setEventImage(fname);
			eRepo.save(event);			
			if(eventImage!=null)
			{			
			String path = context.getRealPath("/") + "\\events";			
			File fl = new File(path);
			if (!fl.exists())
				fl.mkdir();			
			path=path+ "\\" + fname;			
			Path root = Paths.get( path);
			if (Files.exists(root)) {
				Files.delete(root);
			}
			Files.copy(eventImage.getInputStream(), root);
			}
			message = "Data saved successfully";
			}
		catch (Exception e) {			
			errorMessage = e.getMessage();
			errorCode=500;
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
	
	@PostMapping("saveEvent")
	public ResponseEntity<Object> saveEvent(@ModelAttribute Event event,@RequestHeader("Authorization") String authorizationHeader) {
		System.out.println("Hello");
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
				if(role.equals("User")||role.equals("Super")||role.equals("Admin"))
				{
					System.out.println("Hello2"+role);
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
		try {	
			
			String fname=System.currentTimeMillis()+".jpg";
			if(!(event.getEventImage()!=null && event.getEventImage().length()!=0))				
			{
			event.setEventImage(fname);
			String srcPath = context.getRealPath("/") + "\\events\\dummy.jpg";
			String destPath = context.getRealPath("/") + "\\events\\"+fname;
			File srcFile=new File(srcPath);
			File destFile=new File(destPath);
			if(!destFile.exists())
			FileUtils.copyFile(new File(srcPath), new File(destPath));
			}
			eRepo.save(event);
			message = "Data saved successfully";
			}
		catch (Exception e) {			
			errorMessage = e.getMessage();
			errorCode=500;
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
	@GetMapping("/event/{eventId}")
	public ResponseEntity<Object> getEvent(@PathVariable int eventId, @RequestHeader("Authorization") String authorizationHeader)
		{		
		String message="";
		String errorMessage="";
		int errorCode=0;
		Event event=null;
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
				if(role.equals("User")||role.equals("Super")||role.equals("Admin")||role.equals("Member"))
				{
					System.out.println("Hello2"+role);
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
		try
		{
			event= eRepo.findByEventId(eventId);
			if(event==null)
			{
				errorCode=400;
				errorMessage="Invalid Event Id";
			}
			else
				message="success";
		}
	   catch(Exception e)
	   {
		   errorCode=500;
		   errorMessage=e.getMessage();
	   }
	}
		Map<String, Object> data = new HashMap<>();	
		data.put("data", event);
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
	   	if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
      
		}
		
	@PostMapping("/event/status/{eventId}/{eventStatus}")
	public ResponseEntity<Object> changeEventStatus(@PathVariable int eventId,@PathVariable boolean eventStatus, @RequestHeader("Authorization") String authorizationHeader)
		{		
		String message="";
		String errorMessage="";
		int errorCode=0;
		Event event=null;
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
				if(role.equals("User")||role.equals("Super")||role.equals("Admin"))
				{
					System.out.println("Hello2"+role);
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
		
		if(errorCode==0 && (role.equals("Super")||role.equals("Admin")))
		{		
		try
		{
			event= eRepo.findByEventId(eventId);			
			if(event==null)
			{
				errorCode=400;
				errorMessage="Invalid Event Id";
			}
			else
			{
				event.setEventStatus(eventStatus);
				if(eventStatus==false)
					event.setBookingStatus(false);
				eRepo.save(event);
				message="Event Status Changed";
			}
		}
	   catch(Exception e)
	   {
		   errorCode=500;
		   errorMessage=e.getMessage();
	   }
	}
		Map<String, Object> data = new HashMap<>();	
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
	   	if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
      
		}	
	
	@PostMapping("/event/booking/{eventId}/{bookingStatus}")
	public ResponseEntity<Object> changeBooikingStatus(@PathVariable int eventId,@PathVariable boolean bookingStatus, @RequestHeader("Authorization") String authorizationHeader)
		{		
		String message="";
		String errorMessage="";
		int errorCode=0;
		Event event=null;
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
				if(role.equals("User")||role.equals("Super")||role.equals("Admin"))
				{
					System.out.println("Hello2"+role);
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
		
		if(errorCode==0 && (role.equals("Super")||role.equals("Admin")))
		{		
		try
		{
			event= eRepo.findByEventId(eventId);			
			if(event==null)
			{
				errorCode=400;
				errorMessage="Invalid Event Id";
			}
			else
			{
				event.setBookingStatus(bookingStatus);
				eRepo.save(event);
				message="Booking Status Changed";
			}
		}
	   catch(Exception e)
	   {
		   errorCode=500;
		   errorMessage=e.getMessage();
	   }
	}
		Map<String, Object> data = new HashMap<>();	
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
	   	if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
      
		}
	
	@GetMapping("/event/list")
	public ResponseEntity<Object> getEvents(@RequestParam int page, @RequestParam int rowsPerPage,@RequestParam String eventName,@RequestParam boolean eventStatus,@RequestParam boolean bookingStatus) {
		String message="";
		String errorMessage="";
		int errorCode=0;
		int start=(page-1)*rowsPerPage;
		int end=start+rowsPerPage;
		List<Event> myEvents=null;
		List<Event> events=null;
		try
		{
			events= eRepo.findByEventNameStartingWithAndEventStatusAndBookingStatusOrderByEventDateDesc(eventName,eventStatus,bookingStatus);
			if (start >= 0 && end < events.size() && start <= end) {
	    	errorCode=0;
	    	message="success";
	        myEvents=events.subList(start, end);
			} 
			else if (start >= 0 && start <= end) 
			{
	    	errorCode=0;
	    	message="success";
	    	myEvents=events.subList(start, events.size());
			}
			else{
	    	errorCode=400;
	    	errorMessage="No more events";
	    	myEvents=Collections.emptyList();
			}
		}
	   catch(Exception e)
	   {
		   errorCode=500;
		   errorMessage=e.getMessage();
		   myEvents=Collections.emptyList();
	   }
		
		Map<String, Object> data = new HashMap<>();	
		data.put("data", myEvents);
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
	    data.put("totalElement", events.size());		    
		data.put("pageNumber", page);		
		data.put("rowPerPage", rowsPerPage);
		if(events.size()%rowsPerPage==0)
		data.put("totalNoOfPages", events.size()/rowsPerPage);	
		else
			data.put("totalNoOfPages", events.size()/rowsPerPage+1);	
		
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
      
	}

	@GetMapping("/event/all/list")
	public ResponseEntity<Object> getAllEvents() {
		String message="";
		String errorMessage="";
		int errorCode=0;
		List<Event> myEvents=null;
		try
		{
			myEvents= eRepo.findByEventStatusOrderByEventDateDesc(true);
			
		}
	   catch(Exception e)
	   {
		   errorCode=500;
		   errorMessage=e.getMessage();
		   myEvents=Collections.emptyList();
	   }
		
		Map<String, Object> data = new HashMap<>();	
		data.put("data", myEvents);
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
	    if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
      
	}	
}
