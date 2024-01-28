package com.eschool.beans;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="cities")
public class Cities {	
	@Id
	int id;	
	String name;
	int stateId;
public Cities(int id, String name, int stateId) {
		this.id = id;
		this.name = name;
		this.stateId = stateId;
	}
	public Cities() {
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
	public int getStateId() {
		return stateId;
	}
	public void setState_id(int stateId) {
		this.stateId = stateId;
	}
}
