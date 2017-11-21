package com.netxeon.lignthome.data;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.netxeon.lignthome.util.Logger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "shortcut";
	private static DBHelper mHelper;
	
	public static DBHelper getInstance(Context context){
		if(mHelper == null){
			mHelper = new DBHelper(context);
		}
		return mHelper;
	}
	
	private DBHelper(Context context) {
		// CursorFactory设置为null,使用默认值
		super(context, Data.DB_FILE, null, DATABASE_VERSION);
	}

	// 数据库第一次被创建时onCreate会被调用，然后创建2张表， 一张category ，一张componentName
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, category VARCHAR, componentName VARCHAR, persistent INTEGER)");
	}

	// 如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	//写，添加shortcut数据到数据库（查合适调用）
	public void insert(Shortcut shortcut) {
		ContentValues values = new ContentValues();
		values.put("category", shortcut.getCategory());
		values.put("componentName", shortcut.getComponentName());
		values.put("persistent", shortcut.isPersistent() ? 1 : 0);
		SQLiteDatabase db = getWritableDatabase();
		db.replace(TABLE_NAME, null, values);
		db.close();
	}

	//查到列表
	public List<Shortcut> queryByCategory(String category) {
		List<Shortcut> shortcutList = new ArrayList<Shortcut>();
		SQLiteDatabase db = getReadableDatabase();
		String[] selectionArgs = { category };
		if(db == null){
			Log.v("shortcut", "----db is null !!!!!!!");
		}
		//查数据库
		//table:表名，不能为null
		//columns:要查询的列名，可以是多个，可以为null，表示查询所有列
		//selection:查询条件，比如id=? and name=? 可以为null
		//selectionArgs:对查询条件赋值，一个问号对应一个值，按顺序 可以为null
		//having:语法have，可以为null
		//orderBy：语法，按xx排序，可以为null
		Cursor cursor = db.query(TABLE_NAME, null, "category=?", selectionArgs, null, null, null);
		if (!cursor.moveToFirst()) {
			Log.v("temp", "----there is no data !!!!!!!");
		} else {
			do {
				Shortcut shotcut = new Shortcut();
				//get到数据，设置数据
				shotcut.setCategory(cursor.getString(cursor.getColumnIndex("category")));
				shotcut.setComponentName((cursor.getString(cursor.getColumnIndex("componentName"))));
				shotcut.setPersistent(cursor.getInt(cursor.getColumnIndex("persistent")) == 1);
				//把有数据的对象添加到集合
				shortcutList.add(shotcut);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return shortcutList;
	}

	public List<Shortcut> queryAllPersistents() {
		List<Shortcut> shortcutList = new ArrayList<Shortcut>();
		SQLiteDatabase db = getReadableDatabase();
		String[] selectionArgs = { String.valueOf(1) };
		Cursor cursor = db.query(TABLE_NAME, null, "persistent=?", selectionArgs, null, null, null);
		if (!cursor.moveToFirst()) {
			Log.v("temp", "----there is no data !!!!!!!");
		} else {
			do {
				Shortcut shotcut = new Shortcut();
				shotcut.setCategory(cursor.getString(cursor.getColumnIndex("category")));
				shotcut.setComponentName((cursor.getString(cursor.getColumnIndex("packageName"))));
				shotcut.setPersistent(cursor.getInt(cursor.getColumnIndex("persistent")) == 1);
				shortcutList.add(shotcut);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return shortcutList;
	}

	public void deleteShortcut(String componentName, String category) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_NAME, "componentName=? and category=?", new String[] { componentName, category });
		db.close();
	}
	
	//delete the shortcut while the package has removed
	public void deleteByPackageName(String packageName) {
		SQLiteDatabase db = getWritableDatabase();
		//db.delete(TABLE_NAME, "componentName =?", new String[] { packageName});
		Logger.log(Logger.TAG_PACKAGE, "DBhelper.deleteByPackageName() : " + packageName);
		db.execSQL("DELETE FROM shortcut WHERE componentName LIKE '%" + packageName + "%'");
		db.close();
	}

	// 把图片转换成字节数组以便存储在数据库中
	private byte[] bitmap2bytes(Bitmap bitmap) {
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
		return os.toByteArray();
	}

	// 把图片转换成字节数组以便存储在数据库中
	private Bitmap bytes2Bitmap(byte[] bytes) {
		Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return bmp;
	}
}