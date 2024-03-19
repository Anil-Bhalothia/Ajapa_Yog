package com.eschool.beans;

public class DataSecondReport {
String date,timeSlot,modeOfTransport,trainDetails;

public String getDate() {
	return date;
}

public void setDate(String date) {
	this.date = date;
}

public String getTimeSlot() {
	return timeSlot;
}

public void setTimeSlot(String timeSlot) {
	this.timeSlot = timeSlot;
}

public String getModeOfTransport() {
	return modeOfTransport;
}

public void setModeOfTransport(String modeOfTransport) {
	this.modeOfTransport = modeOfTransport;
}

public String getTrainDetails() {
	return trainDetails;
}

public void setTrainDetails(String trainDetails) {
	this.trainDetails = trainDetails;
}

public DataSecondReport(String date, String timeSlot, String modeOfTransport, String trainDetails) {
	
	this.date = date;
	this.timeSlot = timeSlot;
	this.modeOfTransport = modeOfTransport;
	this.trainDetails = trainDetails;
}

public DataSecondReport() {
	
}

}
