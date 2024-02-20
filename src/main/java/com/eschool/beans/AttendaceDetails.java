package com.eschool.beans;

import java.util.ArrayList;

public class AttendaceDetails {
	private ArrayList<Integer> events;
	private ArrayList<Integer> users;
	private ArrayList<Boolean> isPresent;
	private ArrayList<String> hallNo;	
	public AttendaceDetails() {
		
	}
	public AttendaceDetails(ArrayList<Integer> events, ArrayList<Integer> users, ArrayList<Boolean> isPresent,
			ArrayList<String> hallNo) {	
		this.events = events;
		this.users = users;
		this.isPresent = isPresent;
		this.hallNo = hallNo;
	}
	public ArrayList<String> getHallNo() {
		return hallNo;
	}

	public void setHallNo(ArrayList<String> hallNo) {
		this.hallNo = hallNo;
	}	
	public ArrayList<Integer> getEvents() {
		return events;
	}
	public void setEvents(ArrayList<Integer> events) {
		this.events = events;
	}
	public ArrayList<Integer> getUsers() {
		return users;
	}
	public void setUsers(ArrayList<Integer> users) {
		this.users = users;
	}
	public ArrayList<Boolean> getIsPresent() {
		return isPresent;
	}
	public void setIsPresent(ArrayList<Boolean> isPresent) {
		this.isPresent = isPresent;
	}    
}
