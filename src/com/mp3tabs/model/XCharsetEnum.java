package com.mp3tabs.model;

public enum XCharsetEnum {
	UTF_8, GBK, ASCII, ISO_8859_1, UTF_32, UNICODE, ANSI, UTF_16, UTF_16BE;
	@Override
	public String toString() {
		String charsetName = "";
		switch (this) {
		case UTF_8:
			charsetName = "UTF-8";
			break;
		case GBK:
			charsetName = "GBK";
			break;
		case ASCII:
			charsetName = "US-ASCII";
			break;
		case ISO_8859_1:
			charsetName = "ISO-8859-1";
			break;
		case UTF_32:
			charsetName = "UTF-32";
			break;
		case UNICODE:
			charsetName = "UNICODE";
			break;
		case ANSI:
			charsetName = "ANSI";
			break;
		case UTF_16:
			charsetName = "UTF-16";
			break;
		case UTF_16BE:
			charsetName = "UTF-16BE";
			break;
		}
		return charsetName;
	}

	public static XCharsetEnum toEnum(String name) {
		XCharsetEnum charset = XCharsetEnum.UTF_8;
		if (name.equals("UTF-8"))
			charset = XCharsetEnum.UTF_8;
		else if (name.equals("GBK"))
			charset = XCharsetEnum.GBK;
		else if (name.equals("US-ASCII"))
			charset = XCharsetEnum.ASCII;
		else if (name.equals("ISO-8859-1"))
			charset = XCharsetEnum.ISO_8859_1;
		else if (name.equals("UTF-32"))
			charset = XCharsetEnum.UTF_32;
		else if (name.equals("UNICODE"))
			charset = XCharsetEnum.UNICODE;
		else if (name.equals("ANSI"))
			charset = XCharsetEnum.ANSI;
		else if (name.equals("UTF-16"))
			charset = XCharsetEnum.UTF_16;
		else if (name.equals("UTF-16BE"))
			charset = XCharsetEnum.UTF_16BE;
		return charset;
	}
}
