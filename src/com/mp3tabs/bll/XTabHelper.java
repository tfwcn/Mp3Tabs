package com.mp3tabs.bll;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mp3tabs.bll.XListHelper.XCheck;
import com.mp3tabs.model.XCharsetEnum;
import com.mp3tabs.model.XEditInfo;
import com.mp3tabs.model.XFileInfo;
import com.mp3tabs.model.XtagID3V1;
import com.mp3tabs.model.XtagID3V2FrameHeader;
import com.mp3tabs.model.XtagID3V2Header;

public class XTabHelper {
	public HashMap<String, byte[]> tags = new HashMap<String, byte[]>();
	public XListHelper<XFileInfo> listHelper = new XListHelper<XFileInfo>();
	public XCharsetEnum charset;

	public void load(XFileInfo xFileInfo, XCharsetEnum charset)
			throws Exception {
		this.charset = charset;// 记录编码
		tags.clear();// 清空标签列表
		XFileStreamReader xfreader = new XFileStreamReader(xFileInfo.filePath
				+ xFileInfo.oldName);// 读取文件流
		xFileInfo.oldName = xFileInfo.oldName.substring(0,
				xFileInfo.oldName.length() - 4);
		xFileInfo.newName = xFileInfo.oldName;
		// File file = xfreader.getXfile();// 文件信息
		// xFileInfo.setFilePath(file.getPath().substring(0,
		// file.getPath().lastIndexOf("/") + 1));
		// xFileInfo.setOldName(file.getName());
		// xFileInfo.setNewName(file.getName());
		// =============读取ID3V1=============
		xfreader.xSeek(xfreader.length() - 128);
		XtagID3V1 id3v1 = new XtagID3V1();
		id3v1.Header = xfreader.xReadBytes(3);
		if (XByteArrayHelper.getString(id3v1.Header, XCharsetEnum.ASCII)
				.equals("TAG")) {
			id3v1.Title = xfreader.xReadBytes(30);
			id3v1.Artist = xfreader.xReadBytes(30);
			id3v1.Album = xfreader.xReadBytes(30);
			id3v1.Year = xfreader.xReadBytes(4);
			id3v1.Comment = xfreader.xReadBytes(28);
			id3v1.reserve = xfreader.xReadByte();
			id3v1.track = xfreader.xReadByte();
			id3v1.Genre = xfreader.xReadByte();
			// 设置歌名
			String titlestr = XByteArrayHelper.getString(
					XByteArrayHelper.copyByteArray(
							id3v1.Title,
							0,
							0,
							id3v1.Title.length,
							id3v1.Title.length
									+ XByteArrayHelper.getBytes("\0",
											charset.name()).length), charset)
					.trim();
			xFileInfo.oldTitle = titlestr;
			xFileInfo.newTitle = titlestr;
			// 设置歌手
			String artiststr = XByteArrayHelper.getString(
					XByteArrayHelper.copyByteArray(
							id3v1.Artist,
							0,
							0,
							id3v1.Artist.length,
							id3v1.Artist.length
									+ XByteArrayHelper.getBytes("\0",
											charset.name()).length), charset)
					.trim();
			xFileInfo.oldArtist = artiststr;
			xFileInfo.newArtist = artiststr;
			// 设置专辑
			String albumstr = XByteArrayHelper.getString(
					XByteArrayHelper.copyByteArray(
							id3v1.Album,
							0,
							0,
							id3v1.Album.length,
							id3v1.Album.length
									+ XByteArrayHelper.getBytes("\0",
											charset.name()).length), charset)
					.trim();
			xFileInfo.oldAlbum = albumstr;
			xFileInfo.newAlbum = albumstr;
		}
		// =============读取ID3V2=============
		XtagID3V2Header id3v2Header = new XtagID3V2Header();
		xfreader.xSeek(0);
		id3v2Header.Header = xfreader.xReadBytes(3);
		if (XByteArrayHelper.getString(id3v2Header.Header, XCharsetEnum.ASCII)
				.equals("ID3")) {
			id3v2Header.Ver = xfreader.xReadByte();
			id3v2Header.Revision = xfreader.xReadByte();
			id3v2Header.Flag = xfreader.xReadByte();
			id3v2Header.Size = xfreader.xReadBytes(4);
			int len = (id3v2Header.Size[0] & 0x7F) << 21;
			len += (id3v2Header.Size[1] & 0x7F) << 14;
			len += (id3v2Header.Size[2] & 0x7F) << 7;
			len += (id3v2Header.Size[3] & 0x7F);

			int maxsize = 0;
			while (maxsize < len) {
				XtagID3V2FrameHeader id3v2fh = new XtagID3V2FrameHeader();
				id3v2fh.FrameID = xfreader.xReadBytes(4);
				id3v2fh.Size = xfreader.xReadBytes(4);
				id3v2fh.Flags = xfreader.xReadBytes(2);

				int size = XByteArrayHelper.getInt(id3v2fh.Size);
				if (size <= 0 || maxsize + size + 10 > len)
					break;
				maxsize += size + 10;
				tags.put(XByteArrayHelper.getString(id3v2fh.FrameID,
						XCharsetEnum.ASCII), xfreader.xReadBytes(size));
			}
			String TIT2 = getTagToString("TIT2").trim();
			if (TIT2 != "") {
				xFileInfo.oldTitle = TIT2;// 歌名
				xFileInfo.newTitle = TIT2;// 歌名
			}
			String TPE1 = getTagToString("TPE1").trim();
			if (TPE1 != "") {
				xFileInfo.oldArtist = TPE1;// 歌手
				xFileInfo.newArtist = TPE1;// 歌手
			}
			String TALB = getTagToString("TALB").trim();
			if (TALB != "") {
				xFileInfo.oldAlbum = TALB;// 专辑
				xFileInfo.newAlbum = TALB;// 专辑
			}
		}
		xfreader.xClose();
		xfreader = null;
		xFileInfo.charset = this.charset;
	}

