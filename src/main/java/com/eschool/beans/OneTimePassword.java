package com.eschool.beans;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name="OneTimePassword")
public class OneTimePassword {
	@Id
	@GeneratedValue
	int id;
	String email,mobileNumber,countryCode,otp;
	public OneTimePassword() {
		
	}
	public OneTimePassword(int id, String email, String mobileNumber, String countryCode, String otp,
			long timeOTPGenerated) {
		
		this.id = id;
		this.email = email;
		this.mobileNumber = mobileNumber;
		this.countryCode = countryCode;
		this.otp = otp;
		this.timeOTPGenerated = timeOTPGenerated;
	}
	long timeOTPGenerated;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public long getTimeOTPGenerated() {
		return timeOTPGenerated;
	}
	public void setTimeOTPGenerated(long timeOTPGenerated) {
		this.timeOTPGenerated = timeOTPGenerated;
	}
	
}
