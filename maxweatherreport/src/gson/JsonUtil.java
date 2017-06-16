package gson;

import java.util.ArrayList;
import java.util.List;

import model.City;
import model.County;
import model.Province;
import model.WeatherPojo;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.maxweatherreport.Weather;

import dao.OperateDB;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class JsonUtil {

	// ������������ص�ʡ������
    public static boolean saveProvincesResponse(OperateDB weatherDB, String response){
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                Province province;
                List<Province> provinceList = new ArrayList<Province>();
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    province = new Province();
                    province.setpId(array[0]);
                    province.setpName(array[1]);
                    provinceList.add(province);
                }
                weatherDB.saveProvinces(provinceList);
                return true;
            }
        }
        return false;
    }

    // ������������ص��м�����
    public static boolean saveCitiesResponse(OperateDB weatherDB, String response, String provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                City city;
                List<City> cityList = new ArrayList<City>();
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    city = new City();
                    city.setcId(array[0]);
                    city.setcName(array[1]);
                    city.setpId(provinceId);
                    cityList.add(city);
                }
                weatherDB.saveCities(cityList);
                return true;
            }
        }
        return false;
    }

    // ������������ص��ؼ�����
    public static boolean saveCountiesResponse(OperateDB weatherDB, String response, String cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                County county;
                List<County> countyList = new ArrayList<County>();
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    county = new County();
                    county.setCountyId(array[0]);
                    county.setCountyName(array[1]);
                    county.setcId(cityId);
                    countyList.add(county);
                }
                weatherDB.saveCounties(countyList);
                return true;
            }
        }
        return false;
    }

    // ������������ص�json����
    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonobject = new JSONObject(response);
            JSONArray title = jsonobject.getJSONArray("HeWeather data service 3.0");
            JSONObject first_object = (JSONObject) title.get(0);

            JSONObject basic = (JSONObject) first_object.get("basic");

            //����ʱ��
            JSONObject update = (JSONObject) basic.get("update");
            JSONArray daily_forecast = (JSONArray) first_object.get("daily_forecast");
            JSONObject daily_forecast_first = (JSONObject) daily_forecast.get(0);
            JSONObject cond = (JSONObject) daily_forecast_first.get("cond");
            //�¶�
            JSONObject temp = (JSONObject) daily_forecast_first.get("tmp");

            JSONObject astro = (JSONObject) daily_forecast_first.get("astro");

            JSONObject wind = (JSONObject) daily_forecast_first.get("wind");

            JSONArray hourly_forecast = (JSONArray) first_object.get("hourly_forecast");

            Weather.weatherList.clear();

            for (int i = 0; i < hourly_forecast.length(); i++) {
                JSONObject json = hourly_forecast.getJSONObject(i);
                JSONObject json_wind = (JSONObject) json.get("wind");
                String date = json.getString("date");
                String[] array = date.split(" ");
                String dir = json_wind.getString("dir");
                String sc = json_wind.getString("sc");
                String hourly_clock = array[1];
                String temp1 = "�¶ȣ�" + json.getString("tmp") + "��";
                String hourly_pop = "��ˮ���ʣ�" + json.getString("pop");
                String wind1 = "������" + dir + " " + sc + "��";
                WeatherPojo weather = new WeatherPojo(hourly_clock, temp1, wind1,hourly_pop );
                Weather.weatherList.add(weather);
            }
            //�ճ�
            String sunriseTime = astro.getString("sr");
            //����
            String sunsetTime = astro.getString("ss");
            //��������
            String dayWeather = cond.getString("txt_d");
            //ҹ������
            String nightWeather = cond.getString("txt_n");
            //����
            String windText = wind.getString("dir") + " " + wind.getString("sc") + "��";
            //��ˮ����
            String pop = daily_forecast_first.getString("pop");
            //�¶�
            String tempText1 = temp.getString("min") + "��";
            	String tempText2=temp.getString("max") + "��";
            //����ʱ��
            String updateTime = update.getString("loc");
            //������
            String cityName = basic.getString("city");
            saveWeatherInfo(context, cityName, sunriseTime, sunsetTime, dayWeather, nightWeather, windText, pop, tempText1,tempText2, updateTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveWeatherInfo(Context context, String cityName,
                                        String sunriseTime, String sunsetTime, String dayWeather, String nightWeather,
                                        String windText, String pop, String tempText1,String tempText2, String updateTime) {
        SharedPreferences.Editor editor = context.getSharedPreferences("Weather1", Context.MODE_PRIVATE).edit();
        editor.putString("cityName", cityName);
        editor.putString("sunriseTime", sunriseTime);
        editor.putString("sunsetTime", sunsetTime);
        editor.putString("dayWeather", dayWeather);
        editor.putString("nightWeather", nightWeather);
        editor.putString("wind", windText);
        editor.putString("pop", pop);
        editor.putString("temp1", tempText1);
        editor.putString("temp2", tempText2);
        editor.putString("updateTime", updateTime);
        editor.commit();
    }
	
	
	
	
}
