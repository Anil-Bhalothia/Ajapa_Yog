package com.eschool.beans;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="countries")
public class Countries {	
	@Id
	int id;
	String sortname;
	String name;
	int phonecode;	
	public Countries() {
	}
	public Countries(int id, String sortname, String name, int phonecode) {
		this.id = id;
		this.sortname = sortname;
		this.name = name;
		this.phonecode = phonecode;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSortname() {
		return sortname;
	}
	public void setSortname(String sortname) {
		this.sortname = sortname;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPhonecode() {
		return phonecode;
	}
	public void setPhonecode(int phonecode) {
		this.phonecode = phonecode;
	}
}
