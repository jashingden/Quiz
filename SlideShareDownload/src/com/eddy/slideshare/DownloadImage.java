package com.eddy.slideshare;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.eddy.common.Logger;
import com.eddy.common.network.HttpData;
import com.eddy.common.network.IHttpCallback;
import com.eddy.common.network.MyHttpGet;
import com.eddy.common.network.MyHttpParams;
import com.eddy.common.network.ThreadPoolManager;

public class DownloadImage implements IHttpCallback {
	
	private String url;
	private String filename;
	private byte[] data;
	
	public void execute(String url, String filename) {
		this.url = url;
		this.filename = filename;
		
		MyHttpParams httpParams = new MyHttpParams();
		httpParams.url = url;
		httpParams.C2SDataType = MyHttpParams.C_S_DATA_TYPE_STRING;
        httpParams.S2CDataType = MyHttpParams.S_C_DATA_TYPE_BYTES;
		httpParams.callback = this;
		
        ThreadPoolManager.getInstance().execute(new MyHttpGet(httpParams));
	}
	
	@Override
	public void callback(HttpData httpData) {
		data = httpData.b;
		try {
			Path path = Paths.get(filename);
			Files.write(path, data);
			Logger.log(url + " -> " + filename);
		} catch (Exception ex) {
			Logger.log(ex);
		}
	}

	@Override
	public void exception(int code, String message) {
		Logger.logException(code, message);
	}

}
