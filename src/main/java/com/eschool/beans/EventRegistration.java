package com.eschool.beans;

import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "event_registration")
public class EventRegistration {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int registrationId;
    private int eventId;
    private int userId;
    private String userName; 
    private String eventName;     
    private int familyId;
    private String eventDate;
    private String fromCity;
    private String fromState;
    private String fromCountry;
    private String arrivalDate;
    private String arrivalTime;
    private String arrivalModeOfTransport;
    private String arrivalTrainNumber;
    private String departureDate;
    private String departureTime;
    private String departureModeOfTransport;
    private String departureTrainNumber;
    private String specificRequirements;	    
    boolean attendingShivir;
    public String getEventDate() {
		return eventDate;
	}
	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}
	public EventRegistration() {
		
	}
	 	public EventRegistration(int registrationId, int eventId, int userId, String userName, int familyId, String fromCity,
			String fromState, String fromCountry, String arrivalDate, String arrivalTime, String arrivalModeOfTransport,
			String arrivalTrainNumber, String departureDate, String departureTime, String departureModeOfTransport,
			String departureTrainNumber, String specificRequirements,String eventDate, boolean attendingShivir,String eventName) {
		this.registrationId = registrationId;
		this.eventDate=eventDate;
		this.eventId = eventId;
		this.userId = userId;
		this.userName = userName;
		this.familyId = familyId;
		this.fromCity = fromCity;
		this.fromState = fromState;
		this.fromCountry = fromCountry;
		this.arrivalDate = arrivalDate;
		this.arrivalTime = arrivalTime;
		this.arrivalModeOfTransport = arrivalModeOfTransport;
		this.arrivalTrainNumber = arrivalTrainNumber;
		this.departureDate = departureDate;
		this.departureTime = departureTime;
		this.departureModeOfTransport = departureModeOfTransport;
		this.departureTrainNumber = departureTrainNumber;
		this.specificRequirements = specificRequirements;
		this.attendingShivir = attendingShivir;
		this.eventName=eventName;
	}
		
		public String getEventName() {
			return eventName;
		}
		public void setEventName(String eventName) {
			this.eventName = eventName;
		}
		public int getRegistrationId() {
		return registrationId;
	}
	public void setRegistrationId(int registrationId) {
		this.registrationId = registrationId;
	}
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getFamilyId() {
		return familyId;
	}
	public void setFamilyId(int familyId) {
		this.familyId = familyId;
	}
	public String getFromCity() {
		return fromCity;
	}
	public void setFromCity(String fromCity) {
		this.fromCity = fromCity;
	}
	public String getFromState() {
		return fromState;
	}
	public void setFromState(String fromState) {
		this.fromState = fromState;
	}
	public String getFromCountry() {
		return fromCountry;
	}
	public void setFromCountry(String fromCountry) {
		this.fromCountry = fromCountry;
	}
	public String getArrivalDate() {
		return arrivalDate;
	}
	public void setArrivalDate(String arrivalDate) {
		this.arrivalDate = arrivalDate;
	}
	public String getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public String getArrivalModeOfTransport() {
		return arrivalModeOfTransport;
	}
	public void setArrivalModeOfTransport(String arrivalModeOfTransport) {
		this.arrivalModeOfTransport = arrivalModeOfTransport;
	}
	public String getArrivalTrainNumber() {
		return arrivalTrainNumber;
	}
	public void setArrivalTrainNumber(String arrivalTrainNumber) {
		this.arrivalTrainNumber = arrivalTrainNumber;
	}
	public String getDepartureDate() {
		return departureDate;
	}
	public void setDepartureDate(String departureDate) {
		this.departureDate = departureDate;
	}
	public String getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}
	public String getDepartureModeOfTransport() {
		return departureModeOfTransport;
	}
	public void setDepartureModeOfTransport(String departureModeOfTransport) {
		this.departureModeOfTransport = departureModeOfTransport;
	}
	public String getDepartureTrainNumber() {
		return departureTrainNumber;
	}
	public void setDepartureTrainNumber(String departureTrainNumber) {
		this.departureTrainNumber = departureTrainNumber;
	}
	public String getSpecificRequirements() {
		return specificRequirements;
	}
	public void setSpecificRequirements(String specificRequirements) {
		this.specificRequirements = specificRequirements;
	}
	public boolean getAttendingShivir() {
		return attendingShivir;
	}
	public void setAttendingShivir(boolean attendingShivir) {
		this.attendingShivir = attendingShivir;
	}
		
}
