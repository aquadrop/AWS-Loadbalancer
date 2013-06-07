package ece1779.ec2.models;

import java.util.HashMap;

public class Image {

	private int id;
	
	private int userId;
	
	private HashMap<String, String> keys;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public HashMap<String, String> getKeys() {
		return keys;
	}
	
	public void setKeys(HashMap<String, String> keys) {
		this.keys = keys;
	}
	
}
