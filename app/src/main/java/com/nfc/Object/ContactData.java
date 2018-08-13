package com.nfc.Object;

public class ContactData {

	private String name, phone, email, emailType;

	public ContactData(String name_,String email_, String emailType, String phone_) {
		// TODO Auto-generated constructor stub
		name = name_;
		phone = phone_;
		email = email_;
		this.emailType = emailType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getEmailType() {
		return emailType;
	}

	public void setEmailType(String emailType) {
		this.emailType = emailType;
	}
}
