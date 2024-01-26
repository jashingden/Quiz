package com.eddy.quiz;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class Utility {
	
	public final static String TAG = "EddyTeng";
	
	public static String[] readStringArrayFromAssetFile(Context context, String filename) {
		AssetManager asset = context.getAssets();
	    String txt = "";
		try {
			InputStream in = asset.open(filename);
	        byte[] b = new byte[in.available()];
	        in.read(b);
	        txt = Utility.readString(b);
		} catch (Exception e) {
			return new String[0];
		}
	    return txt.split("\r\n");
	}
	
	public static String readString(byte[] b) {
		return readString(b, 0, b.length);
	}

	public static String readString(byte[] b, int position, int len) {
		try {
			return new String(b, position, len, "UTF-8").trim();
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
			return "";
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			Log.d(TAG, "no utf8 char==" + new String(b, position, len).trim());
			return "";
		}
	}

}
