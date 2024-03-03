package com.eschool.beans;

import java.sql.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="event")
public class Event {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int eventId;
	private String eventName;
	private String eventType; //Online Offline	
	private String eventLocation; 		
	private String startTime;
	private String endTime;
	private boolean eventStatus=true;
	private boolean bookingStatus=true;
	private String eventDate;
	private String lockArrivalDate;
	private String lockDepartureDate;
	private String shivirStartDate;
	private String shivirEndDate;
	private boolean shivirAvailable;
	private String createdBy;
	private String statusChangedBy;
	private String deletedBy;
	private String eventImage;
	
	public Event(int eventId, String eventName, String eventType, String eventLocation, String startTime,
			String endTime, boolean eventStatus, boolean bookingStatus, String eventDate, String lockArrivalDate,
			String lockDepartureDate, String shivirStartDate, String shivirEndDate, boolean shivirAvailable,
			String createdBy, String statusChangedBy, String deletedBy,String eventImage) {
		
		this.eventId = eventId;
		this.eventName = eventName;
		this.eventType = eventType;
		this.eventLocation = eventLocation;
		this.startTime = startTime;
		this.endTime = endTime;
		this.eventStatus = eventStatus;
		this.bookingStatus = bookingStatus;
		this.eventDate = eventDate;
		this.lockArrivalDate = lockArrivalDate;
		this.lockDepartureDate = lockDepartureDate;
		this.shivirStartDate = shivirStartDate;
		this.shivirEndDate = shivirEndDate;
		this.shivirAvailable = shivirAvailable;
		this.createdBy = createdBy;
		this.statusChangedBy = statusChangedBy;
		this.deletedBy = deletedBy;
		this.eventImage=eventImage;
	}
	
	public String getEventImage() {
		return eventImage;
	}

	public void setEventImage(String eventImage) {
		this.eventImage = eventImage;
	}

	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getEventLocation() {
		return eventLocation;
	}
	public void setEventLocation(String eventLocation) {
		this.eventLocation = eventLocation;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public boolean getEventStatus() {
		return eventStatus;
	}
	public void setEventStatus(boolean eventStatus) {
		this.eventStatus = eventStatus;
	}
	public boolean getBookingStatus() {
		return bookingStatus;
	}
	public void setBookingStatus(boolean bookingStatus) {
		this.bookingStatus = bookingStatus;
	}
	public String getEventDate() {
		return eventDate;
	}
	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}
	public String getLockArrivalDate() {
		return lockArrivalDate;
	}
	public void setLockArrivalDate(String lockArrivalDate) {
		this.lockArrivalDate = lockArrivalDate;
	}
	public String getLockDepartureDate() {
		return lockDepartureDate;
	}
	public void setLockDepartureDate(String lockDepartureDate) {
		this.lockDepartureDate = lockDepartureDate;
	}
	public String getShivirStartDate() {
		return shivirStartDate;
	}
	public void setShivirStartDate(String shivirStartDate) {
		this.shivirStartDate = shivirStartDate;
	}
	public String getShivirEndDate() {
		return shivirEndDate;
	}
	public void setShivirEndDate(String shivirEndDate) {
		this.shivirEndDate = shivirEndDate;
	}
	public boolean isShivirAvailable() {
		return shivirAvailable;
	}
	public void setShivirAvailable(boolean shivirAvailable) {
		this.shivirAvailable = shivirAvailable;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getStatusChangedBy() {
		return statusChangedBy;
	}
	public void setStatusChangedBy(String statusChangedBy) {
		this.statusChangedBy = statusChangedBy;
	}
	public String getDeletedBy() {
		return deletedBy;
	}
	public void setDeletedBy(String deletedBy) {
		this.deletedBy = deletedBy;
	}
	public Event() {		
	}
	
}
