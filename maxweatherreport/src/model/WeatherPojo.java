package model;

public class WeatherPojo {
    private String time;
    private String temp;
    private String wind;
    private String condition;
    public WeatherPojo(String time,String temp,String wind,String condition){
    	this.time=time;
    	this.wind=wind;
    	this.temp=temp;
    	this.condition=condition;
    	
    }
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	public String getWind() {
		return wind;
	}
	public void setWind(String wind) {
		this.wind = wind;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
}
