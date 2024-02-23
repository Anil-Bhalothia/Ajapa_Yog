package com.eschool.controller;

import java.util.Base64;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.eschool.beans.Attendance;
import com.eschool.beans.Food;
import com.eschool.repo.AttendanceRepository;
import com.eschool.repo.EventRegistrationRepository;
import com.eschool.repo.FoodRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.eschool.beans.EventRegistration;
@RestController
public class FoodController {
	@Autowired
	EventRegistrationRepository trepo;
	@Autowired
	AttendanceRepository arepo;
	@Autowired
	FoodRepository frepo;	
	@GetMapping("getFoodDetail/{eventId}/{entryDate}/{timings}")
	Food getFoodDetail(@PathVariable int eventId,@PathVariable String entryDate,@PathVariable String timings,@RequestHeader("Authorization") String authorizationHeader) {	
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
		Food food=null;
	if(errorCode==0 && (role.equals("Admin")||role.equals("Super")))	
	{		
	food=frepo.findByEventIdAndEntryDateAndTimings(eventId, entryDate, timings);
	if(food==null)
	{		
		List<EventRegistration> registeredUsers=trepo.findAllByEventIdAndEntryDateAndTimingsOrderByFamilyId(eventId,entryDate,timings);
		List<Attendance> presentUsers=arepo.findAllByEventId(eventId);
		food=new Food(0, eventId, entryDate, timings, presentUsers.size(), registeredUsers.size(),0);
	}
	}
	return food;
	}
	@GetMapping("getFoodDetails/{eventId}")
	List<Food> getFoodDetails(@PathVariable int eventId,@RequestHeader("Authorization") String authorizationHeader) {	
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
	List<Food> foods=null;
	if(errorCode==0 && (role.equals("Admin")||role.equals("Super")))	
	{		
		foods=frepo.findAllByEventId(eventId);			
	}
	return foods;
	}
	@PostMapping("saveFoodDetails")
	public ResponseEntity<Object> saveFoodDetails(@ModelAttribute Food food,@RequestHeader("Authorization") String authorizationHeader){	
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
	if(errorCode==0 && (role.equals("Admin")||role.equals("Super")))	
	{
	Food existingFood=frepo.findByEventIdAndEntryDateAndTimings(food.getEventId(),food.getEntryDate(),food.getTimings());	
	if(existingFood==null)
	{
		frepo.save(food);
	}
	else
	{
		existingFood.setEventId(food.getEventId());
		existingFood.setEntryDate(food.getEntryDate());
		existingFood.setFoodTakenCount(food.getFoodTakenCount());
		existingFood.setPresentCount(food.getPresentCount());
		existingFood.setTimings(food.getTimings());
		existingFood.setTotalCount(food.getTotalCount());
		frepo.save(existingFood);		
	}
	data.put("message","Data Saved");
	}	
	
	data.put("errorMessage",errorMessage);
	data.put("errorCode",errorCode);
	if(errorCode==0)
		return new ResponseEntity<>(data, HttpStatus.OK);
	else 
		return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
	
	}
	
}
