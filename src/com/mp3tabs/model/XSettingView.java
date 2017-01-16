package com.mp3tabs.model;

import java.io.Serializable;

import com.example.mp3tabs.R;

import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class XSettingView implements Serializable {
	private static final long serialVersionUID = 7968266326912416587L;
	public RadioGroup setting_view_rgChangeType;
	public RadioButton setting_view_rbChangeTabs;
	public RadioButton setting_view_rbChangeName;
	public TextView setting_view_txtName;
	public TextView setting_view_txtTitle;
	public CheckBox setting_view_chkTitle;
	public TextView setting_view_txtArtist;// 歌手
	public CheckBox setting_view_chkArtist;
	public TextView setting_view_txtAlbum;// 专辑
	public CheckBox setting_view_chkAlbum;

	public XSettingView() {

	}

	public XSettingView(View view) {
		setting_view_rgChangeType = (RadioGroup) view
				.findViewById(R.id.setting_view_rgChangeType);
		setting_view_rbChangeTabs = (RadioButton) view
				.findViewById(R.id.setting_view_rbChangeTabs);
		setting_view_rbChangeName = (RadioButton) view
				.findViewById(R.id.setting_view_rbChangeName);
		setting_view_txtName = (TextView) view
				.findViewById(R.id.setting_view_txtName);
		setting_view_txtTitle = (TextView) view
				.findViewById(R.id.setting_view_txtTitle);
		setting_view_chkTitle = (CheckBox) view
				.findViewById(R.id.setting_view_chkTitle);
		setting_view_txtArtist = (TextView) view
				.findViewById(R.id.setting_view_txtArtist);// 歌手
		setting_view_chkArtist = (CheckBox) view
				.findViewById(R.id.setting_view_chkArtist);// 歌手
		setting_view_txtAlbum = (TextView) view
				.findViewById(R.id.setting_view_txtAlbum);// 专辑
		setting_view_chkAlbum = (CheckBox) view
				.findViewById(R.id.setting_view_chkAlbum);// 专辑
		setting_view_rgChangeType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId==R.id.setting_view_rbChangeTabs){
					setting_view_chkTitle.setVisibility(View.VISIBLE);
					setting_view_chkArtist.setVisibility(View.VISIBLE);
					setting_view_chkAlbum.setVisibility(View.VISIBLE);
				}else{
					setting_view_chkTitle.setVisibility(View.INVISIBLE);
					setting_view_chkArtist.setVisibility(View.INVISIBLE);
					setting_view_chkAlbum.setVisibility(View.INVISIBLE);
				}
			}
		});
	}
}
