package com.example.mp3tabs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.ads.AdRequest.ErrorCode;
import com.mp3tabs.bll.XListHelper;
import com.mp3tabs.bll.XListItemAdapter;
import com.mp3tabs.bll.XTabHelper;
import com.mp3tabs.bll.XListHelper.XCheck;
import com.mp3tabs.bll.XThread;
import com.mp3tabs.model.XCharsetEnum;
import com.mp3tabs.model.XEditInfo;
import com.mp3tabs.model.XFileInfo;
import com.mp3tabs.model.XSettingOneView;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SaveActivity extends Activity {
	private ArrayList<XFileInfo> selectFileList;
	private ImageButton save_view_btnReturn;
	private ImageButton save_view_btnSave;
	private ImageButton save_view_btnSelectAll;
	private ListView save_view_listSave;
	private XListItemAdapter xListItemAdapter;
	private RelativeLayout save_view_rlLoading;
	private TextView save_view_txtLoading;
	private Spinner save_view_listCharset;
	private TextView save_view_txtMsg;
	private XTabHelper xTabHelper = new XTabHelper();
	private AdView adView;
	private boolean isSaving = false;
	private int index = 0;
	private XEditInfo xEditInfo;

	public XEditInfo getxEditInfo() {
		return xEditInfo;
	}

	private XFileInfo xFileOneInfo;
	private View setting_one_view;
	private XSettingOneView xSettingOneView;
	public XListHelper<XFileInfo> listHelper = new XListHelper<XFileInfo>();
	public Object lock1 = new Object();
	// private Intent intent = new Intent("com.mp3tabs.bll.XSaveService");
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
		setContentView(R.layout.save_view);

		save_view_btnReturn = (ImageButton) this
				.findViewById(R.id.save_view_btnReturn);
		save_view_btnSave = (ImageButton) this
				.findViewById(R.id.save_view_btnSave);
		save_view_btnSelectAll = (ImageButton) this
				.findViewById(R.id.save_view_btnSelectAll);
		save_view_listSave = (ListView) this
				.findViewById(R.id.save_view_listSave);
		save_view_rlLoading = (RelativeLayout) this
				.findViewById(R.id.save_view_rlLoading);
		save_view_txtLoading = (TextView) this
				.findViewById(R.id.save_view_txtLoading);
		save_view_listCharset = (Spinner) this
				.findViewById(R.id.save_view_listCharset);
		save_view_txtMsg = (TextView) this.findViewById(R.id.save_view_txtMsg);

		save_view_btnReturn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = getIntent();
				setResult(RESULT_CANCELED, intent);
				finish();
			}
		});

		save_view_btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!listHelper.Existed(selectFileList,
						new XCheck<XFileInfo>() {
							@Override
							public boolean check(XFileInfo t) {
								return t.isNewChecked;
							}
						})) {
					new AlertDialog.Builder(SaveActivity.this).setTitle("提示")
							.setMessage("請先选择需要保存的文件！")
							.setPositiveButton("确定", null).show();
					return;
				}
				if (selectFileList.size() > 20 && xEditInfo.isAD == false) {
					ShowMsg("广告显示失败！一次只能转换20个文件，请打开网络，可在“帮助”里点击“重新显示广告”，为了作者能不断改善该工具，请支持一下，如造成不便，请谅解，谢谢！");
					return;
				}
				new AlertDialog.Builder(SaveActivity.this)
						.setTitle("确认保存")
						.setMessage("确认保存所选的修改信息吗？")
						.setPositiveButton("是",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// 确认保存
										save_view_rlLoading
												.setVisibility(View.VISIBLE);
										save_view_txtLoading.setText("");
										xEditInfo.saveCharset = save_view_listCharset
												.getSelectedItem()
												.equals("原编码") ? null
												: XCharsetEnum
														.toEnum(save_view_listCharset
																.getSelectedItem()
																.toString());
										ExecutorService es = Executors
												.newFixedThreadPool(Runtime
														.getRuntime()
														.availableProcessors());
										index = 0;
										isSaving = true;
										for (int i = 0; i < selectFileList
												.size(); i++) {
											XFileInfo f = selectFileList.get(i);
											f.index = i;
											XThread t = new XThread(
													new XThread.XRun(f) {

														@Override
														public void run() {
															try {
																XFileInfo f = this.file;
																if (xEditInfo.changeType
																		.equals("改标签")) {
																	// 改标签
																	if (f.isNewChecked) {
																		synchronized (lock1) {
																			index++;
																		}
																		xTabHelper
																				.saveTags(
																						f,
																						xEditInfo.saveCharset);
																		Message msg = new Message();
																		msg.what = 0;
																		msg.obj = "正在更新标签 "
																				+ index
																				+ "/"
																				+ listHelper
																						.FindAll(
																								selectFileList,
																								new XCheck<XFileInfo>() {

																									@Override
																									public boolean check(
																											XFileInfo t) {
																										return t.isNewChecked;
																									}
																								})
																						.size()
																				+ "\n(暂时不支持后台，\n请勿关闭程序，\n以免保存失败!)";
																		handler.sendMessage(msg);
																	}
																} else {
																	// 检测文件名是否重复
																	if (!xTabHelper
																			.CheckFileList(selectFileList)) {
																		return;
																	}
																	if (f.isNewChecked) {
																		synchronized (lock1) {
																			index++;
																		}
																		xTabHelper
																				.saveFileName(f);
																	}
																	Message msg = new Message();
																	msg.what = 0;
																	msg.obj = "正在更新文件名 "
																			+ index
																			+ "/"
																			+ listHelper
																					.FindAll(
																							selectFileList,
																							new XCheck<XFileInfo>() {

																								@Override
																								public boolean check(
																										XFileInfo t) {
																									return t.isNewChecked;
																								}
																							})
																					.size()
																			+ "\n(暂时不支持后台，\n请勿关闭程序，\n以免保存失败!)";
																	handler.sendMessage(msg);
																}
															} catch (Exception e) {
																Message msg = new Message();
																msg.what = 2;
																msg.obj = e
																		.toString();
																handler.sendMessage(msg);
															}
															Message msg = new Message();
															msg.what = 3;
															handler.sendMessage(msg);
														}
													});
											es.execute(t);
										}

									}
								}).setNegativeButton("否", null).show();

			}
		});

		save_view_btnSelectAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				for (XFileInfo f : selectFileList) {
					f.isNewChecked = !f.isNewChecked;
					if (f.isError || f.isEnabled == false)
						f.isNewChecked = false;
				}
				xListItemAdapter.notifyDataSetChanged();
			}
		});
		intentData = getIntent();
		save_view_rlLoading.setVisibility(View.VISIBLE);
		save_view_txtLoading.setText("");
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				selectFileList = (ArrayList<XFileInfo>) intentData
						.getSerializableExtra("selectFileList");
				xEditInfo = (XEditInfo) intentData
						.getSerializableExtra("xEditInfo");
				try {
					for (XFileInfo f : selectFileList) {
						f.isEnabled = true;
						f.isError = false;
						if (xEditInfo.changeType.equals("改标签")) {
							xTabHelper.changeTabs(f, xEditInfo);
						} else {
							xTabHelper.changeName(f, xEditInfo);
						}
						Message msg = new Message();
						msg.what = 0;
						msg.obj = (selectFileList.indexOf(f) + 1) + "/"
								+ selectFileList.size();
						handler.sendMessage(msg);
					}
					if (xEditInfo.changeType.equals("改文件名")) {
						xTabHelper.CheckFileList(selectFileList);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					new AlertDialog.Builder(SaveActivity.this)
							.setTitle("提示")
							.setMessage(e.getMessage())
							.setNeutralButton("确定",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											SaveActivity.this.finish();
										}
									}).show();
				}
				Message msg = new Message();
				msg.what = 1;
				msg.obj = selectFileList;
				handler.sendMessage(msg);
			}
		});
		t.start();
		// 第一步：添加一个下拉列表项的list，这里添加的项就是下拉列表的菜单项
		ArrayList<String> list = new ArrayList<String>();
		list.add("原编码");
		list.add(XCharsetEnum.ISO_8859_1.name());
		list.add(XCharsetEnum.UNICODE.name());
		list.add(XCharsetEnum.UTF_16BE.name());
		list.add(XCharsetEnum.UTF_8.name());
		// 第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		// 第三步：为适配器设置下拉列表下拉时的菜单样式。
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 第四步：将适配器添加到下拉列表上
		save_view_listCharset.setAdapter(adapter);

		save_view_rlLoading.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		showAD();
	}

	private Intent intentData;// Activity传递数据
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				// 更新进度
				save_view_txtLoading.setText((String) msg.obj);
			} else if (msg.what == 1) {
				// 标签载入完成
				try {
					if (xEditInfo.changeType.equals("改文件名"))
						xTabHelper.CheckFileList(selectFileList);
					Collections.sort(selectFileList, comparator);
					xListItemAdapter = new XListItemAdapter(SaveActivity.this,
							selectFileList, true);
					save_view_listSave.setAdapter(xListItemAdapter);
					if (xEditInfo.changeType.equals("改文件名")) {
						int errorsize;// 错误数
						errorsize = listHelper.FindAll(selectFileList,
								new XCheck<XFileInfo>() {
									@Override
									public boolean check(XFileInfo t) {
										return t.isError;
									}
								}).size();
						int enabledsize;// 不用修改数
						enabledsize = listHelper.FindAll(selectFileList,
								new XCheck<XFileInfo>() {
									@Override
									public boolean check(XFileInfo t) {
										return t.isEnabled == false;
									}
								}).size();
						save_view_txtMsg.setText(Html.fromHtml("共"
								+ selectFileList.size()
								+ "个文件，<font color=\"#ff0000\">存在" + errorsize
								+ "个错误</font>，<font color=\"#00FF00\">"
								+ enabledsize + "个不用修改</font>！"));
					} else {
						save_view_txtMsg.setText(Html.fromHtml("共"
								+ selectFileList.size() + "个文件！"));
					}
					save_view_rlLoading.setVisibility(View.INVISIBLE);
					ShowToast("长按可编辑文件标签!");
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (xEditInfo.isAD == false) {
					ShowMsg("广告显示失败！一次只能转换20个文件，请打开网络，可在“帮助”里点击“重新显示广告”，为了作者能不断改善该工具，请支持一下，如造成不便，请谅解，谢谢！");
				}
			} else if (msg.what == 2) {
				// 保存错误
				isSaving = false;
				ShowMsg("保存出错：" + (String) msg.obj);
				save_view_rlLoading.setVisibility(View.GONE);
				// stopService(intent);
			} else if (msg.what == 3) {
				// stopService(intent);
				isSaving = false;
				try {
					if (index != listHelper.FindAll(selectFileList,
							new XCheck<XFileInfo>() {

								@Override
								public boolean check(XFileInfo t) {
									return t.isNewChecked;
								}
							}).size()) {
						return;
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				}
				// 保存完成
				Intent intent = getIntent();
				intent.putExtra("selectFileList", selectFileList);
				setResult(RESULT_OK, intent);
				finish();
			}
			super.handleMessage(msg);
		}
	};// 跨线程调用控件

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Init();
	}

	public void ShowEditTag(XFileInfo xFile) {
		// 编辑界面
		// 初始化编辑公式视图
		setting_one_view = this.getLayoutInflater().inflate(
				R.layout.setting_one_view, null);
		xSettingOneView = new XSettingOneView(setting_one_view,
				xEditInfo.changeType);
		xSettingOneView.setting_one_view_txtName.setText(xFile.newName);
		xSettingOneView.setting_one_view_txtTitle.setText(xFile.newTitle);
		xSettingOneView.setting_one_view_txtArtist.setText(xFile.newArtist);
		xSettingOneView.setting_one_view_txtAlbum.setText(xFile.newAlbum);
		xFileOneInfo = xFile;
		// 弹出对话框
		new AlertDialog.Builder(this).setTitle("编辑公式")
				.setView(setting_one_view)
				.setPositiveButton("是", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 确认保存公式
						xFileOneInfo.newName = xSettingOneView.setting_one_view_txtName
								.getText().toString().trim();
						xFileOneInfo.newTitle = xSettingOneView.setting_one_view_txtTitle
								.getText().toString().trim();
						xFileOneInfo.newArtist = xSettingOneView.setting_one_view_txtArtist
								.getText().toString().trim();
						xFileOneInfo.newAlbum = xSettingOneView.setting_one_view_txtAlbum
								.getText().toString().trim();
						if (xEditInfo.changeType.equals("改文件名")) {
							try {
								xTabHelper.CheckFileList(selectFileList);
								int errorsize;// 错误数
								errorsize = listHelper.FindAll(selectFileList,
										new XCheck<XFileInfo>() {
											@Override
											public boolean check(XFileInfo t) {
												return t.isError;
											}
										}).size();
								int enabledsize;// 不用修改数
								enabledsize = listHelper.FindAll(
										selectFileList,
										new XCheck<XFileInfo>() {
											@Override
											public boolean check(XFileInfo t) {
												return t.isEnabled == false;
											}
										}).size();
								save_view_txtMsg.setText(Html.fromHtml("共"
										+ selectFileList.size()
										+ "个文件，<font color=\"#ff0000\">存在"
										+ errorsize
										+ "个错误</font>，<font color=\"#00FF00\">"
										+ enabledsize + "个不用修改</font>！"));
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InstantiationException e) {
								e.printStackTrace();
							}
						}
						xListItemAdapter.notifyDataSetChanged();
					}
				}).setNegativeButton("否", null).show();
	}

	public void ShowMsg(String msg) {
		// 提示框
		new AlertDialog.Builder(this).setTitle("提示").setMessage(msg)
				.setNeutralButton("确定", null).show();
	}

	public void ShowToast(String msg) {
		// 提示信息
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	private void showAD() {
		// google廣告

		// 创建adView。
		/*
		 * adView = new AdView(this, AdSize.IAB_BANNER, "a15368e7446dd29");
		 * body.addView(adView);
		 */
		adView = (AdView) this.findViewById(R.id.adView);
		// 启动一般性请求。
		AdRequest adRequest = new AdRequest();

		// 在adView中加载广告请求。
		adView.loadAd(adRequest);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && isSaving == true) {
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = getIntent();
			setResult(RESULT_CANCELED, intent);
			finish();
			return true;
		}
		return false;
		// return super.onKeyDown(keyCode, event);
	}
}