	public String getTagToString(String frameID) throws Exception {
		if (tags.containsKey(frameID)) {
			byte[] bytes = tags.get(frameID);
			switch (bytes[0]) {
			case 0:
				if (this.charset != null) {
					return XByteArrayHelper
							.getString(XByteArrayHelper.copyByteArray(bytes, 1,
									bytes.length - 1), charset);
				}
				this.charset = XCharsetEnum.ISO_8859_1;
				return XByteArrayHelper.getString(XByteArrayHelper
						.copyByteArray(bytes, 1, bytes.length - 1),
						XCharsetEnum.ISO_8859_1);
			case 1:
				this.charset = XCharsetEnum.UTF_16;
				return XByteArrayHelper.getString(XByteArrayHelper
						.copyByteArray(bytes, 1, bytes.length - 1),
						XCharsetEnum.UTF_16);
			case 2:
				this.charset = XCharsetEnum.UTF_16BE;
				return XByteArrayHelper.getString(XByteArrayHelper
						.copyByteArray(bytes, 1, bytes.length - 1),
						XCharsetEnum.UTF_16BE);
			case 3:
				this.charset = XCharsetEnum.UTF_8;
				return XByteArrayHelper.getString(XByteArrayHelper
						.copyByteArray(bytes, 1, bytes.length - 1),
						XCharsetEnum.UTF_8);
			default:
				this.charset = XCharsetEnum.GBK;
				return XByteArrayHelper.getString(XByteArrayHelper
						.copyByteArray(bytes, 1, bytes.length - 1),
						XCharsetEnum.GBK);
			}
		}
		return "";
	}

