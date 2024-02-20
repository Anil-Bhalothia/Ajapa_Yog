package com.eschool.beans;

public class UserForAttendance {
User user;
boolean isPresent;
String hallNo;
String specificRequirements;
public UserForAttendance() {
}

public UserForAttendance(User user, boolean isPresent, String hallNo,String specificRequirements) {
	this.user = user;
	this.isPresent = isPresent;
	this.hallNo = hallNo;
	this.specificRequirements=specificRequirements;
}

public String getSpecificRequirements() {
	return specificRequirements;
}

public void setSpecificRequirements(String specificRequirements) {
	this.specificRequirements = specificRequirements;
}

public String getHallNo() {
	return hallNo;
}

public void setHallNo(String hallNo) {
	this.hallNo = hallNo;
}

public User getUser() {
	return user;
}
public void setUser(User user) {
	this.user = user;
}
public boolean isPresent() {
	return isPresent;
}
public void setPresent(boolean isPresent) {
	this.isPresent = isPresent;
}

}
