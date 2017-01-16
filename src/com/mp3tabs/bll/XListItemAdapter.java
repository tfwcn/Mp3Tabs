package com.mp3tabs.bll;

import java.util.ArrayList;

import com.example.mp3tabs.R;
import com.example.mp3tabs.SaveActivity;
import com.mp3tabs.model.XFileInfo;
import com.mp3tabs.model.XListItem;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class XListItemAdapter extends BaseAdapter {
	private ArrayList<XFileInfo> fileList;
	private LayoutInflater inflater;
	private boolean isnew = false;
	private Context context;
	private XListItem xListItem;

	public ArrayList<XFileInfo> getFileList() {
		return fileList;
	}

	public void setFileList(ArrayList<XFileInfo> fileList) {
		this.fileList = fileList;
	}

	public boolean isIsnew() {
		return isnew;
	}

	public void setIsnew(boolean isnew) {
		this.isnew = isnew;
	}

	public XListItemAdapter(Context context, ArrayList<XFileInfo> fileList,
			boolean isnew) {
		this.context = context;
		this.fileList = fileList;
		this.inflater = LayoutInflater.from(context);
		this.isnew = isnew;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fileList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return fileList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = this.inflater.inflate(R.layout.list_item, null);
			xListItem = new XListItem(convertView);
			convertView.setTag(xListItem);
		} else {
			xListItem = (XListItem) convertView.getTag();
		}
		xListItem.file = fileList.get(position);
		// 列表項點擊
		/*
		 * convertView.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { if (!isnew) { //编辑界面
		 * XFileInfo file=(XFileInfo)v.getTag(); ImageView imgChecked =
		 * (ImageView) v .findViewById(R.id.list_item_imgChecked);
		 * file.isOldChecked=!file.isOldChecked; if (file.isOldChecked) {
		 * imgChecked .setImageResource(R.drawable.popupbox_checkbox_checked); }
		 * else { imgChecked
		 * .setImageResource(R.drawable.popupbox_checkbox_unchecked); } } } });
		 */
		// 列表项视图点击
		xListItem.list_item_view.setTag(xListItem);
		xListItem.list_item_view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				XListItem tmpXListItem = (XListItem) v.getTag();
				XFileInfo file = tmpXListItem.file;
				if (isnew) {
					// 编辑界面
					SaveActivity sa = (SaveActivity) context;
					if (file.isError&&sa.getxEditInfo().changeType.equals("改文件名")) {//有错误
						file.isNewChecked = false;
						sa.ShowMsg(file.errorMsg);
					}
					else if (file.isEnabled==false&&sa.getxEditInfo().changeType.equals("改文件名")) {//文件名没变化，无需修改
						file.isNewChecked = false;
						sa.ShowMsg("文件名没变化，无需修改！");
					} else {
						ImageView imgChecked = (ImageView) tmpXListItem.list_item_imgChecked;
						file.isNewChecked = !file.isNewChecked;
						if (file.isNewChecked) {
							imgChecked
									.setImageResource(R.drawable.popupbox_checkbox_checked);
						} else {
							imgChecked
									.setImageResource(R.drawable.popupbox_checkbox_unchecked);
						}
					}
				} else {
					// 编辑界面
					ImageView imgChecked = (ImageView) tmpXListItem.convertView
							.findViewById(R.id.list_item_imgChecked);
					file.isOldChecked = !file.isOldChecked;
					if (file.isOldChecked) {
						imgChecked
								.setImageResource(R.drawable.popupbox_checkbox_checked);
					} else {
						imgChecked
								.setImageResource(R.drawable.popupbox_checkbox_unchecked);
					}
				}
			}
		});
		xListItem.list_item_view
				.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						// 长按弹出编辑框
						XListItem tmpXListItem = (XListItem) v.getTag();
						if (isnew) {
							// 编辑界面
							SaveActivity sa = (SaveActivity) context;
							sa.ShowEditTag(tmpXListItem.file);
						}
						return true;
					}
				});
		// 多選框點擊
		xListItem.list_item_imgChecked.setTag(xListItem);
		xListItem.list_item_imgChecked
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						XListItem tmpXListItem = (XListItem) v.getTag();
						XFileInfo file = tmpXListItem.file;
						if (isnew) {
							// 保存界面
								SaveActivity sa = (SaveActivity) context;
							if (file.isError&&sa.getxEditInfo().changeType.equals("改文件名")) {//有错误
								file.isNewChecked = false;
								sa.ShowMsg(file.errorMsg);
							}
							else if (file.isEnabled==false&&sa.getxEditInfo().changeType.equals("改文件名")) {//文件名没变化，无需修改
								file.isNewChecked = false;
								sa.ShowMsg("文件名没变化，无需修改！");
							} else {
								ImageView imgChecked = (ImageView) v;
								file.isNewChecked = !file.isNewChecked;
								if (file.isNewChecked) {
									imgChecked
											.setImageResource(R.drawable.popupbox_checkbox_checked);
								} else {
									imgChecked
											.setImageResource(R.drawable.popupbox_checkbox_unchecked);
								}
							}
						} else {
							// 编辑界面
							ImageView imgChecked = (ImageView) v;
							file.isOldChecked = !file.isOldChecked;
							if (file.isOldChecked) {
								imgChecked
										.setImageResource(R.drawable.popupbox_checkbox_checked);
							} else {
								imgChecked
										.setImageResource(R.drawable.popupbox_checkbox_unchecked);
							}
						}
					}
				});
		xListItem.list_item_txtPath.setText(fileList.get(position).filePath);
		if (isnew) {
			SaveActivity sa = (SaveActivity) context;
			if (sa.getxEditInfo().changeType.equals("改文件名")) {
				if (xListItem.file.isError) {
					xListItem.list_item_txtFileName.setTextColor(Color.RED);
				} else if (!xListItem.file.isEnabled) {
					xListItem.list_item_txtFileName.setTextColor(Color.GREEN);
				} else {
					xListItem.list_item_txtFileName.setTextColor(Color.BLACK);
				}
				String oldName = fileList.get(position).oldName;
				xListItem.list_item_txtFileName
						.setText(fileList.get(position).newName + "【"
								+ (oldName.length() == 0 ? "" : oldName) + "】");
			} else {
				xListItem.list_item_txtFileName
						.setText(fileList.get(position).newName);
			}
			xListItem.list_item_txtTitle
					.setText(fileList.get(position).newTitle + "【"
							+ fileList.get(position).newArtist + "】");
			xListItem.list_item_txtAlbum
					.setText(fileList.get(position).newAlbum);
			xListItem.list_item_imgChecked
					.setImageResource(fileList.get(position).isNewChecked ? R.drawable.popupbox_checkbox_checked
							: R.drawable.popupbox_checkbox_unchecked);
		} else {
			xListItem.list_item_txtFileName
					.setText(fileList.get(position).oldName);
			xListItem.list_item_txtTitle
					.setText(fileList.get(position).oldTitle + "【"
							+ fileList.get(position).oldArtist + "】");
			xListItem.list_item_txtAlbum
					.setText(fileList.get(position).oldAlbum);
			xListItem.list_item_imgChecked
					.setImageResource(fileList.get(position).isOldChecked ? R.drawable.popupbox_checkbox_checked
							: R.drawable.popupbox_checkbox_unchecked);
		}
		return convertView;
	}

}
