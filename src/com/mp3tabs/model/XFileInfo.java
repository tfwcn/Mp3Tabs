package com.mp3tabs.model;

import java.io.Serializable;

public class XFileInfo implements Serializable{
	private static final long serialVersionUID = 157868436714994380L;
	public String oldName="";//文件名
	public String newName="";
	public String filePath="";//文件地址
	public String oldTitle="";//歌名
	public String newTitle="";
	public String oldArtist="";//歌手
	public String newArtist="";
	public String oldAlbum="";//专辑
    public String newAlbum="";
    public String errorMsg="";//错误信息
    public boolean isError;
    public boolean hasNull;
    public boolean isOldChecked;
    public boolean isNewChecked;
    public boolean isEnabled;
    public boolean isDirectory;
    public XCharsetEnum charset;
    public int index;
}
