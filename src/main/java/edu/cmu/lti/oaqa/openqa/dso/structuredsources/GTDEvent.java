package edu.cmu.lti.oaqa.openqa.dso.structuredsources;
public class GTDEvent {

	private String id;
	private int year;
	private int month;
	private int day;
	private String country;
	private String region;
	private String province;
	private String city;
	private String location_desc;
	private String summary;
	private int multiple_incidents;
	private String attack_type;
	private String target;
	private String weapon_type;
	private String perpetrators;
	private int numkilled;
	private int numwounded;

	public GTDEvent(String id, int year, int month, int day, String country,
			String region, String province, String city, String location_desc,
			String summary, int multiple_incidents, String attack_type,
			String target, String weapon_type, String perpetrators,
			int numkilled, int numwounded) {
		super();
		this.id = id;
		this.year = year;
		this.month = month;
		this.day = day;
		this.country = country;
		this.region = region;
		this.province = province;
		this.city = city;
		this.location_desc = location_desc;
		this.summary = summary;
		this.multiple_incidents = multiple_incidents;
		this.attack_type = attack_type;
		this.target = target;
		this.weapon_type = weapon_type;
		this.perpetrators = perpetrators;
		this.numkilled = numkilled;
		this.numwounded = numwounded;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getLocation_desc() {
		return location_desc;
	}

	public void setLocation_desc(String location_desc) {
		this.location_desc = location_desc;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public int getMultiple_incidents() {
		return multiple_incidents;
	}

	public void setMultiple_incidents(int multiple_incidents) {
		this.multiple_incidents = multiple_incidents;
	}

	public String getAttack_type() {
		return attack_type;
	}

	public void setAttack_type(String attack_type) {
		this.attack_type = attack_type;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getWeapon_type() {
		return weapon_type;
	}

	public void setWeapon_type(String weapon_type) {
		this.weapon_type = weapon_type;
	}

	public String getPerpetrators() {
		return perpetrators;
	}

	public void setPerpetrators(String perpetrators) {
		this.perpetrators = perpetrators;
	}

	public int getNumkilled() {
		return numkilled;
	}

	public void setNumkilled(int numkilled) {
		this.numkilled = numkilled;
	}

	public int getNumwounded() {
		return numwounded;
	}

	public void setNumwounded(int numwounded) {
		this.numwounded = numwounded;
	}

}
