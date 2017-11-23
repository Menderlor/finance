package com.cedarhd.test;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.cedarhd.biz.UserBiz;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Client;
import com.cedarhd.models.User;
import com.cedarhd.models.任务;
import com.cedarhd.models.字典;
import com.cedarhd.models.日志;
import com.cedarhd.models.订单;
import com.cedarhd.models.通知;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DBTest extends AndroidTestCase {

	private final String TAG = "dbtest";

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	/**
	 * 插入数据
	 * 
	 * @throws InterruptedException
	 */
	public void insertOrder() {
		ORMDataHelper helper = ORMDataHelper.getInstance(getContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		String nowDate = "";
		for (int i = 1; i < 2; i++) {
			ContentValues values = new ContentValues();
			nowDate = sdf.format(new Date());
			values.put("UpdateTime", nowDate);
			values.put("OrderNO", "010" + i);
			values.put("photoSerialNo", "481234134213241W");
			values.put("ClientName", "北京东方波尔科技有限公司");
			values.put("Total", "956000");
			values.put("OrderTime", "2013-11-28");
			values.put("AccountApproveTime", nowDate);
			values.put("MeasureTime", "2014-2-08");
			values.put("State", "已付款");
			db.insert("订单", null, values);
		}
	}

	/**
	 * 插入数据任务
	 * 
	 * @throws SQLException
	 * 
	 * @throws InterruptedException
	 */
	public void insertPlane() throws SQLException {
		ORMDataHelper helper = ORMDataHelper.getInstance(getContext());
		Dao<任务, Integer> dao = helper.getDao(任务.class);
		for (int i = 20; i < 28; i++) {
			任务 item = new 任务();
			item.PublisherName = "刘莉";
			item.Title = "测试标题" + i;
			item.Participant = "阚健雄";
			item.Time = "2013-12-30";
			item.CompletionTime = "2013-12-" + i;
			item.Content = i + "任务：此处任务内容，测试内容是假数据，就啦送董老赴京啦怒了啦买了件拉丁方，省略7000字:";
			// item.Status = "开始";
			// 照片序列号，对应多张照片
			item.photoNo = "abc6adf" + i;
			// item.UpdateTime = ViewHelper.getDateString();
			dao.create(item);
		}
	}

	/**
	 * 遍历数据表
	 * 
	 * @throws InterruptedException
	 */
	public void queryDao() {
		ORMDataHelper helper = ORMDataHelper.getInstance(getContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			Dao<订单, Integer> dao = helper.getDao(订单.class);
			List<订单> lists = dao.queryForAll();
			for (订单 item : lists) {
				// LogUtils.i(TAG, item.orderNO + "--" + item.clientName + "--"
				// + item.state);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	/**
	 * 遍历数据表
	 * 
	 * @throws InterruptedException
	 */
	public void queryUser() {
		ORMDataHelper helper = ORMDataHelper.getInstance(getContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			Dao<User, Integer> dao = helper.getDao(User.class);
			List<User> lists = dao.queryForAll();
			for (User item : lists) {
				LogUtils.i("user", item.Id + "--" + item.UserName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	/**
	 * 中文字段表插入数据
	 * 
	 * @throws InterruptedException
	 */
	public void insertToCH() {
		ORMDataHelper helper = ORMDataHelper.getInstance(getContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		String nowDate = "";
		for (int i = 3; i < 5; i++) {
			ContentValues values = new ContentValues();
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			nowDate = sdf.format(new Date());
			values.put("最后更新", nowDate);
			values.put("单号", "010" + i);
			values.put("客户名称", "江西齐云山食品有限公司");
			values.put("金额 ", "137000");
			values.put("下单时间", "2013-11-28");
			values.put("财务审核时间", nowDate);
			values.put("测量时间", "2013-12-08");
			values.put("状态", "等待付款");
			db.insert("订单", null, values);
		}
	}

	public void testCompareDate() {
		Boolean resultBoolean = DateDeserializer.compareDate("2011-11-09",
				"2011-11-10");
		LogUtils.i(TAG, "----------------->" + resultBoolean);
	}

	/**
	 * 根据id查询多个员工姓名
	 * 
	 * @param ids
	 *            例如：‘12’;‘14’;‘13’;
	 * @return 员工姓名 例如：张三,李四,王五
	 */
	public String getUserNamesById() {
		String ids = "‘12’;‘14’;‘13’";
		String names = "";
		if (ids.contains("'")) {
			ids = ids.replace("'", "");
		}
		if (ids.contains(";")) {
			String[] idArr = ids.split(";");
			ORMDataHelper helper = ORMDataHelper.getInstance(getContext());
			Dao<User, Integer> dao = helper.getUserDao();
			User user = new User();
			for (int i = 0; i < idArr.length - 1; i++) {
				user.setId(idArr[i]);
				try {
					List<User> result = dao.queryForMatching(user);
					if (result.size() > 0) {
						names += result.get(0).getUserName() + ";";
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		LogUtils.i("keno73", names);
		return names;
	}

	/**
	 * 格式化时间
	 * 
	 * 时间显示到分
	 * 
	 * @param date
	 * @return
	 */
	public static void formatTime() {
		String date = "2014-11-11 17:30:09";
		String time = "";
		if (date == null || "".equals(date)) {
		} else {
			int year = Calendar.YEAR;
			if (date.contains("-")) {
				String yearStr = date.split("-")[0];
				int y = Integer.parseInt(yearStr);
				if (year == y) {
					LogUtils.i("XXXX", "今年");
				}
			}
		}
	}

	public void httpGet() {
		ZLServiceHelper zlServiceHelper = new ZLServiceHelper();
		zlServiceHelper.getPhotoInfos(getContext(), "2011,2012,2013,2014");
	}

	/**
	 * 测试ormlite拼接查询
	 */
	@SuppressWarnings("unchecked")
	public void queryOrmlite() {
		String userId = "85";
		LogUtils.i("dbtest", userId);
		// 由于数据库没统一可能有如下两种格式
		String newuserId = "%''" + userId + "'';%"; // 查询 '3';'13';'20';
		String newuserId2 = "%," + userId + ",%"; // 查询3,13,20
		LogUtils.i("dbtest", newuserId + "---" + newuserId2);
		ORMDataHelper helper = ORMDataHelper.getInstance(getContext());
		try {
			Dao<通知, Integer> dao = helper.getDao(通知.class);
			List<通知> lists = dao.queryForAll();
			LogUtils.i("dbtest", "总数量：" + lists.size());
			Where<通知, Integer> where = dao.queryBuilder().where();
			Where<通知, Integer> where2 = dao.queryBuilder().where();
			// TODO 满足条件：1.发布人为自己 2，接收人有自己 3,为空
			where.and(where.and(where.not().like("Read", newuserId), where
					.not().like("Read", userId)), where.or(
					where.eq("Publisher", userId),
					where.like("Personnel", newuserId)));

			int size2 = where2.not().like("Read", newuserId).and()
					.like("Personnel", newuserId).and().eq("Publisher", userId)
					.query().size();
			// where.and(where.not().like("Read", newuserId),
			// where.eq("Publisher", userId));
			// where = dao
			// .queryBuilder()
			// .where()
			// .not()
			// .like("Read", newuserId)
			// .and(first.eq("Publisher", userId).or()
			// .like("Personnel", newuserId), second);
			// Where<通知, Integer> where = dao.queryBuilder().where().not()
			// .like("Read", newuserId).and().eq("Publisher", userId).or()
			// .like("Personnel", newuserId);

			// LogUtils.i("dbtest", "查询条件：" + where.toString());
			int size = where.query().size();
			LogUtils.i("dbtest", "查询结果：" + size);
			LogUtils.i("dbtest", "查询结果：" + size2);
		} catch (SQLException e) {
			e.printStackTrace();
			LogUtils.i("dbtest", "错误结果：" + e);
		} finally {
			LogUtils.d("dbtest", "执行完毕...");
		}
	}

	public void test() {
		// ZLServiceHelper zlServiceHelper = new ZLServiceHelper();
		// zlServiceHelper.getLatelyTaskTime(getContext());

		DateDeserializer.dateIsBeforoNow("2013-06-11 13:35");
	}

	public void queryRaw() {
		ORMDataHelper helper = ORMDataHelper.getInstance(getContext());
		try {
			Dao<日志, Integer> dao = helper.getDao(日志.class);
			QueryBuilder<日志, Integer> builder = dao.queryBuilder();
			builder.where().eq("Personnel", 105);
			GenericRawResults<String[]> result = dao.queryRaw(builder
					.selectRaw("max(UpdateTime)").prepareStatementString());
			if (result == null) {
				LogUtils.i(TAG, "null______________null");
				return;
			} else {
				String[] values = (String[]) result.getFirstResult();
				if (values != null && values.length > 0) {
					if (values[0] == null) {
						LogUtils.i(TAG, "values[0]==null");
					} else {
						LogUtils.i(TAG, values[0]);
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void queryList() {
		int loadCount = 10;
		ORMDataHelper helper = ORMDataHelper.getInstance(getContext());
		Dao<日志, Integer> dao;
		try {
			dao = helper.getDao(日志.class);
			// orderBy ("字段值",false代表降序)
			List<日志> list = dao.queryBuilder().offset((long) 10)
					.limit(loadCount).orderBy("UpdateTime", false).query();
			LogUtils.i(TAG, "查询结果");
			LogUtils.i(TAG, list.size() + "");
			for (int i = 0; i < list.size(); i++) {
				LogUtils.i(TAG, list.get(i).Time + "---"
						+ list.get(i).Personnel);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 遍历客户数据表
	 * 
	 * @throws InterruptedException
	 */
	public void queryClient() {
		ORMDataHelper helper = ORMDataHelper.getInstance(getContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			Dao<Client, Integer> dao = helper.getDao(Client.class);

			QueryBuilder<Client, Integer> builder = dao.queryBuilder();
			builder.groupBy("Classification");
			List<Client> lists = builder.query();
			for (Client item : lists) {
				LogUtils.i(
						"user",
						"分类：" + item.getClassification() + "--"
								+ item.getClassificationName() + "---上级："
								+ item.getParentNode());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	public void getDict() {
		final ZLServiceHelper zlServiceHelper = new ZLServiceHelper();
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<字典> list = zlServiceHelper.getDictList("行业");
				for (int i = 0; i < list.size(); i++) {
					LogUtils.i("getDict", list.get(i).getName());
				}
			}
		}).start();
	}

	public void initJson() {
		Client client = new Client();
		client.CustomerName = "王重阳";
		client.Readed = "ok";
		client.setAddress("北京市海淀区");
		client.setContacts("刘志敏");
		client.setPhone("15117952378");
		try {
			JSONObject jo = JsonUtils.initJsonObj(client, Client.class);
			// TODO Auto-generated catch block
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getLocalUser() {
		User user = UserBiz.getLocalSerializableUser();
		LogUtils.i(TAG, user.getUserName());
	}
}
