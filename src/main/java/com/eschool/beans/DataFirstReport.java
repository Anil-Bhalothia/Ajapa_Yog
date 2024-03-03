package com.eschool.beans;

public class DataFirstReport {
String date;
int familyCount,maleCount,femaleCount,kidsCount,seniorCount;
public String getDate() {
	return date;
}
public void setDate(String date) {
	this.date = date;
}
public int getFamilyCount() {
	return familyCount;
}
public void setFamilyCount(int familyCount) {
	this.familyCount = familyCount;
}
public int getMaleCount() {
	return maleCount;
}
public void setMaleCount(int maleCount) {
	this.maleCount = maleCount;
}
public int getFemaleCount() {
	return femaleCount;
}
public void setFemaleCount(int femaleCount) {
	this.femaleCount = femaleCount;
}
public int getKidsCount() {
	return kidsCount;
}
public void setKidsCount(int kidsCount) {
	this.kidsCount = kidsCount;
}
public int getSeniorCount() {
	return seniorCount;
}
public void setSeniorCount(int seniorCount) {
	this.seniorCount = seniorCount;
}
public DataFirstReport(String date, int familyCount, int maleCount, int femaleCount, int kidsCount, int seniorCount) {
	this.date = date;
	this.familyCount = familyCount;
	this.maleCount = maleCount;
	this.femaleCount = femaleCount;
	this.kidsCount = kidsCount;
	this.seniorCount = seniorCount;
}
public DataFirstReport() {
}

}
