package com.eddy.common;

public class Logger {

	public static void log(String text) {
		System.out.println(text);
	}
	
	public static void log(Exception ex) {
		ex.printStackTrace();
	}
	
	public static void logException(int code, String message) {
		System.out.println("Exception ["+code+"]message="+message);
	}
}
