package com.eddy.slideshare;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.eddy.common.Logger;
import com.eddy.common.network.HttpData;
import com.eddy.common.network.IHttpCallback;
import com.eddy.common.network.MyHttpGet;
import com.eddy.common.network.MyHttpParams;
import com.eddy.common.network.ThreadPoolManager;

public class DownloadFile implements Runnable, IHttpCallback {
	
	private String tag;
	private String img;
	private String content;
	private Thread thread;
	private boolean done;
	
	public void execute(String url, String tag, String img) {
		this.tag = tag;
		this.img = img;
		
		MyHttpParams httpParams = new MyHttpParams();
		httpParams.url = url;
		httpParams.C2SDataType = MyHttpParams.C_S_DATA_TYPE_STRING;
		httpParams.S2CDataType = MyHttpParams.S_C_DATA_TYPE_STRING;
		httpParams.callback = this;
		
		ThreadPoolManager tpm = ThreadPoolManager.getInstance();
		tpm.start();
        tpm.execute(new MyHttpGet(httpParams));
        
        thread = new Thread(this);
        thread.start();
	}
	
	public void waitForFinish() {
		try {
			thread.join();
		} catch (InterruptedException ie) {}
		ThreadPoolManager.getInstance().shutdown();
	}
	
	@Override
	public void run() {
		while (!done) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException ie) {}
		}

		if (checkFolder(img)) {
			ArrayList<String> list = getImageUrl();
			int count = list.size();
			for (int i = 0; i < count; i++) {
				DownloadImage di = new DownloadImage();
				di.execute(list.get(i), getFilename(i+1));
			}
		}
	}
	
	private boolean checkFolder(String filename) {
		String folder = "";
		int idx = -1;
		if ((idx = filename.lastIndexOf("/")) >= 0) {
			folder = filename.substring(0, idx);
		} else if ((idx = filename.lastIndexOf("\\")) >= 0) {
			folder = filename.substring(0, idx);
		}
		try {
			if (folder.length() > 0) {
				Files.createDirectories(Paths.get(folder));
			}
		} catch (IOException ex) {
			Logger.log(ex);
			return false;
		}
		return true;
	}
	
	private String getFilename(int idx) {
		return String.format(img, idx);
	}
	
	@Override
	public void callback(HttpData httpData) {
		content = httpData.data;
		done = true;
	}

	@Override
	public void exception(int code, String message) {
		Logger.logException(code, message);
		done = true;
	}
	
	private ArrayList<String> getImageUrl() {
		ArrayList<String> list = new ArrayList<String>();
		if (content == null) {
			return list;
		}
		int fromIndex = 0;
		int idx = -1;
		final int fileIndex = 1;
		while ((idx = content.indexOf(tag, fromIndex)) >= 0) {
			idx += tag.length();
			if (content.charAt(idx) == '=') {
				idx ++;
				int end = -1;
				if (content.charAt(idx) == '\"') {
					idx ++;
					end = content.indexOf('\"', idx);
				} else if (content.charAt(idx) == '\'') {
					idx ++;
					end = content.indexOf('\'', idx);
				}
				if (end >= 0) {
					String url = content.substring(idx, end);
					list.add(url);
					idx = end + 1;
				} else {
					idx ++;
				}
				fromIndex = idx;
			}
		}
		return list;
	}

}
