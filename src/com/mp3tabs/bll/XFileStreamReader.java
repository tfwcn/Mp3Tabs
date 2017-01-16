package com.mp3tabs.bll;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import com.mp3tabs.model.XCharsetEnum;

public class XFileStreamReader {
	private File xfile;
	private RandomAccessFile xinstream;

	public File getXfile() {
		return xfile;
	}

	public RandomAccessFile getXinstream() {
		return xinstream;
	}

	public long length() throws Exception {
		return xinstream.length();
	}

	public XFileStreamReader(String path) throws Exception {
		xfile = new File(path);
		xinstream = new RandomAccessFile(xfile,"r");
	}

	public void xSeek(long offset) throws Exception {
		if (xinstream == null)
			throw new Exception("No File!");
		xinstream.seek(offset);
	}

	public void xClose() throws Exception {
		if (xinstream == null)
			throw new Exception("No File!");
		xinstream.close();
		xinstream=null;
		xfile=null;
		System.gc();
	}

	public short xReadShort() throws Exception {
		byte[] data = new byte[2];
		int readlen = xinstream.read(data);
		if (readlen == -1)
			throw new Exception("File End!");
		return XByteArrayHelper.getShort(data);
	}

	public char xReadChar() throws Exception {
		byte[] data = new byte[2];
		int readlen = xinstream.read(data);
		if (readlen == -1)
			throw new Exception("File End!");
		return XByteArrayHelper.getChar(data);
	}

	public int xReadInt() throws Exception {
		byte[] data = new byte[4];
		int readlen = xinstream.read(data);
		if (readlen == -1)
			throw new Exception("File End!");
		return XByteArrayHelper.getInt(data);
	}

	public long xReadLong() throws Exception {
		byte[] data = new byte[8];
		int readlen = xinstream.read(data);
		if (readlen == -1)
			throw new Exception("File End!");
		return XByteArrayHelper.getLong(data);
	}

	public float xReadFloat() throws Exception {
		byte[] data = new byte[4];
		int readlen = xinstream.read(data);
		if (readlen == -1)
			throw new Exception("File End!");
		return XByteArrayHelper.getFloat(data);
	}

	public double xReadDouble() throws Exception {
		byte[] data = new byte[8];
		int readlen = xinstream.read(data);
		if (readlen == -1)
			throw new Exception("File End!");
		return XByteArrayHelper.getDouble(data);
	}

	public String xReadString(int length, XCharsetEnum charset)
			throws Exception {
		return xReadString(length, charset.name());
	}

	public String xReadString(int length, String charsetName) throws Exception {
		byte[] data = new byte[length];
		int readlen = xinstream.read(data);
		if (readlen == -1)
			throw new Exception("File End!");
		return XByteArrayHelper.getString(data, charsetName);
	}

	public byte[] xReadBytes(int length) throws Exception {
		byte[] data = new byte[length];
		int readlen = xinstream.read(data);
		if (readlen == -1)
			throw new Exception("File End!");
		return data;
	}

	public byte xReadByte() throws Exception {
		byte data = 0;
		int readlen = xinstream.read();
		if (readlen == -1)
			throw new Exception("File End!");
		data = (byte) readlen;
		return data;
	}
}
