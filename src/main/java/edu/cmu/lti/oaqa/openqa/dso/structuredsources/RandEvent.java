package edu.cmu.lti.oaqa.openqa.dso.structuredsources;
	
public class RandEvent {
	
	private int id;
	private String date;
	private String city;
	private String country;
	private String perpetrator;
	private String weapon;
	private String injuries;
	private String fatalities;
	private String desc;
	
	public RandEvent(int id, String[] dataStrs) {
		super();
		this.id = id;
		this.date = dataStrs[0];
		this.city = dataStrs[1];
		this.country = dataStrs[2];
		this.perpetrator = dataStrs[3];
		this.weapon = dataStrs[4];
		this.injuries = dataStrs[5];
		this.fatalities = dataStrs[6];
		this.desc = dataStrs[7];
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPerpetrator() {
		return perpetrator;
	}
	public void setPerpetrator(String perpetrator) {
		this.perpetrator = perpetrator;
	}
	public String getWeapon() {
		return weapon;
	}
	public void setWeapon(String weapon) {
		this.weapon = weapon;
	}
	public String getInjuries() {
		return injuries;
	}
	public void setInjuries(String injuries) {
		this.injuries = injuries;
	}
	public String getFatalities() {
		return fatalities;
	}
	public void setFatalities(String fatalities) {
		this.fatalities = fatalities;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	

}
