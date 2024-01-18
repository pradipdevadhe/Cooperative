package com.cooperative.model;

public class AadharData {
	private String client_id;
	private String age_range;
	private String aadhaar_number;
	private String state;
	private String gender;
	private String last_digits;
	private String is_mobile;
	private String remarks;
	private String less_info;
	

	public AadharData(String aadhaar_number, String is_mobile, String remarks) {
		super();
		this.aadhaar_number = aadhaar_number;
		this.is_mobile = is_mobile;
		this.remarks = remarks;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getAge_range() {
		return age_range;
	}

	public void setAge_range(String age_range) {
		this.age_range = age_range;
	}

	public String getAadhaar_number() {
		return aadhaar_number;
	}

	public void setAadhaar_number(String aadhaar_number) {
		this.aadhaar_number = aadhaar_number;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getLast_digits() {
		return last_digits;
	}

	public void setLast_digits(String last_digits) {
		this.last_digits = last_digits;
	}

	public String getIs_mobile() {
		return is_mobile;
	}

	public void setIs_mobile(String is_mobile) {
		this.is_mobile = is_mobile;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getLess_info() {
		return less_info;
	}

	public void setLess_info(String less_info) {
		this.less_info = less_info;
	}

}