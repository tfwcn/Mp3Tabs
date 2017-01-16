package com.mp3tabs.bll;

import com.mp3tabs.model.XFileInfo;

public class XThread extends Thread {
	public XRun xRun;

	public XThread(XRun xRun) {
		this.xRun = xRun;
	}

	public static abstract class XRun {
		public XFileInfo file;

		public XRun(XFileInfo file) {
			this.file = file;
		}

		public abstract void run();
	};

	@Override
	public void run() {
		xRun.run();
	}
}
