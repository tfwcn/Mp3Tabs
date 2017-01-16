package com.mp3tabs.model;

import java.io.Serializable;

import com.example.mp3tabs.R;

import android.view.View;
import android.widget.TextView;

public class XSettingOneView implements Serializable {
	private static final long serialVersionUID = -3411376559458890859L;
	public TextView setting_one_view_txtName;
	public TextView setting_one_view_txtTitle;
	public TextView setting_one_view_txtArtist;// 歌手
	public TextView setting_one_view_txtAlbum;// 专辑

	public XSettingOneView() {

	}

	public XSettingOneView(View view,String changeType) {
		setting_one_view_txtName = (TextView) view
				.findViewById(R.id.setting_one_view_txtName);
		setting_one_view_txtTitle = (TextView) view
				.findViewById(R.id.setting_one_view_txtTitle);
		setting_one_view_txtArtist = (TextView) view
				.findViewById(R.id.setting_one_view_txtArtist);// 歌手
		setting_one_view_txtAlbum = (TextView) view
				.findViewById(R.id.setting_one_view_txtAlbum);// 专辑
		if(changeType.equals("改文件名")){
			setting_one_view_txtName.setEnabled(true);
			setting_one_view_txtTitle.setEnabled(false);
			setting_one_view_txtArtist.setEnabled(false);
			setting_one_view_txtAlbum.setEnabled(false);
		}else{
			setting_one_view_txtName.setEnabled(false);
			setting_one_view_txtTitle.setEnabled(true);
			setting_one_view_txtArtist.setEnabled(true);
			setting_one_view_txtAlbum.setEnabled(true);
		}
	}
}
