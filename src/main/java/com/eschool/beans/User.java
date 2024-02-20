package com.eschool.beans;

import java.sql.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name="user")
public class User {	
	@Id
	@GeneratedValue
	private int id;
	private String name;
	private String gender;
	private String dob;
	private String countryCode;
	private String mobileNumber;
	private String email;
	private String password;
	private String country;
	private String state;
	private String city;
	private boolean isDisciple;
	private String whatsAppNumber;
	private String bloodGroup;
	private String occupation;
	private String qualification;
	private String addressLine;
	private String dikshaDate;
	private String pinCode;
	private String status="Pending";    
	private String role;    
	private int familyId;
	private String profileImage;
	public String getProfileImage() {
		return profileImage;
	}
	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public boolean getIsDisciple() {
		return isDisciple;
	}
	public void setIsDisciple(boolean isDisciple) {
		this.isDisciple = isDisciple;
	}
	public String getWhatsAppNumber() {
		return whatsAppNumber;
	}
	public void setWhatsAppNumber(String whatsAppNumber) {
		this.whatsAppNumber = whatsAppNumber;
	}
	public String getBloodGroup() {
		return bloodGroup;
	}
	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}
	public String getOccupation() {
		return occupation;
	}
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}
	public String getQualification() {
		return qualification;
	}
	public void setQualification(String qualification) {
		this.qualification = qualification;
	}
	public String getAddressLine() {
		return addressLine;
	}
	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}
	public String getDikshaDate() {
		return dikshaDate;
	}
	public void setDikshaDate(String dikshaDate) {
		this.dikshaDate = dikshaDate;
	}
	public String getPinCode() {
		return pinCode;
	}
	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public int getFamilyId() {
		return familyId;
	}
	public void setFamilyId(int familyId) {
		this.familyId = familyId;
	}
	public User(int id, String name, String gender, String dob, String countryCode, String mobileNumber, String email,
			String password, String country, String state, String city, boolean isDisciple, String whatsAppNumber,
			String bloodGroup, String occupation, String qualification, String addressLine, String dikshaDate,
			String pinCode, String status, String role, int familyId,String profileImage) {
		this.id = id;
		this.name = name;
		this.gender = gender;
		this.dob = dob;
		this.countryCode = countryCode;
		this.mobileNumber = mobileNumber;
		this.email = email;
		this.password = password;
		this.country = country;
		this.state = state;
		this.city = city;
		this.isDisciple = isDisciple;
		this.whatsAppNumber = whatsAppNumber;
		this.bloodGroup = bloodGroup;
		this.occupation = occupation;
		this.qualification = qualification;
		this.addressLine = addressLine;
		this.dikshaDate = dikshaDate;
		this.pinCode = pinCode;
		this.status = status;
		this.role = role;
		this.familyId = familyId;
		this.profileImage=profileImage;
	}
	public User() {
	
	}
	
}
