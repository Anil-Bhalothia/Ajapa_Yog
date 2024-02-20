package com.eschool.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.eschool.beans.EventRegistration;

import com.eschool.repo.EventRegistrationRepository;
import com.eschool.repo.UserRepository;
import com.eschool.service.ExcelService;
import com.eschool.service.PDFService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletContext;

@RestController
public class RegistrationController {
	@Autowired
	UserRepository uRepo;
	@Autowired
	EventRegistrationRepository erRepo;
	@Autowired
	ServletContext context;
	@PostMapping("event/registration/save")
	public ResponseEntity<Object> saveEventRegistration(@ModelAttribute EventRegistration eventRegistration,@RequestHeader("Authorization") String authorizationHeader) {
		String message="";
		String errorMessage="";
		int errorCode=0;		
		Map<String, Object> data = new HashMap<>();	
		String email=null,role=null;
		int familyId=0;
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String jwtToken = authorizationHeader.substring(7); // Removing "Bearer " prefix
			try {
				Base64.Decoder decoder = Base64.getUrlDecoder();
				String chunks[] = jwtToken.split("\\.");
				String payload = new String(decoder.decode(chunks[1]));
				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> map = mapper.readValue(payload, Map.class);
				email = map.get("email").toString();
				role = map.get("role").toString();	
				familyId=Integer.valueOf(map.get("familyId").toString());				
				if(familyId==eventRegistration.getFamilyId())
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
			}
		}
		if(errorCode==0)
		{
		try {	
			erRepo.save(eventRegistration);			
			message="You have been registered successfully";			
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
	@GetMapping("/event/registration/{registrationId}")
	public ResponseEntity<Object> getEventRegistration(@RequestHeader("Authorization") String authorizationHeader,@PathVariable("registrationId") int registrationId) {
		String message="";
		String errorMessage="";
		int errorCode=0;
		String email = "";
		String role="";
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
				familyId=Integer.valueOf(map.get("familyId").toString());				
				errorCode=0;
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();				
			}
		}
		try
		{
		EventRegistration eventRegistration = null;
		eventRegistration=erRepo.findByRegistrationId(registrationId);
		if(errorCode==0 && (role.equals("Super") || role.equals("Admin") ||familyId==eventRegistration.getFamilyId()))
		{				
			data.put("eventRegistration", eventRegistration);
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
		}		
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);		
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);  	
	}  	
	@GetMapping("/event/registration/list")
	public ResponseEntity<Object> getAllEventRegistrationsByEventId(@RequestParam int page, @RequestParam int rowsPerPage,@RequestParam int eventId,@RequestHeader("Authorization") String authorizationHeader) {
		
		String message="";
		String errorMessage="";
		String email=null,role=null;		
		int errorCode=0;
		int start=(page-1)*rowsPerPage;
		int end=start+rowsPerPage;
		List<EventRegistration> myEventRegistrations=null;
		List<EventRegistration> eventRegistrations=null;
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
				}
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();	
			}
		}
		
		if(errorCode==0)
		{				
		try
		{
			eventRegistrations=erRepo.findAllByEventIdOrderByFamilyId(eventId);
			if (start >= 0 && end < eventRegistrations.size() && start <= end) {
	    	errorCode=0;
	    	message="success";	    	
	        myEventRegistrations=eventRegistrations.subList(start, end);
			} 
			else if (start >= 0 && start <= end) 
			{
	    	errorCode=0;
	    	message="success";
	    	myEventRegistrations=eventRegistrations.subList(start, eventRegistrations.size());
			}
			else{
	    	errorCode=400;
	    	errorMessage="No more events";
	    	myEventRegistrations=Collections.emptyList();
			}
		}
	   catch(Exception e)
	   {
		   errorCode=500;
		   errorMessage=e.getMessage();
		   myEventRegistrations=Collections.emptyList();
	   }
	}
		Map<String, Object> data = new HashMap<>();	
		data.put("data", myEventRegistrations);
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);
		if(eventRegistrations!=null)
		data.put("totalElement", eventRegistrations.size());		    
		data.put("pageNumber", page);		
		data.put("rowPerPage", rowsPerPage);
		if(eventRegistrations!=null && eventRegistrations.size()%rowsPerPage==0)
			data.put("totalNoOfPages", eventRegistrations.size()/rowsPerPage);	
		else if(eventRegistrations!=null)
			data.put("totalNoOfPages", eventRegistrations.size()/rowsPerPage+1);	
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping("reportExcel/event/registration/list")
	public ResponseEntity<Object> getReportExcelAllEventRegistrationsByEventId(@RequestParam int eventId,@RequestHeader("Authorization") String authorizationHeader) {
		String fileName="";
		String message="";
		String errorMessage="";
		String email=null,role=null;		
		int errorCode=0;
		List<EventRegistration> eventRegistrations=null;
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
				}
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();	
			}
		}
		
		if(errorCode==0)
		{				
		try
		{
			eventRegistrations=erRepo.findAllByEventIdOrderByFamilyId(eventId);
			fileName=context.getRealPath("/")+"/reports/"+System.currentTimeMillis()+".xlsx";
			
			ExcelService service=new ExcelService();
			service.generateExcelFileForEventRegistrations(eventRegistrations,fileName,uRepo);					
			
		}
	   catch(Exception e)
	   {
		   errorCode=500;
		   errorMessage=e.getMessage();		 
	   }
	}
		Map<String, Object> data = new HashMap<>();	
		data.put("fileName",fileName);
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);	
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping("reportPDF/event/registration/list")
	public ResponseEntity<Object> getReportPDFAllEventRegistrationsByEventId(@RequestParam int eventId,@RequestHeader("Authorization") String authorizationHeader) {
		
		String fileName=context.getRealPath("/")+"/reports/"+System.currentTimeMillis()+".pdf";
		byte[] pdfBytes=null;
		String message="";
		String errorMessage="";
		String email=null,role=null;		
		int errorCode=0;
		List<EventRegistration> eventRegistrations=null;
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
				}
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();	
			}
		}
		
		if(errorCode==0)
		{				
		try
		{
			eventRegistrations=erRepo.findAllByEventIdOrderByFamilyId(eventId);
			PDFService pdfGeneratorService=new PDFService();
			pdfBytes = pdfGeneratorService.generatePDFFileForEventRegistrations(eventRegistrations,uRepo);
            convertByteArrayToFile(pdfBytes, fileName);		
		}
	   catch(Exception e)
	   {
		   errorCode=500;
		   errorMessage=e.getMessage();		 
	   }
	}
		Map<String, Object> data = new HashMap<>();	
		data.put("fileName",fileName);
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
	
	
	
	@PutMapping("/event/registration/delete/{eventId}/{familyId}")
	public ResponseEntity<Object> deleteAllEventRegistrationsByEventIdAndFamilyId(@RequestParam int eventId, @RequestParam int familyId,@RequestHeader("Authorization") String authorizationHeader) {
		String message="";
		String errorMessage="";
		String email=null,role=null;		
		int errorCode=0;
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
				}
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();	
			}
		}
		
		if(errorCode==0)
		{				
		try
		{
			erRepo.deleteByEventIdAndFamilyId(eventId, familyId);
			message="Registration cancelled successfully";
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
	
	@GetMapping("/event/registration/family/list")
	public ResponseEntity<Object> getAllEventRegistrationsByFamilyId(@RequestParam int page, @RequestParam int rowsPerPage,@RequestHeader("Authorization") String authorizationHeader) {
		String message="";
		String errorMessage="";
		String email=null,role=null;		
		int errorCode=0;
		int familyId=0;
		int start=(page-1)*rowsPerPage;
		int end=start+rowsPerPage;
		List<EventRegistration> myEventRegistrations=null;
		List<EventRegistration> eventRegistrations=null;
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String jwtToken = authorizationHeader.substring(7); // Removing "Bearer " prefix
			try {
				Base64.Decoder decoder = Base64.getUrlDecoder();
				String chunks[] = jwtToken.split("\\.");
				String payload = new String(decoder.decode(chunks[1]));
				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> map = mapper.readValue(payload, Map.class);
				email = map.get("email").toString();
				role = map.get("role").toString();	
				familyId=Integer.valueOf(map.get("familyId").toString());				
				
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();	
			}
		}
		
		if(errorCode==0)
		{				
		try
		{
			eventRegistrations=erRepo.findAllByFamilyIdOrderByEventIdDesc(familyId);
			if (start >= 0 && end < eventRegistrations.size() && start <= end) {
	    	errorCode=0;
	    	message="success";	    	
	        myEventRegistrations=eventRegistrations.subList(start, end);
			} 
			else if (start >= 0 && start <= end) 
			{
	    	errorCode=0;
	    	message="success";
	    	myEventRegistrations=eventRegistrations.subList(start, eventRegistrations.size());
			}
			else{
	    	errorCode=400;
	    	errorMessage="No more events";
	    	myEventRegistrations=Collections.emptyList();
			}
		}
	   catch(Exception e)
	   {
		   errorCode=500;
		   errorMessage=e.getMessage();
		   myEventRegistrations=Collections.emptyList();
	   }
	}
		Map<String, Object> data = new HashMap<>();	
		data.put("data", myEventRegistrations);
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);
		if(eventRegistrations!=null)
		data.put("totalElement", eventRegistrations.size());		    
		data.put("pageNumber", page);		
		data.put("rowPerPage", rowsPerPage);
		if(eventRegistrations!=null && eventRegistrations.size()%rowsPerPage==0)
			data.put("totalNoOfPages", eventRegistrations.size()/rowsPerPage);	
		else if(eventRegistrations!=null)
			data.put("totalNoOfPages", eventRegistrations.size()/rowsPerPage+1);	
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping("/event/registration/family/event/list")
	public ResponseEntity<Object> getAllEventRegistrationsByFamilyIdAndEventId(@RequestParam int eventId,@RequestHeader("Authorization") String authorizationHeader) {
		String message="";
		String errorMessage="";
		String email=null,role=null;		
		int errorCode=0;
		int familyId=0;
		List<EventRegistration> eventRegistrations=null;
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String jwtToken = authorizationHeader.substring(7); // Removing "Bearer " prefix
			try {
				Base64.Decoder decoder = Base64.getUrlDecoder();
				String chunks[] = jwtToken.split("\\.");
				String payload = new String(decoder.decode(chunks[1]));
				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> map = mapper.readValue(payload, Map.class);
				email = map.get("email").toString();
				role = map.get("role").toString();	
				familyId=Integer.valueOf(map.get("familyId").toString());				
				
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();	
			}
		}
		
		if(errorCode==0)
		{				
		try
		{
			eventRegistrations=erRepo.findAllByFamilyIdAndEventId(familyId,eventId);
		}
	   catch(Exception e)
	   {
		   errorCode=500;
		   errorMessage=e.getMessage();
		   eventRegistrations=Collections.emptyList();
	   }
	}
		Map<String, Object> data = new HashMap<>();	
		data.put("data", eventRegistrations);
		data.put("message",message);
		data.put("errorMessage",errorMessage);	
		data.put("errorCode",errorCode);
		if(errorCode==0)
			return new ResponseEntity<>(data, HttpStatus.OK);
		else 
			return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
	}
	
	
	
	
	
	@PostMapping("event/registration/delete")
	public ResponseEntity<Object> deleteEventRegistration(@RequestParam int registrationId,@RequestHeader("Authorization") String authorizationHeader) {
		String message="";
		String errorMessage="";
		int errorCode=0;
		int familyId=0;
		String email=null,role=null;
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
				familyId=Integer.valueOf(map.get("familyId").toString());				
				errorCode=0;
				} 
			catch (Exception e) {
				errorCode=500;
				errorMessage=e.getMessage();	
			}
		}
		EventRegistration eventRegistration=erRepo.findByRegistrationId(registrationId);
		if(eventRegistration!=null && errorCode==0 && (role.equals("Super") || role.equals("Admin") || familyId==eventRegistration.getFamilyId()))
		{
			try {	
			erRepo.deleteById(registrationId);		
			message = "Data deleted successfully";
		} catch (Exception e) {			
			errorMessage = e.getMessage();
			errorCode=500;
		}
		}
	else
		{
				errorCode=400;
				errorMessage="Invalid Token";
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
