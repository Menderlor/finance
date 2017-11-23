package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.项目简略;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;

import org.json.JSONObject;

/***
 * 新建项目 2015/08/10 18:04
 * 
 * @author K
 * 
 */
public class ProjectAddActivity extends BaseActivity {

	private final int CODE_SELECT_USERS = 1;

	/** 单选员工 */
	private final int CODE_SELECT_SIGNAL_USER = 2;
	private Context mContext;
	private HttpUtils mHttpUtils;

	private ImageView ivBack;
	private ImageView ivSave;
	private EditText etName;
	private EditText etPaticipant;
	private EditText etHeader;

	/** 多个员工 **/
	private String mSelectUserIds;

	/** 单个员工 */
	private String mSelectUserId;

	private 项目简略 mProject;

	private final int SUCCEED_SAVE = 1;
	private final int FAILURE_SAVE = 2;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCEED_SAVE:
				ProgressDialogHelper.dismiss();
				Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
				break;
			case FAILURE_SAVE:
				ProgressDialogHelper.dismiss();
				Toast.makeText(mContext, "保存失败", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_add);
		initData();
		initView();
		initEvent();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == CODE_SELECT_USERS) {
				Bundle bundle = data.getExtras();
				mSelectUserIds = bundle.getString("UserSelectId");
				LogUtils.i("kjx21", "------>" + mSelectUserIds);
				String usersName = bundle.getString("UserSelectName");
				etPaticipant.setText(usersName);
			}

			if (requestCode == CODE_SELECT_SIGNAL_USER) {
				Bundle bundle = data.getExtras();
				mSelectUserId = bundle.getString("UserSelectId");
				LogUtils.i("kjx21_SIGNAL", "------>" + mSelectUserId);
				String usersName = bundle.getString("UserSelectName");
				etHeader.setText(usersName);
			}
		}
	}

	private void initData() {
		mContext = this;
		mHttpUtils = new HttpUtils();
		mProject = new 项目简略();
	}

	private void initView() {
		ivBack = (ImageView) findViewById(R.id.iv_back_project_add);
		ivSave = (ImageView) findViewById(R.id.iv_save_project_add);
		etName = (EditText) findViewById(R.id.et_name_project_add);
		etPaticipant = (EditText) findViewById(R.id.et_participant_project_add);
		etHeader = (EditText) findViewById(R.id.et_header_project_add);
	}

	private void initEvent() {
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ivSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isEmpty()) {
					ProgressDialogHelper.show(mContext);
					saveProject();
				}
			}
		});

		etPaticipant.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext,
						User_SelectActivityNew_zmy.class);
				Bundle bundle = new Bundle();
				bundle.putString("UserSelectId", mSelectUserIds);
				intent.putExtras(bundle);
				startActivityForResult(intent, CODE_SELECT_USERS);
			}
		});

		etHeader.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext,
						User_SelectActivityNew_zmy.class);
				Bundle bundle = new Bundle();
				bundle.putBoolean(User_SelectActivityNew_zmy.SELECT_EMPLOYEE,
						true); // 单选
				intent.putExtras(bundle);
				startActivityForResult(intent, CODE_SELECT_SIGNAL_USER);
			}
		});
	}

	private boolean isEmpty() {
		String name = etName.getText().toString();
		if (TextUtils.isEmpty(name)) {
			Toast.makeText(mContext, "名称不能为空！", Toast.LENGTH_SHORT).show();
			return true;
		}

		if (TextUtils.isEmpty(mSelectUserIds)) {
			Toast.makeText(mContext, "参与人不能为空！", Toast.LENGTH_SHORT).show();
			return true;
		}

		if (TextUtils.isEmpty(mSelectUserId)) {
			Toast.makeText(mContext, "负责人不能为空！", Toast.LENGTH_SHORT).show();
			return true;
		}

		try {
			mProject.名称 = name;
			mProject.参与人 = mSelectUserIds;
			mProject.负责人 = Integer.parseInt(mSelectUserId);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}

	private void saveProject() {
		final String url = Global.BASE_URL + "Project/SaveProject";
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject jo;
				try {
					jo = JsonUtils.initJsonObj(mProject);
					String result = mHttpUtils.postSubmit(url, jo);

					String status = JsonUtils.parseStatus(result);
					if ("1".equals(status)) {
						handler.sendEmptyMessage(SUCCEED_SAVE);
					} else {
						handler.sendEmptyMessage(FAILURE_SAVE);
					}
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(FAILURE_SAVE);
				}
			}
		}).start();
	}
}