	public void saveTags(XFileInfo xFileInfo, XCharsetEnum charset)
			throws Exception {
		if (charset == null) {
			this.charset = xFileInfo.charset;
		} else {
			this.charset = charset;// 记录编码
		}
		tags.clear();// 清空标签列表
		XFileStreamReader xfreader = new XFileStreamReader(xFileInfo.filePath
				+ xFileInfo.oldName + ".mp3");// 读取文件流
		XFileStreamWriter xfwriter = new XFileStreamWriter(xFileInfo.filePath
				+ "Mp3Tabs"+xFileInfo.index+".tmp");// 写入文件流

		// =============读取ID3V1=============
		xfreader.xSeek(xfreader.length() - 128);
		XtagID3V1 id3v1 = new XtagID3V1();
		id3v1.Header = xfreader.xReadBytes(3);
		boolean hasid3v1 = false;
		if (XByteArrayHelper.getString(id3v1.Header, XCharsetEnum.ASCII)
				.equals("TAG")) {
			id3v1.Title = xfreader.xReadBytes(30);
			id3v1.Artist = xfreader.xReadBytes(30);
			id3v1.Album = xfreader.xReadBytes(30);
			id3v1.Year = xfreader.xReadBytes(4);
			id3v1.Comment = xfreader.xReadBytes(28);
			id3v1.reserve = xfreader.xReadByte();
			id3v1.track = xfreader.xReadByte();
			id3v1.Genre = xfreader.xReadByte();
			hasid3v1 = true;
		}
		// =============读取ID3V2=============
		int maxoldsize = 0;
		int id3v2len = 0;
		boolean hasid3v2 = false;
		XtagID3V2Header id3v2Header = new XtagID3V2Header();
		xfreader.xSeek(0);
		id3v2Header.Header = xfreader.xReadBytes(3);
		if (XByteArrayHelper.getString(id3v2Header.Header, XCharsetEnum.ASCII)
				.equals("ID3")) {
			id3v2Header.Ver = xfreader.xReadByte();
			id3v2Header.Revision = xfreader.xReadByte();
			id3v2Header.Flag = xfreader.xReadByte();
			id3v2Header.Size = xfreader.xReadBytes(4);
			id3v2len = (id3v2Header.Size[0] & 0x7F) << 21;
			id3v2len += (id3v2Header.Size[1] & 0x7F) << 14;
			id3v2len += (id3v2Header.Size[2] & 0x7F) << 7;
			id3v2len += (id3v2Header.Size[3] & 0x7F);

			while (maxoldsize < id3v2len) {
				XtagID3V2FrameHeader id3v2fh = new XtagID3V2FrameHeader();
				id3v2fh.FrameID = xfreader.xReadBytes(4);
				id3v2fh.Size = xfreader.xReadBytes(4);
				id3v2fh.Flags = xfreader.xReadBytes(2);

				int size = XByteArrayHelper.getInt(id3v2fh.Size);
				if (size <= 0)
					break;
				maxoldsize += size + 10;
				tags.put(XByteArrayHelper.getString(id3v2fh.FrameID,
						XCharsetEnum.ASCII), xfreader.xReadBytes(size));
			}
			if (xFileInfo.newTitle.length() > 0) {
				setTagByString("TIT2", xFileInfo.newTitle, this.charset);
			} else {
				tags.remove("TIT2");
			}
			if (xFileInfo.newArtist.length() > 0) {
				setTagByString("TPE1", xFileInfo.newArtist, this.charset);
			} else {
				tags.remove("TPE1");
			}
			if (xFileInfo.newAlbum.length() > 0) {
				setTagByString("TALB", xFileInfo.newAlbum, this.charset);
			} else {
				tags.remove("TALB");
			}
			id3v2len += 10;
			hasid3v2 = true;
		}

		// =============写入ID3V2=============
		if (hasid3v2 == false) {
			id3v2Header.Header = XByteArrayHelper.getBytes("ID3",
					XCharsetEnum.ASCII);
		}
		byte[] id3v2bytes = new byte[0];
		id3v2bytes = XByteArrayHelper.joinByteArray(id3v2bytes,
				id3v2Header.Header);
		id3v2bytes = XByteArrayHelper
				.joinByteArray(id3v2bytes, id3v2Header.Ver);
		id3v2bytes = XByteArrayHelper.joinByteArray(id3v2bytes,
				id3v2Header.Revision);
		id3v2bytes = XByteArrayHelper.joinByteArray(id3v2bytes,
				id3v2Header.Flag);
		id3v2bytes = XByteArrayHelper.joinByteArray(id3v2bytes,
				id3v2Header.Size);
		int maxnewsize = 0;
		for (String key : tags.keySet()) {
			byte[] fhdata = tags.get(key);
			XtagID3V2FrameHeader id3v2fh = new XtagID3V2FrameHeader();
			id3v2fh.FrameID = XByteArrayHelper
					.getBytes(key, XCharsetEnum.ASCII);
			id3v2fh.Size = XByteArrayHelper.getBytes(fhdata.length);
			id3v2bytes = XByteArrayHelper.joinByteArray(id3v2bytes,
					id3v2fh.FrameID);
			id3v2bytes = XByteArrayHelper.joinByteArray(id3v2bytes,
					id3v2fh.Size);
			id3v2bytes = XByteArrayHelper.joinByteArray(id3v2bytes,
					id3v2fh.Flags);
			id3v2bytes = XByteArrayHelper.joinByteArray(id3v2bytes, fhdata);
			maxnewsize += fhdata.length + 10;
		}
		byte[] maxnewsizeBytes = new byte[4];
		maxnewsizeBytes[3] = (byte) (maxnewsize << 25 >>> 1 >> 24);
		maxnewsizeBytes[2] = (byte) (maxnewsize >> 7 << 25 >>> 1 >> 24);
		maxnewsizeBytes[1] = (byte) (maxnewsize >> 14 << 25 >>> 1 >> 24);
		maxnewsizeBytes[0] = (byte) (maxnewsize >> 21 << 25 >>> 1 >> 24);
		XByteArrayHelper.copyArray(maxnewsizeBytes, 0, id3v2bytes, 6, 4);
		xfwriter.xWriteBytes(id3v2bytes);
		// =============读取并写入歌曲主体=============
		xfreader.xSeek(id3v2len);
		long maxLength = (xfreader.length() - id3v2len - (hasid3v1 ? 128 : 0));// 歌曲主体总长度
		long readLength = 0;// 已读长度
		while (readLength < maxLength) {
			int subLength = 1024 * 1024;// 1M数据
			if (readLength + subLength > maxLength)
				subLength = (int) (maxLength - readLength);
			byte[] bodybytes = xfreader.xReadBytes(subLength);
			xfwriter.xWriteBytes(bodybytes);
			readLength += subLength;
			bodybytes = null;
		}

		// =============写入ID3V1=============
		if (!hasid3v1) {
			id3v1.Header = XByteArrayHelper.getBytes("TAG", XCharsetEnum.ASCII);
			xfwriter.xWriteBytes(id3v1.Header);
		}
		id3v1.Title = XByteArrayHelper.copyByteArray(
				XByteArrayHelper.getBytes(xFileInfo.newTitle, this.charset), 0,
				0, 30);
		id3v1.Artist = XByteArrayHelper.copyByteArray(
				XByteArrayHelper.getBytes(xFileInfo.newArtist, this.charset),
				0, 0, 30);
		id3v1.Album = XByteArrayHelper.copyByteArray(
				XByteArrayHelper.getBytes(xFileInfo.newAlbum, this.charset), 0,
				0, 30);
		xfwriter.xWriteBytes(id3v1.Album);
		xfwriter.xWriteBytes(id3v1.Year);
		xfwriter.xWriteBytes(id3v1.Comment);
		xfwriter.xWriteByte(id3v1.reserve);
		xfwriter.xWriteByte(id3v1.track);
		xfwriter.xWriteByte(id3v1.Genre);

		xfreader.xClose();// 关闭读取流
		xfreader = null;
		xfwriter.xClose();// 关闭写入流
		xfwriter = null;
		// 重命名新文件
		renameTo(xFileInfo.filePath + "Mp3Tabs"+xFileInfo.index+".tmp", xFileInfo.filePath
				+ xFileInfo.newName + ".mp3");
		// 删除临时文件
		delFile(xFileInfo.filePath + "Mp3Tabs"+xFileInfo.index+".tmp");
		xFileInfo.oldTitle = xFileInfo.newTitle;
		xFileInfo.oldAlbum = xFileInfo.newAlbum;
		xFileInfo.oldArtist = xFileInfo.newArtist;
	}

