package com.example.AndroidMVC;

// This class implements a house object.
// It consists of constructors, getter and setter methods

public class House_Advert {
	
	private String name;
	private String description;
	private double longitude;
	private double latitude;
	private int marked;
	private String image;
	private String couples;
	private String roomType;
	private String datePosted;
	private String dateAvailable;
	private String area;
	private String price;
	String id;
	private String sellerType;
	
	
	public House_Advert(){}
	
	public House_Advert(String n, String d, double lo, double la, int m, String idd){
		name = n;
		description = d;
		longitude = lo;
		latitude = la;
		marked = m;
		id = idd;
	}
	
	public House_Advert(String name, String sellerType, String id, String description, String image, String couples, String roomType, String datePosted, String dateAvailable, String area, String price){
		this.name = name;
		this.description = description;
		this.image = image;
		this.couples = couples;
		this.roomType = roomType;
		this.datePosted = datePosted;
		this.dateAvailable = dateAvailable;
		this.area = area;
		this.price = price;
		this.id = id;
		this.sellerType = sellerType;
	}
	
	public House_Advert(String name, String id, String image, String couples, String roomType, String datePosted, String price, int marked){
		this.name = name;
		this.image = image;
		this.couples = couples;
		this.roomType = roomType;
		this.datePosted = datePosted;
		this.price = price;
		this.id = id;
		this.marked = marked;
	}
	
	public String getName(){
		return name;
	}
	
	public String getID(){
		return id;
	}
	
	public String getSellerType(){
		return sellerType;
	}
	
	public String getDescription(){
		return description;
	}
	
	public double getLongitude(){
		return longitude;
	}
	
	public double getLatitude(){
		return latitude;
	}
	
	public int getMarked(){
		return marked;
	}
	
	public void setName(String n){
		name = n;
	}
	
	public void setID(String idd){
		id = idd;
	}
	
	public void setSellerType(String se){
		sellerType = se;
	}
	
	public void setDescription(String d){
		description = d;
	}
	
	public void setLongitude(double lo){
		longitude = lo;
	}
	
	public void setLatitude(double la){
		latitude = la;
	}
	
	public void setMarked(int m){
		marked = m;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCouples() {
		return couples;
	}

	public void setCouples(String couples) {
		this.couples = couples;
	}

	public String getDatePosted() {
		return datePosted;
	}

	public void setDatePosted(String datePosted) {
		this.datePosted = datePosted;
	}

	public String getRoomType() {
		return roomType;
	}

	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}

	public String getDateAvailable() {
		return dateAvailable;
	}

	public void setDateAvailable(String dateCreated) {
		this.dateAvailable = dateCreated;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

}
