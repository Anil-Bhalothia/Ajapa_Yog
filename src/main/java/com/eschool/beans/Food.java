package com.eschool.beans;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name="food")
public class Food {
@Id
@GeneratedValue
private int foodId;
private int eventId;
private String entryDate;
private String timings;
private int presentCount,totalCount,foodTakenCount;
public Food(int foodId, int eventId, String entryDate, String timings, int presentCount, int totalCount,
		int foodTakenCount) {
	this.foodId = foodId;
	this.eventId = eventId;
	this.entryDate = entryDate;
	this.timings = timings;
	this.presentCount = presentCount;
	this.totalCount = totalCount;
	this.foodTakenCount = foodTakenCount;
}
public Food() {
	}
public int getFoodId() {
	return foodId;
}
public void setFoodId(int foodId) {
	this.foodId = foodId;
}
public int getEventId() {
	return eventId;
}
public void setEventId(int eventId) {
	this.eventId = eventId;
}
public String getEntryDate() {
	return entryDate;
}
public void setEntryDate(String entryDate) {
	this.entryDate = entryDate;
}
public String getTimings() {
	return timings;
}
public void setTimings(String timings) {
	this.timings = timings;
}
public int getPresentCount() {
	return presentCount;
}
public void setPresentCount(int presentCount) {
	this.presentCount = presentCount;
}
public int getTotalCount() {
	return totalCount;
}
public void setTotalCount(int totalCount) {
	this.totalCount = totalCount;
}
public int getFoodTakenCount() {
	return foodTakenCount;
}
public void setFoodTakenCount(int foodTakenCount) {
	this.foodTakenCount = foodTakenCount;
}

}
