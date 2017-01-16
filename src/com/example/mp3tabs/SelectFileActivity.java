package com.example.mp3tabs;

import java.util.ArrayList;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.ads.AdRequest.ErrorCode;
import com.mp3tabs.bll.XSelectItemAdapter;
import com.mp3tabs.model.XCharsetEnum;
import com.mp3tabs.model.XFileInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class SelectFileActivity extends Activity {
	private ArrayList<XFileInfo> selectFileList = new ArrayList<XFileInfo>();
	private ArrayList<XFileInfo> showFileList = new ArrayList<XFileInfo>();
	private ImageButton select_view_btnSelectAll;
	private ImageButton select_view_btnSure;
	private ListView select_view_listSelect;
	private TextView select_view_txtPath;
	private Spinner select_view_listCharset;
	private XSelectItemAdapter selectAdapter;
	private AdView adView;

	private void Init() {
		setContentView(R.layout.select_view);

		select_view_btnSelectAll = (ImageButton) this
				.findViewById(R.id.select_view_btnSelectAll);
		select_view_btnSure = (ImageButton) this
				.findViewById(R.id.select_view_btnSure);
		select_view_listSelect = (ListView) this
				.findViewById(R.id.select_view_listSelect);
		select_view_txtPath = (TextView) this
				.findViewById(R.id.select_view_txtPath);
		select_view_listCharset = (Spinner) this
				.findViewById(R.id.select_view_listCharset);

		select_view_btnSelectAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				for (XFileInfo f : showFileList) {
					f.isOldChecked=!f.isOldChecked;
				}
				selectAdapter.notifyDataSetChanged();
			}
		});

		select_view_btnSure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectFileList.clear();
				for (XFileInfo f : showFileList) {
					if (!f.isDirectory && f.isOldChecked) {
						selectFileList.add(f);
					}
				}
				Intent intent = getIntent();
				intent.putExtra("selectFileList", selectFileList);
				intent.putExtra("charset", (String)select_view_listCharset.getSelectedItem());
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		select_view_txtPath.setText("/");
		// 第一步：添加一个下拉列表项的list，这里添加的项就是下拉列表的菜单项
		ArrayList<String> list = new ArrayList<String>();
//		list.add("自动编码");
		list.add(XCharsetEnum.GBK.name());
		list.add(XCharsetEnum.ANSI.name());
		list.add(XCharsetEnum.ISO_8859_1.name());
		list.add(XCharsetEnum.UNICODE.name());
		list.add(XCharsetEnum.UTF_16BE.name());
		list.add(XCharsetEnum.UTF_8.name());
		list.add(XCharsetEnum.UTF_32.name());
		// 第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		// 第三步：为适配器设置下拉列表下拉时的菜单样式。
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 第四步：将适配器添加到下拉列表上
		select_view_listCharset.setAdapter(adapter);
		showAD();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && selectAdapter.BackPath()) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Init();
		selectAdapter = new XSelectItemAdapter(this, showFileList,
				selectFileList, select_view_txtPath);
		select_view_listSelect.setAdapter(selectAdapter);
	}
	
	private void showAD() {
		// google廣告

		// 创建adView。
		/*adView = new AdView(this, AdSize.IAB_BANNER, "a15368e7446dd29");
		body.addView(adView);*/
		adView = (AdView) this
				.findViewById(R.id.adView);
		// 启动一般性请求。
		AdRequest adRequest = new AdRequest();

		// 在adView中加载广告请求。
		adView.loadAd(adRequest);
	}
}
