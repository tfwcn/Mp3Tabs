package com.mp3tabs.bll;

import com.mp3tabs.model.XCharsetEnum;

public class XByteArrayHelper {
	public static byte[] getBytes(short data) {
		byte[] bytes = new byte[2];
		bytes[1] = (byte) (data & 0xff);
		bytes[0] = (byte) ((data & 0xff00) >> 8);
		return bytes;
	}

	public static byte[] getBytes(char data) {
		byte[] bytes = new byte[2];
		bytes[1] = (byte) (data);
		bytes[0] = (byte) (data >> 8);
		return bytes;
	}

	public static byte[] getBytes(int data) {
		byte[] bytes = new byte[4];
		bytes[3] = (byte) (data & 0xff);
		bytes[2] = (byte) ((data & 0xff00) >> 8);
		bytes[1] = (byte) ((data & 0xff0000) >> 16);
		bytes[0] = (byte) ((data & 0xff000000) >> 24);
		return bytes;
	}

	public static byte[] getBytes(long data) {
		byte[] bytes = new byte[8];
		bytes[7] = (byte) (data & 0xff);
		bytes[6] = (byte) ((data >> 8) & 0xff);
		bytes[5] = (byte) ((data >> 16) & 0xff);
		bytes[4] = (byte) ((data >> 24) & 0xff);
		bytes[3] = (byte) ((data >> 32) & 0xff);
		bytes[2] = (byte) ((data >> 40) & 0xff);
		bytes[1] = (byte) ((data >> 48) & 0xff);
		bytes[0] = (byte) ((data >> 56) & 0xff);
		return bytes;
	}

	public static byte[] getBytes(float data) {
		int intBits = Float.floatToIntBits(data);
		return getBytes(intBits);
	}

	public static byte[] getBytes(double data) {
		long intBits = Double.doubleToLongBits(data);
		return getBytes(intBits);
	}

	public static byte[] getBytes(String data, XCharsetEnum charset)
			throws Exception {
		return getBytes(data, charset.name());
	}

	public static byte[] getBytes(String data, String charsetName)
			throws Exception {
		return data.getBytes(charsetName);
	}

	public static short getShort(byte[] bytes) {
		return (short) ((0xff & bytes[1]) | (0xff00 & (bytes[0] << 8)));
	}

	public static char getChar(byte[] bytes) {
		return (char) ((0xff & bytes[1]) | (0xff00 & (bytes[0] << 8)));
	}

	public static int getInt(byte[] bytes) {
		return (0xff & bytes[3]) | (0xff00 & (bytes[2] << 8))
				| (0xff0000 & (bytes[1] << 16))
				| (0xff000000 & (bytes[0] << 24));
	}

	public static long getLong(byte[] bytes) {
		return (0xffL & (long) bytes[7]) | (0xff00L & ((long) bytes[6] << 8))
				| (0xff0000L & ((long) bytes[5] << 16))
				| (0xff000000L & ((long) bytes[4] << 24))
				| (0xff00000000L & ((long) bytes[3] << 32))
				| (0xff0000000000L & ((long) bytes[2] << 40))
				| (0xff000000000000L & ((long) bytes[1] << 48))
				| (0xff00000000000000L & ((long) bytes[0] << 56));
	}

	public static float getFloat(byte[] bytes) {
		return Float.intBitsToFloat(getInt(bytes));
	}

	public static double getDouble(byte[] bytes) {
		long l = getLong(bytes);
		System.out.println(l);
		return Double.longBitsToDouble(l);
	}

	public static String getString(byte[] bytes, XCharsetEnum charset)
			throws Exception {
		return getString(bytes, charset.name());
	}

	public static String getString(byte[] bytes, String charsetName)
			throws Exception {
		return new String(bytes, charsetName);
	}

	public static byte[] copyByteArray(byte[] src, int srcPos,int length) throws Exception {
		//复制某一段数组
		byte[] clonebytes = new byte[length];
		System.arraycopy(src, srcPos, clonebytes, 0,length);
		return clonebytes;
	}

	public static byte[] copyByteArray(byte[] src, int srcPos, int dstPos,
			int maxLength) throws Exception {
		//初始化一定长度的数组，并复制截取
		byte[] clonebytes = new byte[maxLength];
		int length = src.length+srcPos;
		if (length > maxLength-dstPos)
			length = maxLength-dstPos;
		System.arraycopy(src, srcPos, clonebytes, dstPos, length);
		return clonebytes;
	}

	public static byte[] copyByteArray(byte[] src, int srcPos, int dstPos,
			int srcLength,int maxLength) throws Exception {
		byte[] clonebytes = new byte[maxLength];
		int length = srcLength+srcPos;
		if (length > maxLength-dstPos)
			length = maxLength-dstPos;
		System.arraycopy(src, srcPos, clonebytes, dstPos, length);
		return clonebytes;
	}

	public static <T> void copyArray(T src, int srcPos, T dst, int dstPos,
			int length) throws Exception {
		System.arraycopy(src, srcPos, dst, dstPos, length);
	}

	public static byte[] joinByteArray(byte[] src0, byte[] src1)
			throws Exception {
		byte[] newbytes = new byte[src0.length + src1.length];
		System.arraycopy(src0, 0, newbytes, 0, src0.length);
		System.arraycopy(src1, 0, newbytes, src0.length, src1.length);
		return newbytes;
	}

	public static byte[] joinByteArray(byte[] src0, byte src1) throws Exception {
		byte[] newbytes = new byte[src0.length + 1];
		System.arraycopy(src0, 0, newbytes, 0, src0.length);
		newbytes[src0.length] = src1;
		return newbytes;
	}

	public static byte[] joinByteArray(byte src0, byte[] src1) throws Exception {
		byte[] newbytes = new byte[1 + src1.length];
		newbytes[0] = src0;
		System.arraycopy(src1, 0, newbytes, 1, src1.length);
		return newbytes;
	}
}
