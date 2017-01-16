package com.mp3tabs.bll;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.example.mp3tabs.R;
import com.mp3tabs.model.XFileInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class XSelectItemAdapter extends BaseAdapter {
	private ArrayList<XFileInfo> showFileList;
	private ArrayList<XFileInfo> selectFileList;
	private LayoutInflater inflater;
	private TextView select_view_txtPath;
	private Comparator<XFileInfo> comparator = new Comparator<XFileInfo>() {

		@Override
		public int compare(XFileInfo lhs, XFileInfo rhs) {
			if (lhs.isDirectory == true && rhs.isDirectory == true)
				return lhs.oldName.compareToIgnoreCase(rhs.oldName);
			else if (lhs.isDirectory == true && rhs.isDirectory == false)
				return -1;
			else if (lhs.isDirectory == false && rhs.isDirectory == true)
				return 1;
			else
				return lhs.oldName.compareToIgnoreCase(rhs.oldName);
		}
	};

	public ArrayList<XFileInfo> getSelectFileList() {
		return selectFileList;
	}

	public void setSelectFileList(ArrayList<XFileInfo> selectFileList) {
		this.selectFileList = selectFileList;
	}

	public XSelectItemAdapter(Context context,
			ArrayList<XFileInfo> showFileList,
			ArrayList<XFileInfo> selectFileList, TextView select_view_txtPath) {
		this.showFileList = showFileList;
		this.selectFileList = selectFileList;
		this.select_view_txtPath = select_view_txtPath;
		this.inflater = LayoutInflater.from(context);

		findFiles();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return showFileList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return showFileList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = this.inflater.inflate(R.layout.select_item, null);
		}
		convertView.setTag(showFileList.get(position));
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				XFileInfo file = (XFileInfo) v.getTag();
				if (file.isDirectory) {
					if (file.oldName.equals("..")) {
						select_view_txtPath.setText(file.filePath
								.substring(0, file.filePath.length() - 1));
					} else {
						select_view_txtPath.setText(file.filePath
								+ file.oldName);
					}
					findFiles();
				} else {
					file.isOldChecked=!file.isOldChecked;
					ImageView select_item_imgChecked = (ImageView) v
							.findViewById(R.id.select_item_imgChecked);
					if (file.isOldChecked) {
						select_item_imgChecked
								.setImageResource(R.drawable.popupbox_checkbox_checked);
					} else {
						select_item_imgChecked
								.setImageResource(R.drawable.popupbox_checkbox_unchecked);
					}
				}
			}
		});
		TextView select_item_txtFileName = (TextView) convertView
				.findViewById(R.id.select_item_txtFileName);
		ImageView select_item_imgChecked = (ImageView) convertView
				.findViewById(R.id.select_item_imgChecked);
		ImageView select_item_imgFileImg = (ImageView) convertView
				.findViewById(R.id.select_item_imgFileImg);
		select_item_txtFileName
				.setText(showFileList.get(position).oldName);
		select_item_imgChecked.setImageResource(showFileList.get(position)
				.isOldChecked ? R.drawable.popupbox_checkbox_checked
				: R.drawable.popupbox_checkbox_unchecked);
		select_item_imgChecked.setVisibility(showFileList.get(position)
				.isDirectory ? View.INVISIBLE : View.VISIBLE);
		select_item_imgFileImg.setImageResource(showFileList.get(position)
				.isDirectory ? R.drawable.toolbar_edit_view
				: R.drawable.toolbar_music);
		return convertView;
	}

	private void findFiles() {
		showFileList.clear();
		File file = new File(select_view_txtPath.getText().toString());
		if (!file.getPath().equals("/")) {
			XFileInfo retFileInfo = new XFileInfo();
			retFileInfo.filePath=file.getParent() + "/";
			retFileInfo.oldName="..";
			retFileInfo.newName="..";
			retFileInfo.isDirectory=file.isDirectory();
			showFileList.add(retFileInfo);
		}
		File[] fileList = file.listFiles();
		if (fileList == null)
			return;
		for (File f : fileList) {
			if (f.isDirectory()
					|| (f.getName().length() > 4 && f.getName()
							.substring(f.getName().length() - 4)
							.equalsIgnoreCase(".mp3"))) {
				XFileInfo newFileInfo = new XFileInfo();
				newFileInfo.filePath=leftLast(f.getAbsolutePath()) + "/";
				newFileInfo.oldName=f.getName();
				newFileInfo.newName=f.getName();
				newFileInfo.isDirectory=f.isDirectory();
				showFileList.add(newFileInfo);
			}
		}
		Collections.sort(showFileList, comparator);
		this.notifyDataSetChanged();
	}

	private String leftLast(String s) {
		return s.substring(0, s.lastIndexOf("/"));
	}

	public boolean BackPath() {
		if (showFileList.get(0).oldName == "..") {
			select_view_txtPath.setText(showFileList
					.get(0)
					.filePath
					.substring(0,
							showFileList.get(0).filePath.length() - 1));
			findFiles();
			return true;
		}
		return false;
	}
}
