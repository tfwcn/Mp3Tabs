package com.mp3tabs.model;

import java.io.Serializable;

import com.example.mp3tabs.R;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class XListItem implements Serializable {
	private static final long serialVersionUID = 7942528746496034823L;
	public TextView list_item_view;
	public TextView list_item_txtFileName;
	public TextView list_item_txtPath;
	public TextView list_item_txtTitle;
	public TextView list_item_txtAlbum ;
	public ImageView list_item_imgChecked;
	public XFileInfo file;
	public View convertView;

	public XListItem() {

	}

	public XListItem(View view) {
		this.convertView=view;
		 list_item_view= (TextView) convertView
				.findViewById(R.id.list_item_view);
		 list_item_txtFileName = (TextView) convertView
				.findViewById(R.id.list_item_txtFileName);
		 list_item_txtPath = (TextView) convertView
				.findViewById(R.id.list_item_txtPath);
		 list_item_txtTitle = (TextView) convertView
				.findViewById(R.id.list_item_txtTitle);
		 list_item_txtAlbum = (TextView) convertView
				.findViewById(R.id.list_item_txtAlbum);
		 list_item_imgChecked = (ImageView) convertView
				.findViewById(R.id.list_item_imgChecked);
	}
}
