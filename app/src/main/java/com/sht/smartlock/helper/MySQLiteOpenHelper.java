package com.sht.smartlock.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
	private final static String DBNAME = "db_words.db";
	public final static int VERSION = 2;
	public SQLiteDatabase db = null;
	private static MySQLiteOpenHelper instance;

	public MySQLiteOpenHelper(Context context) {
		super(context, DBNAME, null, VERSION);
		db = this.getReadableDatabase();
	}
	public static MySQLiteOpenHelper getInstance(Context context) {
		if (instance == null) {
			instance = new MySQLiteOpenHelper(context.getApplicationContext());
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 表一：存订餐订单(已选购数据)
		db.execSQL("CREATE TABLE IF NOT EXISTS tb_words(_id INTEGER PRIMARY KEY AUTOINCREMENT,name,price,num,ID_item,provider_id,content,unit,thumbnail，brief，start_time，end_time，hotel_price)");
//		// 表二： 存购物订单(已选购数据)
		db.execSQL("CREATE TABLE IF NOT EXISTS shopping_words(_id INTEGER PRIMARY KEY AUTOINCREMENT,name,price,num,ID_item,provider_id,content,unit,thumbnail，brief，start_time，end_time，hotel_price)");
		//存储订餐，分类数据
		db.execSQL("create table if not exists productType(_id integer primary key autoincrement , ID , caption)");
		//存储 分类对应的菜列表数据
		db.execSQL("create table if not exists product(_id integer primary key autoincrement , ID , provider_id,caption,content,price,unit,thumbnail,brief,start_time,end_time,hotel_price)");

		//环信账号 存储 头像和 nickName
		db.execSQL("create table if not exists huanxinuser(_id integer primary key autoincrement , username , nick,img_Num)");

		//购物订餐全部josn数据
		db.execSQL("create table if not exists ordering_ordering(_id integer primary key autoincrement , json , roomid)");
		//购物
		db.execSQL("create table if not exists ordering_Shopping(_id integer primary key autoincrement , json , roomid)");
        //发票抬头
		db.execSQL("create table if not exists InvioceTitle(_id integer primary key autoincrement , json , roomid)");
		//未读消息
		db.execSQL("create table if not exists NUNoMSG(_id integer primary key autoincrement , service_type , room_servicer_id ,msgnum)");
	}

	public SQLiteDatabase getSQLiteDatabase() {
		if (db != null) {
			return db;
		}
		return null;
	}
	//更新数据库
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			db.execSQL("DROP TABLE IF EXISTS tb_words");
//			db.execSQL("drop table if exists default_words");
			onCreate(db);
		}
	}
	//更新购物表
	public void onUpgrade_DefaultDB(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
//			db.execSQL("DROP TABLE IF EXISTS tb_words");
			db.execSQL("drop table if exists shopping_words");
			onCreate(db);
		}
	}

	//更新订餐列表数据
	public void onUpgrade_productTypeDB(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
//			db.execSQL("DROP TABLE IF EXISTS tb_words");
			db.execSQL("drop table if exists productType");
			db.execSQL("drop table if exists product");
			onCreate(db);
		}
	}
	//更新聊天室用户 头像 ，nickName表
	public void onUpgrade_huanxinuserDB(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
//			db.execSQL("DROP TABLE IF EXISTS tb_words");
			db.execSQL("drop table if exists huanxinuser");

			onCreate(db);
		}
	}
	//更新订餐数据json（）
	public void onUpgrade_ordering_orderingDB(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
//			db.execSQL("DROP TABLE IF EXISTS tb_words");
			db.execSQL("drop table if exists ordering_ordering");

			onCreate(db);
		}
	}
	//更新购物json数据（全部数据）
	public void onUpgrade_ordering_ShoppingDB(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
//			db.execSQL("DROP TABLE IF EXISTS tb_words");
			db.execSQL("drop table if exists ordering_Shopping");

			onCreate(db);
		}
	}//更新服务
	public void onUpgrade_ServiceTpye_DB(SQLiteDatabase db) {
		db.execSQL("drop table if exists NUNoMSG");
		onCreate(db);
	}

	public Cursor selectCursor(String sql, String[] selectionArgs) {
		return db.rawQuery(sql, selectionArgs);
	}

	public int selectCount(String sql, String[] selectionArgs) {
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		int result = cursor.getCount();
		if (cursor != null) {
			cursor.close();
		}
		return result;
	}

	/**
	 * @作用：执行带占位符的update、insert、delete语句，更新数据库，返回true或false
	 * @param sql
	 * @param bindArgs
	 * @return boolean
	 */
	public boolean updateData(String sql, String[] bindArgs) {
		boolean flag = false;
		try {
			db.execSQL(sql, bindArgs);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * @作用：执行带占位符的update、insert、delete语句，更新数据库，返回true或false
	 * @param sql
	 * @param // bindArgs
	 * @return boolean
	 */
	public boolean delete(String sql) {
		boolean flag = false;
		try {
			db.execSQL(sql);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}


	public List<Map<String, Object>> selectList(String sql,
			String[] selectionArgs) {
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		return cursorToList(cursor);
	}

	public List<Map<String, Object>> cursorToList(Cursor cursor) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while (cursor.moveToNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				// 1、比较简洁的做法：
				// map.put(cursor.getColumnName(i), cursor.getString(i));
				// 2、比较完善的做法：
				Object myValue = null;
				switch (cursor.getType(i)) {
				case 1:
					myValue = cursor.getLong(i);
					break;
				case 2:
					myValue = cursor.getDouble(i);
					break;
				case 3:
					myValue = cursor.getString(i);
					break;
				default:
					myValue = cursor.getBlob(i);
					break;
				}
				map.put(cursor.getColumnName(i), myValue);
			}
			list.add(map);
		}
		return list;
	}

	public boolean execData(String sql, Object[] bindArgs) {
		try {
			db.execSQL(sql, bindArgs);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void destroy() {
		if (db != null) {
			db.close();
		}

	}

}
