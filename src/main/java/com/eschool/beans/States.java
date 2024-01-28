package com.eschool.beans;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="states")
public class States {

	@Id
	int id;
	String name;
	int countryId;
	
	
	public States() {
	}


	public States(int id, String name, int countryId) {
		this.id = id;
		this.name = name;
		this.countryId = countryId;
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


	public int getCountryId() {
		return countryId;
	}


	public void setCountry_id(int countryId) {
		this.countryId = countryId;
	}
	
	
	
}
