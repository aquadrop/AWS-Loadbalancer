package ece1779.ec2.models;

public class User {

	private int id;
	
	private String username;
	
	private int imageCount;

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public int getImageCount() {
		return imageCount;
	}
	
	public void setImageCount(int imageCount) {
		this.imageCount = imageCount;
	}
}
