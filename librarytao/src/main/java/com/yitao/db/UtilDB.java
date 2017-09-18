package com.yitao.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtilDB {
	private static final String dbName = "example.db";
	private SQLiteDatabase dataBase;
	private boolean debug = false;
	private static HashMap<String, UtilDB> daoMap = new HashMap<String, UtilDB>();
	private static HashMap<String, String> tableMap = new HashMap<String, String>();

	public UtilDB(Context context) {
		this.dataBase = createDatabase(context);
	}

	/**
	 * 方法说明：创建UtilDB对象<br>
	 * 作者：易涛 <br>
	 * 时间：2015-7-27 下午3:37:14 <br>
	 * 
	 * @param context
	 * @return <br>
	 */
	public static UtilDB create(Context context) {
		return getInstance(context);
	}

	private synchronized static UtilDB getInstance(Context context) {
		UtilDB dao = daoMap.get(dbName);
		if (dao == null) {
			dao = new UtilDB(context);
			daoMap.put(dbName, dao);
		}
		return dao;
	}

	private SQLiteDatabase createDatabase(Context context) {
		return context.getApplicationContext().openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
	}

	/**
	  * 方法说明：保存数据<br>
	  * 作者：易涛 <br>
	  * 时间：2015-8-7 上午9:21:01 <br>
	  * @param clazz <br>
	  */
	public void save(Object clazz) {
		try {
			dataBase.beginTransaction();
			createTableIfNotExist(clazz.getClass());
			Map<String, Object> map = buildInsertSqlInfo(clazz);
			if (map != null && map.containsKey("sql") && map.containsKey("bindArgs")) {
				execNonQuery("DELETE FROM " + clazz.getClass().getSimpleName() + " WHERE id = '" + getFieldValueByName("id", clazz) + "'");
				execNonQuery(map.get("sql").toString(), ((List<Object>) map.get("bindArgs")).toArray());
			}
			dataBase.setTransactionSuccessful();
		} finally {
			dataBase.endTransaction();
		}
	}
	
	public boolean execSql(String sql) {
		boolean result = true;
		try {
			dataBase.execSQL(sql);
		} catch (Throwable e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	/**
	  * 方法说明：删除<br>
	  * 作者：易涛 <br>
	  * 时间：2015-8-7 上午9:24:07 <br>
	  * @param clazz
	  * @param whereBuilder <br>
	  */
	public void delete(Class clazz, WhereBuidler whereBuilder) {
		if (!tableIsExist(clazz)) {
			return;
		}
		try {
			dataBase.beginTransaction();
			String sql = "DELETE FROM " + clazz.getSimpleName() + " WHERE 1 = 1 ";
			if(whereBuilder != null){
				sql += whereBuilder.ModelToString(); 
			}
			execNonQuery(sql);
			dataBase.setTransactionSuccessful();
		} finally {
			dataBase.endTransaction();
		}
	}

	/**
	  * 方法说明：查找单个的信息<br>
	  * 作者：易涛 <br>
	  * 时间：2015-8-7 上午9:24:55 <br>
	  * @param entityType
	  * @param idValue
	  * @return <br>
	  */
	@SuppressWarnings("unchecked")
	public <T> T findById(Class<T> entityType, Object idValue) {
		if (!tableIsExist(entityType)) {
			return null;
		}
		Cursor cursor = execQuery("select * from " + entityType.getSimpleName() + " where id = '" + idValue+"'");
		if (cursor != null) {
			try {
				if (cursor.moveToNext()) {
					T entity = entityType.newInstance();
					return (T)setKeyValue(entityType, entity, cursor);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}
		return null;
	}
	
	/**
	  * 方法说明：查找多个<br>
	  * 作者：易涛 <br>
	  * 时间：2015-8-7 上午9:27:11 <br>
	  * @param entityType
	  * @param whereBuilder
	  * @return <br>
	  */
	public <T> List<T> findAll(Class<T> entityType, WhereBuidler whereBuilder){
        if (!tableIsExist(entityType)){
        	return null;
        }

        String sql = "select * from " + entityType.getSimpleName() + " where 1=1";
        if(whereBuilder != null){
        	sql += whereBuilder.ModelToString();
        }
        List<T> result = new ArrayList<T>();
        Cursor cursor = execQuery(sql);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
					T entity = entityType.newInstance();
                    result.add((T)setKeyValue(entityType, entity, cursor));
                }
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return result;
    }
	
	public <T> List<T> findListBySql(Class<T> entityType, String sql){
        if (!tableIsExist(entityType)){
        	return null;
        }
        List<T> result = new ArrayList<T>();
        Cursor cursor = execQuery(sql);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
					T entity = entityType.newInstance();
                    result.add((T)setKeyValue(entityType, entity, cursor));
                }
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return result;
    }
	
	

	/**
	 * 方法说明：判断表是否存在<br>
	 * 作者：易涛 <br>
	 * 时间：2015-7-27 下午3:42:08 <br>
	 * 
	 * @param clazz
	 * <br>
	 */
	public void createTableIfNotExist(Class clazz) {
		if (!tableIsExist(clazz)) {
			String createTableSql = buildCreateTableSqlInfo(clazz);
			execNonQuery(createTableSql);
		}
	}

	/**
	  * 方法说明：线数据库插入数据<br>
	  * 作者：易涛 <br>
	  * 时间：2015-7-28 下午3:28:22 <br>
	  * @param clazz
	  * @return <br>
	  */
	public Map<String, Object> buildInsertSqlInfo(Object clazz) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> objList = new ArrayList<Object>();
		List<KeyValue> keyValueList = getKeyValue(clazz);
		if (keyValueList.size() == 0) {
			return null;
		}
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("INSERT INTO ");
		sqlBuffer.append(clazz.getClass().getSimpleName());
		sqlBuffer.append(" (");
		for (KeyValue kv : keyValueList) {
			sqlBuffer.append(kv.getKey()).append(",");
			objList.add(kv.getValue());
		}
		sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
		sqlBuffer.append(") VALUES (");

		int length = keyValueList.size();
		for (int i = 0; i < length; i++) {
			sqlBuffer.append("?,");
		}
		sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
		sqlBuffer.append(")");
		map.put("sql", sqlBuffer.toString());
		map.put("bindArgs", objList);
		return map;
	}

	/**
	 * 方法说明：获取实体类的属性名称与属性值对应的key-value<br>
	 * 作者：易涛 <br>
	 * 时间：2015-7-27 下午4:13:36 <br>
	 * 
	 * @param clazz
	 * @return <br>
	 */
	private List<KeyValue> getKeyValue(Object clazz) {
		List<KeyValue> list = new ArrayList<KeyValue>();
		Field[] fields = clazz.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			KeyValue kv = new KeyValue();
			kv.setKey(fields[i].getName());
			kv.setValue(getFieldValueByName(fields[i].getName(), clazz));
			list.add(kv);
		}
		return list;
	}
	
	/**
	  * 方法说明：获取实体类的属性值<br>
	  * 作者：易涛 <br>
	  * 时间：2015-7-28 上午9:14:39 <br>
	  * @param clazz
	  * @return <br>
	  */
	private List<String> getEntityKey(Class clazz){
		List<String> keyList = new ArrayList<String>();
		Field[] fields = clazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			keyList.add(fields[i].getName());
		}
		return keyList;
	}

	/**
	 * 方法说明：根据属性名获取属性值<br>
	 * 作者：易涛 <br>
	 * 时间：2015-7-27 下午4:19:24 <br>
	 * 
	 * @param fieldName
	 * @param o
	 * @return <br>
	 */
	private Object getFieldValueByName(String fieldName, Object o) {
		try {
			String firstLetter = fieldName.substring(0, 1).toUpperCase();
			String getter = "get" + firstLetter + fieldName.substring(1);
			Method method = o.getClass().getMethod(getter, new Class[] {});
			Object value = method.invoke(o, new Object[] {});
			return value;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 方法说明：设置实体类的属性值<br>
	 * 作者：易涛 <br>
	 * 时间：2015-7-27 下午6:12:47 <br>
	 *
	 * @param clazz
	 * @param obj
	 * @param cur
	 * @return <br>
	 */
	private Object setKeyValue(Class clazz, Object obj, Cursor cur) {
		Field[] fields = clazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			setFieldValueByName(fields[i], obj, cur.getString(cur.getColumnIndex(fields[i].getName())));
		}
		return obj;
	}

	/**
	 * 方法说明：设置实体类的属性值<br>
	 * 作者：易涛 <br>
	 * 时间：2015-7-27 下午4:19:24 <br>
	 * 
	 * @param fieldName
	 * @param obj
	 * @param value
	 * @return <br>
	 */
	private void setFieldValueByName(Field fieldName, Object obj, Object value) {
		try {
			String firstLetter = fieldName.getName().substring(0, 1).toUpperCase();
			String setter = "set" + firstLetter + fieldName.getName().substring(1);
			Method method = obj.getClass().getDeclaredMethod(setter, fieldName.getType());
			method.invoke(obj, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String buildCreateTableSqlInfo(Class clazz) {
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("CREATE TABLE IF NOT EXISTS ");
        sqlBuffer.append(clazz.getSimpleName());
        sqlBuffer.append(" ( ");
        List<String> columnList = getEntityKey(clazz);
        for (String column : columnList) {
            sqlBuffer.append(column).append("  TEXT,");
        }

        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        sqlBuffer.append(" )");
        return sqlBuffer.toString();
	}

	/**
	 * 方法说明：判断表是否存在<br>
	 * 作者：易涛 <br>
	 * 时间：2015-7-27 下午3:53:23 <br>
	 * 
	 * @param clazz
	 * @return <br>
	 */
	public boolean tableIsExist(Class clazz) {
		if (tableMap.get(clazz.getSimpleName()) != null) {
			return true;
		}
		Cursor cursor = execQuery("SELECT COUNT(*) AS c FROM sqlite_master WHERE type='table' AND name='" + clazz.getSimpleName() + "'");
		if (cursor != null) {
			try {
				if (cursor.moveToNext()) {
					int count = cursor.getInt(0);
					if (count > 0) {
						tableMap.put(clazz.getSimpleName(), clazz.getSimpleName());
						return true;
					}
				}
			} catch (Throwable e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}
		return false;
	}

	public Cursor execQuery(String sql) {
		try {
			if (debug) {
				Log.d("sql", sql);
			}
			return dataBase.rawQuery(sql, null);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public void execNonQuery(String sql, Object[] bindArgs) {
		try {
			dataBase.execSQL(sql, bindArgs);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void execNonQuery(String sql) {
		try {
			if (debug) {
				Log.d("sql", sql);
			}
			dataBase.execSQL(sql);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
