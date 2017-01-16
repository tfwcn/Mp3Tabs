package com.example.mp3tabs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//import net.youmi.android.AdManager;
//import net.youmi.android.spot.SpotDialogListener;
//import net.youmi.android.spot.SpotManager;

import com.mp3tabs.bll.XListItemAdapter;
import com.mp3tabs.bll.XTabHelper;
import com.mp3tabs.model.XCharsetEnum;
import com.mp3tabs.model.XEditInfo;
import com.mp3tabs.model.XFileInfo;
import com.mp3tabs.model.XSettingView;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.AdRequest.ErrorCode;

public class MainActivity extends Activity {
	private ArrayList<XFileInfo> selectFileList = new ArrayList<XFileInfo>();
	private	RelativeLayout body;
	private ImageButton info_view_btnAdd;
	private ImageButton info_view_btnNext;
	private ImageButton info_view_btnEdit;
	private ImageButton info_view_btnSelectAll;
	private ImageButton info_view_btnDel;
	private ImageButton info_view_btnHelp;
	private ListView info_view_listInfo;
	private RelativeLayout info_view_rlLoading;
	private TextView info_view_txtLoading;
	private XListItemAdapter xListItemAdapter;
	private XTabHelper xTabHelper;
	private View setting_view;
	private XSettingView xSettingView;
	private XCharsetEnum charset;
	private XEditInfo xEditInfo;
	private View help_view;
	private AdView adView;
	private Comparator<XFileInfo> comparator = new Comparator<XFileInfo>() {

		@Override
		public int compare(XFileInfo lhs, XFileInfo rhs) {
			if (lhs.filePath.equals(rhs.filePath))
				return lhs.oldName.compareToIgnoreCase(rhs.oldName);
			else
				return lhs.oldName.compareToIgnoreCase(rhs.oldName);
		}
	};

