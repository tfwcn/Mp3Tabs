package com.mp3tabs.model;

import java.io.Serializable;

public class XEditInfo implements Serializable{
	private static final long serialVersionUID = -7482897968987759356L;
	public String changeType = "";
	public String fxName = "%2 - %1";
	public String fxTitle = "%1";//歌名
	public String fxArtist = "%2";//歌手
	public String fxAlbum = "%3";//专辑
	public boolean chkName = true;
	public boolean chkTitle = true;
	public boolean chkArtist = true;
	public boolean chkAlbum = false;
	public XCharsetEnum saveCharset=null;
	public boolean isAD=false;
}
