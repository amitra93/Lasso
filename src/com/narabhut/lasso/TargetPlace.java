package com.narabhut.lasso;

public class TargetPlace {

	private double latitude;
	private double longitude;
	private String name;
	private String formattedAddress;
	private String formattedPhoneNumber;
	private double averageRating;
	private String imageURL;
	
	public TargetPlace(double latitude, double longitude, String name, String formattedAddress, String formattedPhoneNumber, double averageRating, String imageURL){
		this.latitude = latitude;
		this.longitude = longitude;
		this.name = name;
		this.formattedAddress = formattedAddress;
		this.formattedPhoneNumber = formattedPhoneNumber;
		this.averageRating = averageRating;
		this.imageURL = imageURL;
	}
	
	public double getLatitude(){
		return latitude;
	}
	
	public double getLongitude(){
		return longitude;
	}
	
	public String getName(){
		return name;
	}
	
	public String getFormattedAddress(){
		return formattedAddress;
	}
	
	public String getFormattedPhoneNumber(){
		return formattedPhoneNumber;
	}
	
	public double getAverageRating(){
		return averageRating;
	}
	
	public String getImageURL(){
		return imageURL;
	}
}
