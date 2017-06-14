package dao;

import java.util.ArrayList;
import java.util.List;

import model.City;
import model.County;
import model.Province;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class OperateDB {
	   private SQLiteDatabase database;
	   private static OperateDB operateDB; 
	   
	   
	   public  OperateDB(Context context){
		   PCCDb dBH = new PCCDb(context,
	                "lcd", null, 1);
	        database = dBH.getWritableDatabase();
		   
	   }
	   public static OperateDB getOperateDb(Context context){
		  if(operateDB == null) {
	            operateDB = new OperateDB(context);
	        }
	        return operateDB;
	   }
	   
	   
	 //保存省信息
	    public void saveProvinces(List<Province> provinceList) {
	        if (provinceList != null && provinceList.size() > 0) {
	            ContentValues values = new ContentValues();
	            for (int i = 0; i < provinceList.size(); i++) {
	                values.put("provinceName", provinceList.get(i).getpName());
	                values.put("provinceId", provinceList.get(i).getpId());
	                database.insert("Province", null, values);
	                values.clear();
	            }
	        }
	    }

	    //保存城信息
	    public void saveCities(List<City> cityList) {
	        if (cityList != null && cityList.size() > 0) {
	            ContentValues values = new ContentValues();
	            for (int i = 0; i < cityList.size(); i++) {
	                values.put("cityName", cityList.get(i).getcName());
	                values.put("cityId", cityList.get(i).getcId());
	                values.put("provinceId", cityList.get(i).getpId());
	                database.insert("City", null, values);
	                values.clear();
	            }
	        }
	    }

	    
	    
	    
	    
	    
	    //保存县信息
	    public void saveCounties(List<County> countyList) {
	        if (countyList != null && countyList.size() > 0) {
	            ContentValues values = new ContentValues();
	            for (int i = 0; i < countyList.size(); i++) {
	                values.put("countyName", countyList.get(i).getCountyName());
	                values.put("countyId", countyList.get(i).getCountyId());
	                values.put("cityId", countyList.get(i).getcId());
	                database.insert("County", null, values);
	                values.clear();
	            }
	        }
	    }
	    
	    //取省城县
	    public List<Province> getAllProvince() {
	        Cursor cursor = database.query("Province", null, null, null, null, null, null);
	        List<Province> list = new ArrayList<Province>();
	        Province province;
	        if (cursor.moveToFirst()) {
	            do {
	                province = new Province();
	                province.setpName(cursor.getString(cursor.getColumnIndex("provinceName")));
	                province.setpId(cursor.getString(cursor.getColumnIndex("provinceId")));
	                list.add(province);
	            } while (cursor.moveToNext());
	        }
	        return list;
	    }

	   
	    public List<City> getAllCity(String provinceId) {
	        List<City> list = new ArrayList<City>();
	        City city;
	        Cursor cursor = database.query("City", null, "provinceId = ?", new String[]{provinceId}, null, null, null);
	        if (cursor.moveToFirst()) {
	            do {
	                city = new City();
	                city.setcName(cursor.getString(cursor.getColumnIndex("cityName")));
	                city.setcId(cursor.getString(cursor.getColumnIndex("cityId")));
	                city.setpId(provinceId);
	                list.add(city);
	            } while (cursor.moveToNext());
	        }
	        return list;
	    }

	    
	    public List<County> getAllCountry(String cityId) {
	        List<County> list = new ArrayList<County>();
	        Cursor cursor = database.query("County", null, "cityId=?", new String[]{cityId}, null, null, null);
	        County county;
	        if (cursor.moveToFirst()) {
	            do {
	                county = new County();
	                county.setCountyName(cursor.getString(cursor.getColumnIndex("countyName")));
	                county.setCountyId(cursor.getString(cursor.getColumnIndex("countyId")));
	                county.setcId(cityId);
	                list.add(county);
	            } while (cursor.moveToNext());
	        }
	        return list;
	    }
	    
	    
}
