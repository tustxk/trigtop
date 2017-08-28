package com.trigtop.trigtop.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by k on 2017/8/28.
 */

public class DBHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DBFile= "trigtop.db";
	private static DBHelper mHelper;

	public static DBHelper getInstance(Context context){
		if(mHelper == null){
			mHelper = new DBHelper(context);
		}
		return mHelper;
	}

	private DBHelper(Context context) {
		super(context, DBFile, null, DATABASE_VERSION);
	}

	// 数据库第一次被创建时onCreate会被调用，然后创建2张表， 一张category ，一张componentName
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS user (id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,gender TEXT,age INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	/**
	 * 根据数组的列和值进行insert
	 * @param tableName
	 * @param columns
	 * @param values
	 * @return
	 */
	public boolean insert(String tableName,String[] columns,Object[] values){
		SQLiteDatabase db = getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		for (int rows = 0; rows < columns.length;++rows){
			ContentValuesPut(contentValues,columns[rows],values[rows]);
		}
		long rowId = db.insert(tableName,null,contentValues);
		db.close();
		return rowId!=-1;
	}

	/**
	 * 根据map来进行insert
	 * @param tableName
	 * @param columnValues
	 * @return
	 */
	public boolean insert(String tableName,Map<String,Object> columnValues){
		SQLiteDatabase db = getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		Iterator iterator = columnValues.keySet().iterator();
		while (iterator.hasNext()){
			String key = (String) iterator.next();
			this.ContentValuesPut(contentValues,key,columnValues.get(key));
		}
		long rowId = db.insert(tableName,null,contentValues);
		db.close();
		return rowId!=-1;
	}

	/**
	 * 根据数组条件进行delete
	 * @param tableName
	 * @param whereColumns
	 * @param whereParam
	 * @return
	 */
	public boolean delete(String tableName,String[] whereColumns,String[] whereParam){
		SQLiteDatabase db = getReadableDatabase();
		String whereStr = this.initWhereSqlFromArray(whereColumns);
		int rowNumber = db.delete(tableName,whereStr,whereParam);
		db.close();
		return rowNumber > 0;
	}

	/**
	 * 根据map来进行delete
	 * @param tableName
	 * @param whereParams
	 * @return
	 */
	public boolean delete(String tableName,Map<String,String> whereParams){
		SQLiteDatabase db = getReadableDatabase();
		Map map = this.initWhereSqlFromMap(whereParams);
		int rowNumber = db.delete(tableName,map.get("whereSql").toString(),(String[]) map.get("whereSqlParam"));
		db.close();
		return rowNumber > 0;
	}

	/**
	 * 根据数组条件来update
	 * @param tableName
	 * @param columns
	 * @param values
	 * @param whereColumns
	 * @param whereArgs
	 * @return
	 */
	public boolean update(String tableName,String[] columns,Object[] values,String[] whereColumns,String[] whereArgs){
		SQLiteDatabase db = getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		for (int i=0;i<columns.length;++i){
			this.ContentValuesPut(contentValues,columns[i],values[i]);
		}
		String whereClause = this.initWhereSqlFromArray(whereColumns);
		int rowNumber = db.update(tableName,contentValues,whereClause,whereArgs);
		db.close();
		return rowNumber > 0 ;
	}

	/**
	 * 根据map值来进行update
	 * @param tableName
	 * @param columnValues
	 * @param whereParam
	 * @return
	 */
	public boolean update(String tableName,Map<String,Object> columnValues,Map<String,String> whereParam){
		SQLiteDatabase db = getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		Iterator iterator = columnValues.keySet().iterator();

		String columns;
		while (iterator.hasNext()){
			columns = (String) iterator.next();
			ContentValuesPut(contentValues,columns,columnValues.get(columns));
		}

		Map map = this.initWhereSqlFromMap(whereParam);
		int rowNumber = db.update(tableName,contentValues,(String)map.get("whereSql"),(String[]) map.get("whereSqlParam"));
		db.close();
		return rowNumber > 0;
	}

	/**
	 * 查询返回List
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<Map> queryListMap(String sql, String[] params){
		ArrayList list = new ArrayList();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(sql,params);
		int columnCount = cursor.getColumnCount();
		while (cursor.moveToNext()){
			HashMap item = new HashMap();
			for (int i=0;i<columnCount;++i){
				int type = cursor.getType(i);
				switch (type){
					case 0:
						item.put(cursor.getColumnName(i),null);
						break;
					case 1:
						item.put(cursor.getColumnName(i), cursor.getInt(i));
						break;
					case 2:
						item.put(cursor.getColumnName(i),cursor.getFloat(i));
						break;
					case 3:
						item.put(cursor.getColumnName(i),cursor.getString(i));
						break;
				}
			}
			list.add(item);
		}
		cursor.close();
		db.close();
		return list;
	}

	/**
	 * 查询单条数据返回map
	 * @param sql
	 * @param params
	 * @return
	 */
	public Map queryItemMap(String sql,String[] params){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(sql,params);
		HashMap map = new HashMap();
		if (cursor.moveToNext()){
			for (int i = 0;i < cursor.getColumnCount();++i){
				int type = cursor.getType(i);
				switch (type){
					case 0:
						map.put(cursor.getColumnName(i),null);
						break;
					case 1:
						map.put(cursor.getColumnName(i),cursor.getInt(i));
						break;
					case 2:
						map.put(cursor.getColumnName(i),cursor.getFloat(i));
						break;
					case 3:
						map.put(cursor.getColumnName(i),cursor.getString(i));
						break;
				}
			}
		}
		cursor.close();
		db.close();
		return map;
	}

	/**
	 * 统一对ContentValues处理
	 * @param contentValues
	 * @param key
	 * @param value
	 */
	private void ContentValuesPut(ContentValues contentValues,String key,Object value){
		if (value==null){
			contentValues.put(key,"");
		}else{
			String className = value.getClass().getName();
			if (className.equals("java.lang.String")){
				contentValues.put(key,value.toString());
			} else if (className.equals("java.lang.Integer")){
				contentValues.put(key,Integer.valueOf(value.toString()));
			} else if (className.equals("java.lang.Float")){
				contentValues.put(key,Float.valueOf(value.toString()));
			} else if (className.equals("java.lang.Double")){
				contentValues.put(key,Double.valueOf(value.toString()));
			} else if (className.equals("java.lang.Boolean")){
				contentValues.put(key,Boolean.valueOf(value.toString()));
			} else if (className.equals("java.lang.Long")){
				contentValues.put(key,Long.valueOf(value.toString()));
			} else if (className.equals("java.lang.Short")){
				contentValues.put(key,Short.valueOf(value.toString()));
			}
		}
	}
	/**
	 * 统一对数组where条件进行拼接
	 * @param whereColumns
	 * @return
	 */
	private String initWhereSqlFromArray(String[] whereColumns){
		StringBuffer whereStr = new StringBuffer();
		for (int i=0;i<whereColumns.length;++i){
			whereStr.append(whereColumns[i]).append(" = ? ");
			if (i<whereColumns.length-1){
				whereStr.append(" and ");
			}
		}
		return whereStr.toString();
	}

	/**
	 * 统一对map的where条件和值进行处理
	 * @param whereParams
	 * @return
	 */
	private Map<String,Object> initWhereSqlFromMap(Map<String,String> whereParams){
		Set set = whereParams.keySet();
		String[] temp = new String[whereParams.size()];
		int i = 0;
		Iterator iterator = set.iterator();
		StringBuffer whereStr = new StringBuffer();
		while (iterator.hasNext()){
			String key = (String) iterator.next();
			whereStr.append(key).append(" = ? ");
			temp[i] = whereParams.get(key);
			if (i<set.size()-1){
				whereStr.append(" and ");
			}
			i++;
		}
		HashMap result = new HashMap();
		result.put("whereSql",whereStr);
		result.put("whereSqlParam",temp);
		return result;
	}

}