	private void Init() {
		setContentView(R.layout.info_view);
//		AdManager.getInstance(this).init("5b1f4607ef0f04c7",
//				"96ac4eb88f860280", false);
		// 初始化变量
		xTabHelper = new XTabHelper();
		xEditInfo = new XEditInfo();
		// 初始化界面控件
		body = (RelativeLayout) this
				.findViewById(R.id.body);
		info_view_btnAdd = (ImageButton) this
				.findViewById(R.id.info_view_btnAdd);
		info_view_btnNext = (ImageButton) this
				.findViewById(R.id.info_view_btnNext);
		info_view_btnEdit = (ImageButton) this
				.findViewById(R.id.info_view_btnEdit);
		info_view_btnSelectAll = (ImageButton) this
				.findViewById(R.id.info_view_btnSelectAll);
		info_view_btnDel = (ImageButton) this
				.findViewById(R.id.info_view_btnDel);
		info_view_btnHelp = (ImageButton) this
				.findViewById(R.id.info_view_btnHelp);
		info_view_listInfo = (ListView) this
				.findViewById(R.id.info_view_listInfo);
		info_view_rlLoading = (RelativeLayout) this
				.findViewById(R.id.info_view_rlLoading);
		info_view_txtLoading = (TextView) this
				.findViewById(R.id.info_view_txtLoading);

		// 添加文件按钮
		info_view_btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						SelectFileActivity.class);
				startActivityForResult(intent, R.layout.select_view);
			}
		});

		// 下一步按钮
		info_view_btnNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (selectFileList.size() == 0) {
					new AlertDialog.Builder(MainActivity.this).setTitle("提示")
							.setMessage("請先添加文件！")
							.setPositiveButton("确定", null).show();
					return;
				}
				if (xEditInfo.changeType.length() == 0) {
					new AlertDialog.Builder(MainActivity.this).setTitle("提示")
							.setMessage("請先編輯公式！")
							.setPositiveButton("确定", null).show();
					return;
				}
				// 传值
				Intent intent = new Intent(MainActivity.this,
						SaveActivity.class);
				intent.putExtra("selectFileList", selectFileList);
				intent.putExtra("xEditInfo", xEditInfo);
				// 跳转到预览界面
				startActivityForResult(intent, R.layout.save_view);
			}
		});

		info_view_btnEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 初始化编辑公式视图
				setting_view = MainActivity.this.getLayoutInflater().inflate(
						R.layout.setting_view, null);
				xSettingView = new XSettingView(setting_view);
				// 编辑标签公式按钮，单击事件
				if (xEditInfo.changeType.equals("改文件名"))
					xSettingView.setting_view_rbChangeName.setChecked(true);
				else
					xSettingView.setting_view_rbChangeTabs.setChecked(true);
				xSettingView.setting_view_txtName.setText(xEditInfo.fxName);
				xSettingView.setting_view_txtTitle.setText(xEditInfo.fxTitle);
				xSettingView.setting_view_chkTitle
						.setChecked(xEditInfo.chkTitle);
				xSettingView.setting_view_txtArtist.setText(xEditInfo.fxArtist);
				xSettingView.setting_view_chkArtist
						.setChecked(xEditInfo.chkArtist);
				xSettingView.setting_view_txtAlbum.setText(xEditInfo.fxAlbum);
				xSettingView.setting_view_chkAlbum
						.setChecked(xEditInfo.chkAlbum);

				// 弹出对话框
				AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
						.setTitle("编辑公式")
						.setView(setting_view)
						.setPositiveButton("是",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// 确认保存公式
										xEditInfo.changeType = xSettingView.setting_view_rgChangeType
												.getCheckedRadioButtonId() == R.id.setting_view_rbChangeTabs ? "改标签"
												: "改文件名";
										xEditInfo.fxName = xSettingView.setting_view_txtName
												.getText().toString();
										xEditInfo.fxTitle = xSettingView.setting_view_txtTitle
												.getText().toString();
										xEditInfo.chkTitle = xSettingView.setting_view_chkTitle
												.isChecked();
										xEditInfo.fxArtist = xSettingView.setting_view_txtArtist
												.getText().toString();
										xEditInfo.chkArtist = xSettingView.setting_view_chkArtist
												.isChecked();
										xEditInfo.fxAlbum = xSettingView.setting_view_txtAlbum
												.getText().toString();
										xEditInfo.chkAlbum = xSettingView.setting_view_chkAlbum
												.isChecked();
									}
								}).setNegativeButton("否", null).show();
			}
		});

		info_view_btnSelectAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 反选
				for (XFileInfo f : selectFileList) {
					f.isOldChecked = !f.isOldChecked;
				}
				xListItemAdapter.notifyDataSetChanged();
			}
		});

		info_view_btnDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 删除选中项
				ArrayList<XFileInfo> tmpSelectFileList = new ArrayList<XFileInfo>();
				for (XFileInfo f : selectFileList) {
					if (!f.isOldChecked)
						tmpSelectFileList.add(f);
				}
				selectFileList.clear();
				selectFileList.addAll(tmpSelectFileList);
				Collections.sort(selectFileList, comparator);
				xListItemAdapter.notifyDataSetChanged();
			}
		});

		info_view_btnHelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 帮助
				help_view = MainActivity.this.getLayoutInflater().inflate(
						R.layout.help_view, null);
				TextView help_view_info = (TextView) help_view
						.findViewById(R.id.help_view_info);
				String info = "该版本提供功能有：\n" + "1.批量修改MP3标签\n" + "2.批量修改文件名\n"
						+ "3.批量MP3标签转编码。\n\n" + "使用说明：\n" + "打开后添加文件，\n"
						+ "然后编辑标签，\n" + "点击下一步，\n" + "选择要批量修改的文件(可以改编码)，\n\n"
						+ "标签表达式说明：\n" + "保留字符：%1-%9\n" + "例子：\n" + "原文件：\n"
						+ "文件1：歌手1 - 歌名1\n" + "文件2：歌手2 - 歌名2\n" + "公式：\n"
						+ "文件名：%1 - %2\n" + "歌手：%1\n" + "歌名：%2\n"
						+ "按以上公式即可提取文件名中的歌手和歌名。\n";
				help_view_info.setText(info);
				Button help_view_ad = (Button) help_view
						.findViewById(R.id.help_view_ad);
				help_view_ad.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						showAD();
					}
				});
				new AlertDialog.Builder(MainActivity.this).setTitle("帮助")
						.setView(help_view).setNeutralButton("确定", null).show();
			}
		});

		xListItemAdapter = new XListItemAdapter(this, selectFileList, false);
		info_view_listInfo.setAdapter(xListItemAdapter);

		info_view_rlLoading.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		showAD();
	}

	private void showAD() {
		ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			xEditInfo.isAD = true;// 广告显示成功
		} else {
			xEditInfo.isAD = false;// 广告显示失败
			ShowMsg("广告显示失败！一次只能转换20个文件，请打开网络，可在“帮助”里点击“重新显示广告”，为了作者能不断改善该工具，请支持一下，如造成不便，请谅解，谢谢！");
			return;
		}
		// 加载插播资源
//		SpotManager.getInstance(this).loadSpotAds();
		// 设置展示超时时间，加载超时则不展示广告，默认0，代表不设置超时时间
//		SpotManager.getInstance(this).setSpotTimeout(5000);// 设置5秒

		// 展示插播广告，可以不调用loadSpot独立使用
