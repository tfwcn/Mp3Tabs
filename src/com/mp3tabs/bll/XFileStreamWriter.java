package com.mp3tabs.bll;

import java.io.File;
import java.io.RandomAccessFile;

import com.mp3tabs.model.XCharsetEnum;

public class XFileStreamWriter {
	private File xfile;
	private RandomAccessFile xoutstream;

	public File getXfile() {
		return xfile;
	}

	public RandomAccessFile getXoutstream() {
		return xoutstream;
	}

	public XFileStreamWriter(String path) throws Exception {
		xfile = new File(path);
		xoutstream = new RandomAccessFile(xfile,"rw");
	}

	public void xSeek(long offset) throws Exception {
		if (xoutstream == null)
			throw new Exception("No File!");
		xoutstream.seek(offset);
	}

	public void xClose() throws Exception {
		if (xoutstream == null)
			throw new Exception("No File!");
		xoutstream.close();
		xoutstream=null;
		xfile=null;
		System.gc();
	}

	public void xWriteShort(short value) throws Exception {
		byte[] data = XByteArrayHelper.getBytes(value);
		xoutstream.write(data);
	}

	public void xWriteChar(char value) throws Exception {
		byte[] data = XByteArrayHelper.getBytes(value);
		xoutstream.write(data);
	}

	public void xWriteInt(int value) throws Exception {
		byte[] data = XByteArrayHelper.getBytes(value);
		xoutstream.write(data);
	}

	public void xWriteLong(long value) throws Exception {
		byte[] data = XByteArrayHelper.getBytes(value);
		xoutstream.write(data);
	}

	public void xWriteFloat(float value) throws Exception {
		byte[] data = XByteArrayHelper.getBytes(value);
		xoutstream.write(data);
	}

	public void xWriteDouble(double value) throws Exception {
		byte[] data = XByteArrayHelper.getBytes(value);
		xoutstream.write(data);
	}

	public void xWriteString(String value, XCharsetEnum charset)
			throws Exception {
		xWriteString(value, charset.name());
	}

	public void xWriteString(String value, String charsetName) throws Exception {
		byte[] data = XByteArrayHelper.getBytes(value, charsetName);
		xoutstream.write(data);
	}

	public void xWriteBytes(byte[] value) throws Exception {
		xoutstream.write(value);
	}

	public void xWriteByte(byte value) throws Exception {
		byte[] data = new byte[] { value };
		xoutstream.write(data);
	}
}