	public void setTagByString(String frameID, String value,
			XCharsetEnum charset) throws Exception {
		if (this.charset != null) {
			charset = this.charset;
		}
		byte[] bytes = XByteArrayHelper.getBytes(value, charset);
		byte[] newbytes = XByteArrayHelper.copyByteArray(bytes, 0, 1,
				bytes.length, bytes.length + 1);
		switch (charset) {
		case ISO_8859_1:
			newbytes[0] = 0;
			break;
		case UTF_16:
			newbytes[0] = 1;
			break;
		case UTF_16BE:
			newbytes[0] = 2;
			break;
		case UTF_8:
			newbytes[0] = 3;
			break;
		default:
			newbytes[0] = 0;
			break;
		}
		tags.put(frameID, newbytes);
	}

	public void renameTo(String oldname, String newName) {
		new File(oldname).renameTo(new File(newName));
	}

	public void delFile(String name) {
		new File(name).delete();
	}

	private static List<String> GetFx(String str) throws Exception {
		List<String> strList = new ArrayList<String>();
		strList.add("");
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '%' && i + 1 < str.length()
					&& str.charAt(i + 1) >= '0' && str.charAt(i + 1) <= '9') {
				if (strList.contains("%" + str.charAt(i + 1))) {
					throw new Exception("关键字 %" + str.charAt(i + 1) + " 重复!");
				}
				strList.add("%" + str.charAt(i + 1));
				strList.add("");
				i++;
				continue;
			}
			strList.set(strList.size() - 1, strList.get(strList.size() - 1)
					.concat(str.charAt(i) + ""));
		}
		return strList;
	}

	private static HashMap<String, String> GetKeyList(String strfx,
			String strval) throws Exception {
		List<String> strList = GetFx(strfx);
		String val = strval;
		HashMap<String, String> valList = new HashMap<String, String>();
		for (int i = 0; i < strList.size(); i++) {
			if (strList.get(i).length() == 2 && strList.get(i).charAt(0) == '%'
					&& strList.get(i).charAt(1) >= '0'
					&& strList.get(i).charAt(1) <= '9') {
				int index = -1;
				if (i + 1 < strList.size() && strList.get(i + 1).length() > 0) {
					index = val.indexOf(strList.get(i + 1));
				}
				if (index >= 0) {
					valList.put(strList.get(i), val.substring(0, index));
					val = val.substring(index);
				} else {
					valList.put(strList.get(i), val);
					val = "";
				}
			} else {
				if (val.indexOf(strList.get(i)) != 0) {
					break;
				} else {
					val = val.substring(strList.get(i).length());
				}
			}
		}
		return valList;
	}

	public void changeTabs(XFileInfo xFileInfo, XEditInfo xEditInfo)
			throws Exception {
		HashMap<String, String> nameList = GetKeyList(xEditInfo.fxName,
				xFileInfo.oldName);
		String newTitle = xEditInfo.fxTitle;
		String newArtist = xEditInfo.fxArtist;
		String newAlbum = xEditInfo.fxAlbum;
		for (String k : nameList.keySet()) {
			newTitle = newTitle.replace(k, nameList.get(k));
			newArtist = newArtist.replace(k, nameList.get(k));
			newAlbum = newAlbum.replace(k, nameList.get(k));
		}
		if (xEditInfo.chkTitle)
			xFileInfo.newTitle = newTitle.trim();
		if (xEditInfo.chkArtist)
			xFileInfo.newArtist = newArtist.trim();
		if (xEditInfo.chkAlbum)
			xFileInfo.newAlbum = newAlbum.trim();
		if (xFileInfo.newTitle.length() == 0
				|| xFileInfo.newArtist.length() == 0
				|| xFileInfo.newAlbum.length() == 0) {
			xFileInfo.hasNull = true;
		} else {
			xFileInfo.hasNull = false;
		}
	}

	public void changeName(XFileInfo xFileInfo, XEditInfo xEditInfo)
			throws Exception {
		HashMap<String, String> titleList = GetKeyList(xEditInfo.fxTitle,
				xFileInfo.oldTitle);
		HashMap<String, String> artistList = GetKeyList(xEditInfo.fxArtist,
				xFileInfo.oldArtist);
		HashMap<String, String> albumList = GetKeyList(xEditInfo.fxAlbum,
				xFileInfo.oldAlbum);
		List<String> tmpList = new ArrayList<String>();
		String newName = xEditInfo.fxName;
		for (String k : titleList.keySet()) {
			if (tmpList.contains(k)) {
				throw new Exception("关键字 " + k + " 重複!");
			}
			tmpList.add(k);
			newName = newName.replace(k, titleList.get(k));
		}
		for (String k : artistList.keySet()) {
			if (tmpList.contains(k)) {
				throw new Exception("关键字 " + k + " 重複!");
			}
			tmpList.add(k);
			newName = newName.replace(k, artistList.get(k));
		}
		for (String k : albumList.keySet()) {
			if (tmpList.contains(k)) {
				throw new Exception("关键字 " + k + " 重複!");
			}
			tmpList.add(k);
			newName = newName.replace(k, albumList.get(k));
		}
		newName = newName.trim();
		xFileInfo.newName = newName;
	}

	public boolean CheckFileList(ArrayList<XFileInfo> selectFileList)
			throws IllegalAccessException, InstantiationException {
		for (XFileInfo xFileInfo : selectFileList) {
			xFileInfo.isEnabled = true;
			xFileInfo.isError = false;
			xFileInfo.errorMsg = "";
			if (xFileInfo.oldName.equals(xFileInfo.newName)) {
				// 文件名没变化，不用修改
				xFileInfo.isNewChecked = false;
				xFileInfo.isEnabled = false;
			} else if (xFileInfo.newName.length() == 0) {// 文件名为空
				xFileInfo.isError = true;
				xFileInfo.errorMsg = "文件名为空，可長按進行修改！";
			} else if (xFileInfo.newName
					.matches("[^/\\\\<>*?|\"]+\\.[^/\\\\<>*?|\"]+")) {
				xFileInfo.isError = true;
				xFileInfo.errorMsg = "文件名含非法字符，可長按進行修改！";
			} else if (listHelper.FindAll(selectFileList,
					new XCheck<XFileInfo>(xFileInfo) {
						@Override
						public boolean check(XFileInfo t) {
							return t.newName
									.equalsIgnoreCase(this.oldObject.newName);
						}
					}).size() > 1) {// 文件名在列表中存在重複，可長按進行修改
				xFileInfo.isError = true;
				xFileInfo.errorMsg = "文件名在列表中存在重複，可長按進行修改！";
			} else if (xFileInfo.isError = false && new File(xFileInfo.filePath
					+ xFileInfo.newName + ".mp3").exists()) {// 列表中无重复，同时文件名改变了，存储位置中存在同名文件
				xFileInfo.isError = true;
				xFileInfo.errorMsg = "同文件夹中存在同名文件，可長按進行修改！";
			}
			if (xFileInfo.isError) {
				xFileInfo.isNewChecked = false;
			}
		}
		return !listHelper.Existed(selectFileList, new XCheck<XFileInfo>() {
			@Override
			public boolean check(XFileInfo t) {
				return t.isError;
			}
		});
	}

	public void saveFileName(XFileInfo xFileInfo) {
		this.renameTo(xFileInfo.filePath + xFileInfo.oldName + ".mp3",
				xFileInfo.filePath + xFileInfo.newName + ".mp3");
		xFileInfo.oldName = xFileInfo.newName;
	}

	public void RenameFileList(ArrayList<XFileInfo> selectFileList) {
		for (XFileInfo xFileInfo : selectFileList) {
			if (!xFileInfo.isNewChecked) {
				return;
			}
			XFileInfo tmpXFileInfo = listHelper.Find(selectFileList,
					new XCheck<XFileInfo>(xFileInfo) {
						@Override
						public boolean check(XFileInfo t) {
							if (t.isNewChecked
									&& this.oldObject.newName.equals(t.oldName)) {
								return true;
							}
							return false;
						}
					});
			if (tmpXFileInfo != null) {
				renameTo(tmpXFileInfo.oldName, tmpXFileInfo.oldName + "2");
				tmpXFileInfo.oldName = tmpXFileInfo.oldName + "2";
			}
		}
	}
}
