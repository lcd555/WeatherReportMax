package com.example.maxweatherreport;


import gson.JsonUtil;

import java.util.ArrayList;
import java.util.List;

import util.HttpUtil;
import util.unusual;

import model.City;
import model.County;
import model.Province;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import dao.OperateDB;

public class MainActivity extends Activity {
	// 标记当前列表为省份
    public static final int LEVEL_P = 0;
    // 标记当前列表为城市
    public static final int LEVEL_C = 1;
    // 标记当前列表为县
    public static final int LEVEL_COUNTY = 2;
   
    // 标题栏
    private TextView titleText;
    // 数据列表
    private ListView listView;
    // 列表数据
    private ArrayAdapter<String> adapter;
    // 数据库
    private OperateDB weatherDB;

    private List<String> dataList;

    private List<Province> provinceList;

    private List<City> cityList;

    private List<County> countyList;
    //选择的省份
    private Province selectedProvince;
    //选择的城市
    private City selectedCity;
    //当前选择的列表类型
    private int currentLevel;
    //标记是否从WeatherActivity跳转而来的
    private boolean isFromWeatherActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra("ChooseArea", false);
        SharedPreferences sharedPreferences = getSharedPreferences("Weather", Context.MODE_PRIVATE);

        // 如果country已选择且本Activity不是从天气界面启动而来的，则直接跳转到WeatherActivity

        if (!TextUtils.isEmpty(sharedPreferences.getString("CountyName", "")) && !isFromWeatherActivity) {
            Intent intent = new Intent(this, Weather.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
      
       
        listView = (ListView) findViewById(R.id.listview);
        titleText = (TextView) findViewById(R.id.title);
        //titleText.setText("中国");
        dataList = new ArrayList<String>();
        //item为android自带
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        weatherDB = OperateDB.getOperateDb(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                if (currentLevel == 0) {
                    selectedProvince = provinceList.get(index);
                    queryCities();
                } else if (currentLevel == 1) {
                    selectedCity = cityList.get(index);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    //当点击到县列表时，就利用Intent跳转到天气信息界面
                    String countyName = countyList.get(index).getCountyName();
                    Intent intent = new Intent(MainActivity.this, Weather.class);
                    intent.putExtra("CountyName", countyName);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();
    
        
    }

   //查询省
    private void queryProvinces() {
    	titleText.setText("中国");
        provinceList = weatherDB.getAllProvince();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getpName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = 0;
           
        } else {
            queryFromServer(null, "province");
        }
    }
    
    
    
    private void queryCities() {
        
        cityList = weatherDB.getAllCity(selectedProvince.getpId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getcName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getpName());
            currentLevel =1;
            
        } else {
            queryFromServer(selectedProvince.getpId(), "city");
        }
    }

    
    private void queryCounties(){
        
        countyList = weatherDB.getAllCountry(selectedCity.getcId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getcName());
            currentLevel = LEVEL_COUNTY;
            
        } else {
            queryFromServer(selectedCity.getcId(), "county");
        }
    }

    private void queryFromServer(final String code, final String type) {
        String address;
        // code不为空
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        HttpUtil.sendHttpRequest(address,  new unusual(){

			@Override
			public void onFinish(String response) {
				//String response="01|北京,02|上海,03|天津,04|重庆,05|黑龙江,06|吉林,07|辽宁,08|内蒙古,09|河北,10|山西,11|陕西,12|山东,13|新疆,14|西藏,15|青海,16|甘肃,17|宁夏,18|河南,19|江苏,20|湖北,21|浙江,22|安徽,23|福建,24|江西,25|湖南,26|贵州,27|四川,28|广东,29|云南,30|广西,31|海南,32|香港,33|澳门,34|台湾";
				
				 boolean result = false;
			        
			        if ("province".equals(type)) {
			            result = JsonUtil.saveProvincesResponse(weatherDB, response);
			        } else if ("city".equals(type)) {
			            result = JsonUtil.saveCitiesResponse(weatherDB, response, selectedProvince.getpId());
			        } else if ("county".equals(type)) {
			            result = JsonUtil.saveCountiesResponse(weatherDB, response, selectedCity.getcId());
			        }
			        if (result) {
			           new Thread(new Runnable() {
			                @Override
			                public void run() {
			                    if ("province".equals(type)) {
			                        queryProvinces();
			                    } else if ("city".equals(type)) {
			                        queryCities();
			                    } else if ("county".equals(type)) {
			                        queryCounties();
			                    }
			                }
			            });
			        }
				
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
			});
       
    }
    
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == 1) {
            queryProvinces();
        } else {
            if (isFromWeatherActivity) {
                Intent intent = new Intent(this, Weather.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
