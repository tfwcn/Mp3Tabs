package com.mp3tabs.bll;

import java.util.ArrayList;

import com.mp3tabs.model.XCharsetEnum;
import com.mp3tabs.model.XEditInfo;
import com.mp3tabs.model.XFileInfo;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class XSaveService extends Service {
	private XTabHelper xTabHelper = new XTabHelper();
	private XEditInfo xEditInfo;
	private ArrayList<XFileInfo> selectFileList;
	private Handler handler;
	public static boolean IsRun = false;

	/*public XSaveService(XEditInfo xEditInfo,
			ArrayList<XFileInfo> selectFileList, Handler handler) {
		this.xEditInfo = xEditInfo;
		this.selectFileList = selectFileList;
		this.handler = handler;
	}*/

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		IsRun = true;
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		IsRun = false;
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		if (!IsRun) {
			selectFileList = (ArrayList<XFileInfo>) intent
					.getSerializableExtra("selectFileList");
			xEditInfo = (XEditInfo) intent
					.getSerializableExtra("xEditInfo");
			handler = (Handler) intent
					.getSerializableExtra("handler");
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						if (xEditInfo.changeType.equals("改标签")) {
							// 改标签
							for (XFileInfo f : selectFileList) {
								if (f.isNewChecked) {
									xTabHelper.saveTags(f,
											xEditInfo.saveCharset);
								}
								Message msg = new Message();
								msg.what = 0;
								msg.obj = "正在更新标签 "
										+ (selectFileList.indexOf(f) + 1) + "/"
										+ selectFileList.size();
								handler.sendMessage(msg);
							}
						} else {
							// 检测文件名是否重复
							if (!xTabHelper.CheckFileList(selectFileList)) {
								return;
							}
							for (XFileInfo f : selectFileList) {
								if (f.isNewChecked) {
									xTabHelper.saveFileName(f);
								}
								Message msg = new Message();
								msg.what = 0;
								msg.obj = "正在更新文件名 "
										+ (selectFileList.indexOf(f) + 1) + "/"
										+ selectFileList.size();
								handler.sendMessage(msg);
							}
						}
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = 2;
						msg.obj = e.toString();
						handler.sendMessage(msg);
					}
					Message msg = new Message();
					msg.what = 3;
					handler.sendMessage(msg);
				}
			});
			t.start();
		}
		super.onStart(intent, startId);
	}

}
