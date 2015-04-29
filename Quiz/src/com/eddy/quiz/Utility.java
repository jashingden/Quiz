package com.eddy.quiz;

import java.io.UnsupportedEncodingException;

import android.util.Log;

public class Utility {
	
	public final static String TAG = "EddyTeng";
	
	public static String readString(byte[] b)
	{
		return readString(b, 0, b.length);
	}

	public static String readString(byte[] b, int position, int len)
	{
		try
		{
			return new String(b, position, len, "UTF-8").trim();
		}
		catch(UnsupportedEncodingException ex)
		{
			ex.printStackTrace();
			// return null;
			return "";
		}
		catch(RuntimeException ex)
		{
			ex.printStackTrace();
			Log.d(TAG, "no utf8 char==" + new String(b, position, len).trim());
			// return null;
			return "";
		}
	}

}
