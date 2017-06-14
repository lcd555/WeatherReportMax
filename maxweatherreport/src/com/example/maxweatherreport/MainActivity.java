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
	// ��ǵ�ǰ�б�Ϊʡ��
    public static final int LEVEL_P = 0;
    // ��ǵ�ǰ�б�Ϊ����
    public static final int LEVEL_C = 1;
    // ��ǵ�ǰ�б�Ϊ��
    public static final int LEVEL_COUNTY = 2;
   
    // ������
    private TextView titleText;
    // �����б�
    private ListView listView;
    // �б�����
    private ArrayAdapter<String> adapter;
    // ���ݿ�
    private OperateDB weatherDB;

    private List<String> dataList;

    private List<Province> provinceList;

    private List<City> cityList;

    private List<County> countyList;
    //ѡ���ʡ��
    private Province selectedProvince;
    //ѡ��ĳ���
    private City selectedCity;
    //��ǰѡ����б�����
    private int currentLevel;
    //����Ƿ��WeatherActivity��ת������
    private boolean isFromWeatherActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra("ChooseArea", false);
        SharedPreferences sharedPreferences = getSharedPreferences("Weather", Context.MODE_PRIVATE);

        // ���country��ѡ���ұ�Activity���Ǵ������������������ģ���ֱ����ת��WeatherActivity

        if (!TextUtils.isEmpty(sharedPreferences.getString("CountyName", "")) && !isFromWeatherActivity) {
            Intent intent = new Intent(this, Weather.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
      
       
        listView = (ListView) findViewById(R.id.listview);
        titleText = (TextView) findViewById(R.id.title);
        //titleText.setText("�й�");
        dataList = new ArrayList<String>();
        //itemΪandroid�Դ�
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
                    //����������б�ʱ��������Intent��ת��������Ϣ����
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

   //��ѯʡ
    private void queryProvinces() {
    	titleText.setText("�й�");
        provinceList = weatherDB.getAllProvince();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getpName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("�й�");
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
        // code��Ϊ��
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        HttpUtil.sendHttpRequest(address,  new unusual(){

			@Override
			public void onFinish(String response) {
				//String response="01|����,02|�Ϻ�,03|���,04|����,05|������,06|����,07|����,08|���ɹ�,09|�ӱ�,10|ɽ��,11|����,12|ɽ��,13|�½�,14|����,15|�ຣ,16|����,17|����,18|����,19|����,20|����,21|�㽭,22|����,23|����,24|����,25|����,26|����,27|�Ĵ�,28|�㶫,29|����,30|����,31|����,32|���,33|����,34|̨��";
				
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
                        Toast.makeText(MainActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
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
