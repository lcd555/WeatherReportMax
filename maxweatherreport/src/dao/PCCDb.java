package dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class PCCDb extends SQLiteOpenHelper{

	public PCCDb(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table Province ("
	            + "provinceName text," + "provinceId text )");
		db.execSQL("create table City("
	            + "cityName text," + "cityId text," + "provinceId text)");
		db.execSQL("create table County("
	            + "countyName text," + "countyId text," + "cityId text)");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}
