package com.mp3tabs.model;

public class XtagID3V1 {
    public byte[] Header=new byte[3];
    public byte[] Title=new byte[30];
    public byte[] Artist=new byte[30];
    public byte[] Album=new byte[30];
    public byte[] Year=new byte[4];
    public byte[] Comment=new byte[28];
    public byte reserve;
    public byte track;
    public byte Genre;
}
