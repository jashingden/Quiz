package com.eddy.slideshare;

import com.eddy.common.Logger;

public class Downloader {

	private final static String CONFIG_NAME = "SlideShareDownload.conf";
	
	public static void main(String[] args) {
		Config config = Config.getInstance();
		/**
		 * 先讀取設定檔
		 */
		config.load(CONFIG_NAME);
		/**
		 * 再檢查啟動參數, 若設定參數相同則以啟動參數為主
		 */
		config.checkArguments(args);
		
		String url = config.target_url();
		if (url != null && url.length() > 0) {
			Logger.log("Starting download...");
			try {
				DownloadFile df = new DownloadFile();
				df.execute(url, config.target_tag(), config.target_img());
				df.waitForFinish();
			} catch (Exception ex) {
				Logger.log(ex);
				System.exit(0);
			}
		}
		
		Logger.log("Finish.");
	}
	
}
