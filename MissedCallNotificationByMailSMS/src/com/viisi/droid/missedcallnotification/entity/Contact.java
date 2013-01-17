package com.viisi.droid.missedcallnotification.entity;

import java.util.ArrayList;
import java.util.List;

public class Contact implements IEntity {

	private Long id;
	private String name;
	private List<String> phones;

	public Contact(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Contact() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getPhones() {
		if (phones == null) {
			phones = new ArrayList<String>();
		}
		return phones;
	}

	public void setPhones(List<String> phones) {
		this.phones = phones;
	}

}
