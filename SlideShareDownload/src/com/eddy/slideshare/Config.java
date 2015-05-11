package com.eddy.slideshare;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class Config {

	private static Config instance;
	
	private String mTargetUrl;
	private String mTargetTag = "data-full";
	private String mTargetImg = "%04d.jpg";
	
	public static synchronized Config getInstance() {
		if (instance == null) {
			instance = new Config();
		}
		return instance;
	}
	
	public void load(String filename) {
		String value;
		try {
			Properties p = new Properties();
			p.load(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
			
			value = p.getProperty("TargetUrl");
			if (value != null) {
				mTargetUrl = value;
			}
			value = p.getProperty("TargetTag");
			if (value != null) {
				mTargetTag = value;
			}
			value = p.getProperty("TargetImg");
			if (value != null) {
				mTargetImg = value;
			}
		} catch (Exception ex) {
		}
	}
	
	public void checkArguments(String[] args) {
		if (args != null && args.length > 0) {
			for (String s : args) {
				if (s.startsWith("-url") || s.startsWith("--targeturl")) {
					try {
						mTargetUrl = s.substring(s.indexOf(":")+1);
					} catch (Exception e) {
					}
				} else if (s.startsWith("-tag") || s.startsWith("--targettag")) {
					try {
						mTargetTag = s.substring(s.indexOf(":")+1);
					} catch (Exception e) {
					}
				} else if (s.startsWith("-img") || s.startsWith("--targetimg")) {
					try {
						mTargetImg = s.substring(s.indexOf(":")+1);
					} catch (Exception e) {
					}
				}
			}
		}
	}
	
	public String target_url() {
		return mTargetUrl;
	}
	
	public String target_tag() {
		return mTargetTag;
	}
	
	public String target_img() {
		return mTargetImg;
	}
}