//		SpotManager.getInstance(MainActivity.this).showSpotAds(
//				MainActivity.this, new SpotDialogListener() {
//					@Override
//					public void onShowSuccess() {
//						// xEditInfo.isAD = true;// 广告显示成功
//					}
//
//					@Override
//					public void onShowFailed() {
//						xEditInfo.isAD = false;// 广告显示失败
//						Message msg = new Message();
//						msg.what = 3;
//						handler.sendMessage(msg);
//					}
//
//				});
		// //
		// 可以根据需要设置Theme，如下调用，如果无特殊需求，直接调用上方的接口即可
		// SpotManager.getInstance(YoumiAdDemo.this).showSpotAds(YoumiAdDemo.this,
		// android.R.style.Theme_Translucent_NoTitleBar);
		// //

		// google廣告

		// 创建adView。
		/*adView = new AdView(this, AdSize.IAB_BANNER, "a15368e7446dd29");
		body.addView(adView);*/
		adView = (AdView) this
				.findViewById(R.id.adView);
		adView.setAdListener(new AdListener() {
			
			@Override
			public void onReceiveAd(Ad arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPresentScreen(Ad arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLeaveApplication(Ad arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
				// TODO Auto-generated method stub
				xEditInfo.isAD = false;// 广告显示失败
				Message msg = new Message();
				msg.what = 3;
				handler.sendMessage(msg);
			}
			
			@Override
			public void onDismissScreen(Ad arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		// 启动一般性请求。
		AdRequest adRequest = new AdRequest();

		// 在adView中加载广告请求。
		adView.loadAd(adRequest);
	}

	@Override
	public void onDestroy() {
		//adView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Init();
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.main, menu); return true; }
	 */

	private Intent intentData;// Activity传递数据
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				info_view_txtLoading.setText((String) msg.obj);
			} else if (msg.what == 1) {
				selectFileList.addAll((ArrayList<XFileInfo>) msg.obj);
				Collections.sort(selectFileList, comparator);
				xListItemAdapter.notifyDataSetChanged();
				info_view_rlLoading.setVisibility(View.INVISIBLE);
			} else if (msg.what == 2) {
				new AlertDialog.Builder(MainActivity.this).setTitle("提示")
						.setMessage(msg.obj.toString())
						.setNeutralButton("确定", null).show();
				info_view_rlLoading.setVisibility(View.INVISIBLE);
			} else if (msg.what == 3) {
				ShowMsg("广告显示失败！一次只能转换20个文件，请打开网络，可在“帮助”里点击“重新显示广告”，为了作者能不断改善该工具，请支持一下，如造成不便，请谅解，谢谢！");
			}
			super.handleMessage(msg);
		}
	};// 跨线程调用控件

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 返回结果
		intentData = data;
		switch (resultCode) {
		case RESULT_OK:
			if (requestCode == R.layout.select_view) {
				// 添加文件
				String charsetStr = intentData.getStringExtra("charset");
				if (!charsetStr.equals("自动编码")) {
					charset = XCharsetEnum.toEnum(charsetStr);
				} else {
					charset = XCharsetEnum.GBK;
				}
				info_view_rlLoading.setVisibility(View.VISIBLE);
				info_view_txtLoading.setText("");
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						ArrayList<XFileInfo> tmpSelectFileList = new ArrayList<XFileInfo>();
						ArrayList<XFileInfo> tmpList = (ArrayList<XFileInfo>) intentData
								.getSerializableExtra("selectFileList");
						for (XFileInfo f : tmpList) {
							if (CheckedFile(f)) {
								try {
									xTabHelper.load(f, charset);
								} catch (Exception e) {
									Message msg = new Message();
									msg.what = 2;
									msg.obj = "读取文件\"" + f.oldName + "\"错误："
											+ e.toString();
									handler.sendMessage(msg);
									return;
								}
								tmpSelectFileList.add(f);
							}
							Message msg = new Message();
							msg.what = 0;
							msg.obj = tmpList.indexOf(f) + "/" + tmpList.size();
							handler.sendMessage(msg);
						}
						Message msg = new Message();
						msg.what = 1;
						msg.obj = tmpSelectFileList;
						handler.sendMessage(msg);
					}
				});
				t.start();
			} else if (requestCode == R.layout.save_view) {
				// 保存成功
				new AlertDialog.Builder(MainActivity.this).setTitle("提示")
						.setMessage("保存成功!").setNeutralButton("确定", null)
						.show();
				ArrayList<XFileInfo> returnFileList = (ArrayList<XFileInfo>) intentData
						.getSerializableExtra("selectFileList");
				selectFileList.clear();
				selectFileList.addAll(returnFileList);
				xListItemAdapter.notifyDataSetChanged();
			}
		}

	}

	private boolean CheckedFile(XFileInfo file) {
		for (XFileInfo f : selectFileList) {
			if (file.filePath.equals(f.filePath)
					&& file.oldName.equalsIgnoreCase(f.oldName + ".mp3")) {
				return false;
			}
		}
		return true;
	}

	public void ShowMsg(String msg) {
		// 提示框
		new AlertDialog.Builder(this).setTitle("提示").setMessage(msg)
				.setNeutralButton("确定", null).show();
	}
}
