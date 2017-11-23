package com.cedarhd.helpers.server;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.cedarhd.R;
import com.cedarhd.helpers.Global;
import com.cedarhd.models.Common_TableName;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class delSQLiteManagerEX {
	Context mContext;
	// DBHelper helper;
	public SQLiteDatabase Mysqlite;
	private final int BUFFER_SIZE = 400000;
	// 数据库名称
	public static String DB_NAME = "zlphonedata.db";
	public static final String PACKAGE_NAME = "com.cedarhd";
	// 在手机里存放数据库的位置
	public static final String DB_PATH = "/data"
			+ Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME;

	// private final String DB_PATH = android.os.Environment
	// .getExternalStorageDirectory().getAbsolutePath()
	// + "/dictionary";

	public static String Id = "_id";
	public static int DataVersion = 1;

	// private class DBHelper extends SQLiteOpenHelper {
	//
	// public DBHelper(Context context) {
	// super(context, DB_NAME, null, DataVersion);
	// // TODO Auto-generated constructor stub
	// }
	//
	// @Override
	// public void onCreate(SQLiteDatabase db) {
	// db.execSQL(Table_CREATETABLE);
	// }
	//
	// @Override
	// public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	// {
	// db.execSQL("DROP TABLE IF EXISTS notes");
	// onCreate(db);
	// }
	//
	// }

	public delSQLiteManagerEX(Context context) {
		mContext = context;
		// open();
	}

	public synchronized void open() {
		// helper = new DBHelper(mContext);
		// // Mysqlite = helper.getWritableDatabase();
		// Mysqlite = helper.getReadableDatabase();

		// if (Mysqlite == null) {
		Mysqlite = this.openDatabase(DB_PATH + "/" + DB_NAME);
		// }

	}

	private SQLiteDatabase openDatabase(String dbfile) {
		try {
			if (!(new File(dbfile).exists())) {
				// 判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
				InputStream in = mContext.getResources().openRawResource(
						R.raw.zlphonedata);// 欲导入的数据库
				FileOutputStream fos = new FileOutputStream(dbfile);
				int lenght = in.available();
				byte[] buffer = new byte[lenght];
				int count = 0;
				while ((count = in.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				in.close();
			}
			SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile,
					null);
			return db;
		} catch (FileNotFoundException e) {
			Log.e("Database", "File not found");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("Database", "IO exception");
			e.printStackTrace();
		}
		return null;
	}

	public void BeginTransaction() {
		Mysqlite.beginTransaction();
	}

	public synchronized void close() {
		// helper.close();
		Mysqlite.close();
	}

	public void execSQL(String sql) {
		try {
			Mysqlite.execSQL(sql);
		} catch (SQLException e) {
			Log.i("err", "execSQL failed：" + sql);
		}
	}

	// 本地过去用户的方法
	public HashMap<String, Object> GetUser_Local(String corpName, String uname,
			String pwd) {
		HashMap<String, Object> map = null;

		// open();

		String filter = " 企业名称='" + corpName + "' and 名称='" + uname
				+ "' and 密码='" + pwd + "'";
		Cursor cursor = Mysqlite.rawQuery(" select * from 用户  where " + filter,
				null);
		String[] columns = cursor.getColumnNames();
		if (cursor.moveToNext()) {
			map = new HashMap<String, Object>();
			for (String str : columns) {
				map.put(str, cursor.getString(cursor.getColumnIndex(str)));
			}
		}

		// close();
		return map;
	}

	// 保存用户到本机
	public Boolean InsertUser(HashMap<String, Object> map) {
		try {
			// open();
			BeginTransaction();// 手动设置开始事务
			// 数据插入操作
			ContentValues cv = null;
			cv = new ContentValues();
			cv.put("_id", map.get("编号").toString());
			cv.put("编号", map.get("编号").toString());
			cv.put("名称", map.get("名称").toString());
			cv.put("密码", map.get("密码").toString());
			cv.put("企业编号", map.get("企业编号").toString());
			cv.put("企业名称", map.get("企业名称").toString());
			insert(cv, "用户");
			// 设置事务处理成功，不设置会自动回滚不提交
			Mysqlite.setTransactionSuccessful();
			// 处理完成
			Mysqlite.endTransaction();
			// close();
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	// 保存获取到的数据到本机
	public Boolean InsertDataList(String tableName,
			List<HashMap<String, Object>> hashMapList) {

		if (hashMapList == null)
			return false;

		try {
			// open();
			BeginTransaction();// 手动设置开始事务
			// 数据插入操作循环
			ContentValues cv = null;
			for (HashMap<String, Object> map : hashMapList) {
				if (tableName == Common_TableName.通知.toString()) {
					cv = new ContentValues();
					// cv.put("_id", map.get("Id").toString());
					// cv.put("发布人", map.get("Publisher").toString());
					// cv.put("发布人名称", map.get("PublisherName").toString());
					// cv.put("发布时间", map.get("ReleaseTime").toString());
					// cv.put("内容", map.get("Content").toString());
					// cv.put("标题", map.get("Title").toString());
					// cv.put("过期时间", map.get("ExpirationTime").toString());
					// cv.put("员工", map.get("Personnel").toString());
					// cv.put("员工名称", map.get("PersonnelName").toString());
					// cv.put("已读", map.get("Read").toString());
					// cv.put("附件", map.get("Attachment").toString());
					cv.put("_id", map.get("Id").toString());
					cv.put("Id", map.get("Id").toString());
					cv.put("Publisher", map.get("Publisher").toString());
					cv.put("PublisherName", map.get("PublisherName").toString());
					cv.put("ReleaseTime", map.get("ReleaseTime").toString());
					cv.put("Content", map.get("Content").toString());
					cv.put("Title", map.get("Title").toString());
					cv.put("ExpirationTime", map.get("ExpirationTime")
							.toString());
					cv.put("Personnel", map.get("Personnel").toString());
					cv.put("PersonnelName", map.get("PersonnelName").toString());
					cv.put("Read", map.get("Read").toString());
					cv.put("Attachment", map.get("Attachment").toString());
					cv.put("CorpId", Global.mUser.CorpId);
					cv.put("UserId", Global.mUser.Id);
					insert(cv, tableName);
				} else if (tableName == Common_TableName.邮件.toString()) {
					cv = new ContentValues();
					cv.put("_id", map.get("Id").toString());
					cv.put("Id", map.get("Id").toString());
					cv.put("Title", map.get("Title").toString());
					cv.put("Content", map.get("Content").toString());
					cv.put("Sender", map.get("Sender").toString());
					cv.put("SenderName", map.get("SenderName").toString());
					cv.put("Receiver", map.get("Receiver").toString());
					cv.put("ReceiverName", map.get("ReceiverName").toString());
					cv.put("SendTime", map.get("SendTime").toString());
					cv.put("Reply", map.get("Reply").toString());
					cv.put("IsSender", map.get("IsSender").toString());
					cv.put("Attachment", map.get("Attachment").toString());
					cv.put("Read", map.get("Read").toString());
					cv.put("CorpId", Global.mUser.CorpId);
					cv.put("UserId", Global.mUser.Id);
					insert(cv, tableName);
				} else if (tableName == Common_TableName.任务.toString()) {
					cv = new ContentValues();
					cv.put("_id", map.get("Id").toString());
					cv.put("Id", map.get("Id").toString());
					cv.put("Publisher", map.get("Publisher").toString());
					cv.put("PublisherName", map.get("PublisherName").toString());
					cv.put("Executor", map.get("Executor").toString());
					cv.put("ExecutorName", map.get("ExecutorName").toString());
					cv.put("Title", map.get("Title").toString());
					cv.put("Content", map.get("Content").toString());
					cv.put("Status", map.get("Status").toString());
					cv.put("StatusName", map.get("StatusName").toString());
					cv.put("Time", map.get("Time").toString());
					cv.put("AssignTime", map.get("AssignTime").toString());
					cv.put("CompletionTime", map.get("CompletionTime")
							.toString());
					cv.put("Sort", map.get("Sort").toString());
					cv.put("Attachment", map.get("Attachment").toString());
					cv.put("CorpId", Global.mUser.CorpId);
					cv.put("UserId", Global.mUser.Id);
					insert(cv, tableName);
				} else if (tableName == Common_TableName.员工日志.toString()) {
					cv = new ContentValues();
					cv.put("_id", map.get("Id").toString());
					cv.put("Id", map.get("Id").toString());
					cv.put("Time", map.get("Time").toString());
					cv.put("Content", map.get("Content").toString());
					cv.put("Personnel", map.get("Personnel").toString());
					cv.put("PersonnelName", map.get("PersonnelName").toString());
					cv.put("CorpId", Global.mUser.CorpId);
					cv.put("UserId", Global.mUser.Id);
					insert(cv, tableName);
				} else if (tableName == Common_TableName.客户.toString()) {
					cv = new ContentValues();
					cv.put("_id", map.get("Id").toString());
					cv.put("Id", map.get("Id").toString());
					cv.put("Classification", map.get("Classification")
							.toString());
					cv.put("ClassificationName", map.get("ClassificationName")
							.toString());
					cv.put("CustomerName", map.get("CustomerName").toString());
					cv.put("Trade", map.get("Trade").toString());
					cv.put("TradeName", map.get("TradeName").toString());
					cv.put("Salesman", map.get("Salesman").toString());
					cv.put("SalesmanName", map.get("SalesmanName").toString());
					cv.put("RegisterTime", map.get("RegisterTime").toString());
					// cv.put("LastContactTime",
					// map.get("LastContactTime").toString());
					// cv.put("PlanContactTime",
					// map.get("PlanContactTime").toString());
					cv.put("Attachment", map.get("Attachment").toString());
					cv.put("ToContact", map.get("ToContact").toString());
					cv.put("ContactState", map.get("ContactState").toString());
					cv.put("Province", map.get("Province").toString());
					cv.put("ProvinceName", map.get("ProvinceName").toString());
					cv.put("City", map.get("City").toString());
					cv.put("CityName", map.get("CityName").toString());
					cv.put("CorpId", map.get("CorpId").toString());
					cv.put("UserId", map.get("UserId").toString());
					cv.put("Phone", map.get("Phone").toString());
					cv.put("Address", map.get("Address").toString());
					cv.put("updateTime", map.get("updateTime").toString());
					insert(cv, tableName);
				} else if (tableName == Common_TableName.销售机会.toString()) {
					cv = new ContentValues();
					cv.put("_id", map.get("Id").toString());
					cv.put("Id", map.get("Id").toString());
					cv.put("CustomerId", map.get("CustomerId").toString());
					cv.put("Classification", map.get("Classification")
							.toString());
					cv.put("ClassificationName", map.get("ClassificationName")
							.toString());
					cv.put("CustomerName", map.get("CustomerName").toString());
					cv.put("Trade", map.get("Trade").toString());
					cv.put("TradeName", map.get("TradeName").toString());
					cv.put("Salesman", map.get("Salesman").toString());
					cv.put("SalesmanName", map.get("SalesmanName").toString());
					cv.put("RegisterTime", map.get("RegisterTime").toString());
					// cv.put("LastContactTime",
					// map.get("LastContactTime").toString());
					// cv.put("PlanContactTime",
					// map.get("PlanContactTime").toString());
					cv.put("Attachment", map.get("Attachment").toString());
					cv.put("ToContact", map.get("ToContact").toString());
					cv.put("ContactState", map.get("ContactState").toString());
					cv.put("Province", map.get("Province").toString());
					cv.put("ProvinceName", map.get("ProvinceName").toString());
					cv.put("City", map.get("City").toString());
					cv.put("CityName", map.get("CityName").toString());
					cv.put("CorpId", map.get("CorpId").toString());
					cv.put("UserId", map.get("UserId").toString());
					cv.put("Phone", map.get("Phone").toString());
					cv.put("Address", map.get("Address").toString());
					cv.put("Content", map.get("Content").toString());

					insert(cv, tableName);
				} else if (tableName == Common_TableName.工作计划.toString()) {
					cv = new ContentValues();
					cv.put("_id", map.get("Id").toString());
					cv.put("Id", map.get("Id").toString());
					cv.put("CustomerId", map.get("CustomerId").toString());
					cv.put("Classification", map.get("Classification")
							.toString());
					cv.put("ClassificationName", map.get("ClassificationName")
							.toString());
					cv.put("CustomerName", map.get("CustomerName").toString());
					// cv.put("LastContactTime",
					// map.get("LastContactTime").toString());
					// cv.put("PlanContactTime",
					// map.get("PlanContactTime").toString());
					cv.put("CorpId", map.get("CorpId").toString());
					cv.put("UserId", map.get("UserId").toString());
					cv.put("PlanTime", map.get("PlanTime").toString());
					cv.put("Address", map.get("Address").toString());
					cv.put("Content", map.get("Content").toString());
					cv.put("State", map.get("State").toString());
					cv.put("StateName", map.get("StateName").toString());
					cv.put("Latitude", map.get("Latitude").toString());
					cv.put("Longitude", map.get("Longitude").toString());
					cv.put("Contacts", map.get("Contacts").toString());
					cv.put("Phone", map.get("Phone").toString());

					insert(cv, tableName);
				} else if (tableName == Common_TableName.联系拜访记录.toString()) {
					cv = new ContentValues();
					cv.put("_id", map.get("Id").toString());
					cv.put("Id", map.get("Id").toString());
					cv.put("CustomerId", map.get("CustomerId").toString());
					cv.put("Salesman", map.get("Salesman").toString());
					cv.put("SalesChance", map.get("SalesChance").toString());
					cv.put("Time", map.get("Time").toString());
					// cv.put("LastContactTime",
					// map.get("LastContactTime").toString());
					// cv.put("PlanContactTime",
					// map.get("PlanContactTime").toString());
					cv.put("CorpId", map.get("CorpId").toString());
					cv.put("UserId", map.get("UserId").toString());
					cv.put("CustomerName", map.get("CustomerName").toString());
					cv.put("SalesmanName", map.get("SalesmanName").toString());
					cv.put("Address", map.get("Address").toString());
					cv.put("Latitude", map.get("Latitude").toString());
					cv.put("Longitude", map.get("Longitude").toString());

					insert(cv, tableName);
				} else if (tableName == Common_TableName.联系拜访记录明细.toString()) {
					cv = new ContentValues();
					cv.put("_id", map.get("Id").toString());
					cv.put("Id", map.get("Id").toString());
					cv.put("Classification", map.get("Classification")
							.toString());
					cv.put("Content", map.get("Content").toString());
					cv.put("ContactHistoryId", map.get("ContactHistoryId")
							.toString());

					insert(cv, tableName);
				}
			}
			// 设置事务处理成功，不设置会自动回滚不提交
			Mysqlite.setTransactionSuccessful();
			// 处理完成
			Mysqlite.endTransaction();
			// close();
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	public void insert(ContentValues cv, String tName) {
		Mysqlite.insert(tName, null, cv);
	}

	/* 通过Cursor查询所有数据 */
	public Cursor fetchAllData(String tname) {
		return Mysqlite.query(tname, null, null, null, null, null, null);
	}

	// public List<String> findAllDate() {
	// List<String> list = new ArrayList<String>();
	// Cursor cursor = Mysqlite.rawQuery("select Name from Street",
	// new String[] {});
	// while (cursor.moveToNext()) {
	// list.add(cursor.getString(0));
	// }
	// cursor.close();
	// // Mysqlite.close();
	// return list;
	// }

	// public Cursor fetchData(int RowId) {
	// return Mysqlite.query(true, t_Name, new String[] { Id, v_Name }, "_id="
	// + RowId, null, null, null, null, null);
	// }
	/*
	 * 找出
	 */
	public List getExistedData(String tableName,
			List<HashMap<String, Object>> entities) {
		ArrayList<HashMap<String, Object>> listExisted = new ArrayList<HashMap<String, Object>>();
		StringBuilder sb = new StringBuilder();
		for (HashMap<String, Object> entity : entities) {
			if (sb.length() > 0)
				sb.append(",");
			sb.append(entity.get("Id"));
		}

		Cursor cursor = Mysqlite.rawQuery("select Id from " + tableName
				+ " where Id in(" + sb.toString() + ")", null);
		ArrayList listIds = new ArrayList();
		while (cursor.moveToNext()) {
			listIds.add(cursor.getInt(0));
		}
		return listIds;
	}

	// 获取最新的更新时间
	public Date getMaxUpdateTime(String tableName) throws ParseException {
		Cursor cursor = Mysqlite.rawQuery("select max(UpdateTime) from "
				+ tableName, null);
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
		if (cursor.moveToNext()) {
			if (cursor.getString(0) != null) {

				try {
					return sdf.parse(cursor.getString(0));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
			}
		}
		return sdf.parse("19700101 00:00:00");
	}

	// 获取编号最大的值
	public String getMaxId(String tableName) {
		Cursor cursor = Mysqlite.rawQuery("select max(_id) from " + tableName,
				null);
		if (cursor.moveToNext()) {
			if (cursor.getString(0) != null)
				return cursor.getString(0);
		}
		return "0";
	}

	public String getMaxIdForContactHistory(String tableName,
			String extraCondition) {
		Cursor cursor = Mysqlite.rawQuery("select max(_id) from " + tableName
				+ " where ContactHistoryId = ?",
				new String[] { extraCondition });
		if (cursor.moveToNext()) {
			if (cursor.getString(0) != null)
				return cursor.getString(0);
		}
		return "0";
	}

	// public String getMaxIdForSalesChance(String tableName, String
	// extraCondition) {
	// Cursor cursor = Mysqlite.rawQuery("select max(_id) from " + tableName +
	// " where CustomerName = ?",
	// new String[]{extraCondition});
	// if (cursor.moveToNext()) {
	// if (cursor.getString(0) != null)
	// return cursor.getString(0);
	// }
	// return "0";
	// }

	// 获取本地数据并分页
	public List<HashMap<String, Object>> getData_Paging(String tableName,
			int start, int requestCount, String condition, String extraCondition) {
		// open();
		List<HashMap<String, Object>> hashMaplst = new ArrayList<HashMap<String, Object>>();
		Cursor cursor = null;
		try {

			if (!(tableName.equals(Common_TableName.工作计划.toString())
					|| tableName.equals(Common_TableName.联系拜访记录.toString()) || tableName
						.equals(Common_TableName.联系拜访记录明细.toString()))) {

				if (tableName.equals(Common_TableName.销售机会.toString())
						&& (condition != null) && (!condition.equals(""))) {
					cursor = Mysqlite.query(tableName, null,
							" CustomerName = ?", new String[] { condition },
							null, null, "_id desc", Integer.toString(start)
									+ "," + Integer.toString(requestCount));
				} else {
					cursor = Mysqlite.query(tableName, null, null, null, null,
							null, "_id desc", Integer.toString(start) + ","
									+ Integer.toString(requestCount));
				}

			} else {

				if (tableName.equals(Common_TableName.联系拜访记录明细.toString())) {
					cursor = Mysqlite.query(tableName, null,
							" ContactHistoryId = ?",
							new String[] { extraCondition }, null, null,
							"_id desc", null);
				} else if (tableName.equals(Common_TableName.联系拜访记录.toString())) {
					if (extraCondition.equals("")) {
						cursor = Mysqlite.query(tableName, null, null, null,
								null, null, "_id desc", null);
					} else {
						if (extraCondition.startsWith("FromCustomerDetail")) {
							cursor = Mysqlite.query(tableName, null,
									" CustomerId = ?",
									new String[] { extraCondition
											.substring("FromCustomerDetail"
													.length()) }, null, null,
									"_id desc", null);
						} else if (extraCondition.startsWith("FromSalesChance")) {
							cursor = Mysqlite.query(tableName, null,
									" SalesChance = ?",
									new String[] { extraCondition
											.substring("FromSalesChance"
													.length()) }, null, null,
									"_id desc", null);
						} else {
							cursor = Mysqlite.query(tableName, null, null,
									null, null, null, "_id desc", null);
						}
					}

				} else {
					cursor = Mysqlite.query(tableName, null, null, null, null,
							null, "_id desc", null);
				}
			}
		} catch (Exception ex) {
			Log.e(ex.getMessage(), ex.getStackTrace().toString());
		}

		String[] columns = cursor.getColumnNames();

		while (cursor.moveToNext()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			for (String str : columns) {
				map.put(str, cursor.getString(cursor.getColumnIndex(str)));
			}
			hashMaplst.add(map);
		}
		cursor.close();
		// close();
		return hashMaplst;
	}

	public List<HashMap<String, Object>> getTableData(String tableName) {
		// open();
		List<HashMap<String, Object>> hashMaplst = new ArrayList<HashMap<String, Object>>();

		Cursor cursor = Mysqlite.query(tableName, null, null, null, null, null,
				null, null);

		String[] columns = cursor.getColumnNames();

		while (cursor.moveToNext()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			for (String str : columns) {
				map.put(str, cursor.getString(cursor.getColumnIndex(str)));
			}
			hashMaplst.add(map);
		}
		cursor.close();
		// close();
		return hashMaplst;
	}

	public boolean deleteCustomer(int id) {
		boolean flag = false;
		int returnValue = Mysqlite.delete("客户", " Id = ?",
				new String[] { String.valueOf(id) });
		if (returnValue != 0) {
			flag = true;
		}
		return flag;
	}

	public boolean deleteSalesChance(int id) {
		boolean flag = false;
		int returnValue = Mysqlite.delete("销售机会", " Id = ?",
				new String[] { String.valueOf(id) });
		if (returnValue != 0) {
			flag = true;
		}
		return flag;
	}

	public boolean deleteContactHistory(int contactHistoryId) {
		// TODO Auto-generated method stub
		boolean flag = false;
		int returnValue = Mysqlite.delete("联系拜访记录", " Id = ?",
				new String[] { String.valueOf(contactHistoryId) });
		if (returnValue != 0) {
			flag = true;
		}
		Mysqlite.delete("联系拜访记录明细", " ContactHistoryId = ?",
				new String[] { String.valueOf(contactHistoryId) });
		return flag;
	}

	public boolean deleteContactHistoryDetail(int contactHistoryDetailId) {
		// TODO Auto-generated method stub
		boolean flag = false;
		int returnValue = Mysqlite.delete("联系拜访记录明细", " Id = ?",
				new String[] { String.valueOf(contactHistoryDetailId) });
		if (returnValue != 0) {
			flag = true;
		}
		return flag;
	}

	public boolean deleteWorkPlan(int workPlanId) {
		// TODO Auto-generated method stub
		boolean flag = false;
		int returnValue = Mysqlite.delete("工作计划", " Id = ?",
				new String[] { String.valueOf(workPlanId) });
		if (returnValue != 0) {
			flag = true;
		}
		return flag;
	}

	public boolean updateWorkPlanState(int workPlanId, int state) {
		// TODO Auto-generated method stub
		boolean flag = false;
		ContentValues cv = new ContentValues();
		cv.put("State", state);
		String stateName;
		if (state == 0) {
			stateName = "未完成";
		} else if (state == 1) {
			stateName = "完成";
		} else {
			stateName = "部分完成";
		}
		cv.put("StateName", stateName);
		int returnValue = Mysqlite.update("工作计划", cv, " Id = ?",
				new String[] { String.valueOf(workPlanId) });
		if (returnValue != 0) {
			flag = true;
		}
		return flag;
	}

	public void updateEntity(HashMap<String, Object> entity) {
		// TODO Auto-generated method stub

	}
}