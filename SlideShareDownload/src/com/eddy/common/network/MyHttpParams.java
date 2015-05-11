package com.eddy.common.network;

import java.util.Hashtable;

/**
 * Http協定參數物件
 */
public class MyHttpParams
{
    public static final int QUEUE_SEND = 0;
    public static final int QUEUE_WAIT = 1;
	/**
	 * Client到Server的參數型態為字串
	 */
	public static final int C_S_DATA_TYPE_STRING = 0;
	/**
	 * Client到Server的參數型態為位元組
	 */
	public static final int C_S_DATA_TYPE_BYTES = 1;
	/**
	 * Server回傳Client的結果型態為字串
	 */
	public static final int S_C_DATA_TYPE_STRING = 2;
	/**
	 * Server回傳Client的結果型態為位元組
	 */
	public static final int S_C_DATA_TYPE_BYTES = 3;
	/**
	 * Http Get是放整個完整的url,包含要傳遞的參數
	 * Http Post只放url
	 */
	public String url;
	public IHttpCallback callback;
	public int C2SDataType;
	public int S2CDataType;
	/**
	 * 只用在Http Post傳遞變數用
	 */
	public Hashtable<String, String> kv;
	/**
	 * 只用在Http Post傳遞變數用
	 */
	public byte[] b;
    /**
     * User-Agent
     */
	public String userAgent;
    /**
     * Proxy
     */
	public String proxyIP;
	
	public int proxyPort;
    /**
     * 設定Headers
     */
    public String[][] headers;
}
