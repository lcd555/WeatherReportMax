package com.example.maxweatherreport;



import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import model.WeatherPojo;

import gson.JsonUtil;
import util.HttpUtil;
import util.unusual;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Weather extends Activity {

	TextView cName,temp1,temp2,time,wind,data;
	String cityName;
	SharedPreferences sp;
	public static List<WeatherPojo> weatherList = new ArrayList<WeatherPojo>();
	//还回选着地区按钮
	public void click(View v){
		Intent intent=new Intent(this,MainActivity.class);
		 intent.putExtra("ChooseArea", true);
		startActivity(intent);
	}
	public void push(View v){
		
		try {
			queryWeather(cityName);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather);
		cName=(TextView)findViewById(R.id.city_name);
		temp1=(TextView)findViewById(R.id.temp1);
		temp2=(TextView)findViewById(R.id.temp2);
		data=(TextView)findViewById(R.id.current_date);
		time=(TextView)findViewById(R.id.publish_time);
		sp=getSharedPreferences("Weather", Context.MODE_PRIVATE);
		
		cityName=sp.getString("CountyName","");
		//cityName=getIntent().getStringExtra("CountyName");
		cName.setText(cityName+"天气预报");
//		try {
//			queryWeather(cityName);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public void queryWeather(final String countyName) throws UnsupportedEncodingException{
		
		String url = "http://apis.baidu.com/heweather/weather/free?city=";
        String name = new String(countyName.getBytes("UTF-8"), "iso-8859-1");
        HttpUtil.sendHttpRequest(url + name, new unusual() {
            @Override
            public void onFinish(String response) {
                JsonUtil.handleWeatherResponse(Weather.this, response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWeather();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Weather.this, "同步失败", Toast.LENGTH_LONG).show();
                        
                    }
                });
            }
        });
		
	}
	
	public void showWeather(){
		sp=getSharedPreferences("Weather1", Context.MODE_PRIVATE);
		temp1.setText(sp.getString("temp1",""));
		temp2.setText(sp.getString("temp2",""));
		
		
	}
	
	
}
