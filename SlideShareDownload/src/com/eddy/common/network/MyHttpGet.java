package com.eddy.common.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class MyHttpGet implements Runnable {
	
	private MyHttpParams params;
	
	public MyHttpGet(MyHttpParams params) {
		this.params = params;
	}
	
	@Override
	public void run() {
		doGet();
	}
	
	public void doGet() {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(params.url);
        if (null != params.headers) {
            for (int i = 0 ; i < params.headers.length ; i++) {
                httpget.addHeader(params.headers[i][0], params.headers[i][1]);
            }
        }
		if (null != params.userAgent) {
			client.getParams().setParameter("http.useragent", params.userAgent);
		}
		if (null != params.proxyIP) {
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(params.proxyIP, params.proxyPort));
		}
		
		try {
			HttpData httpData = new HttpData();
			CloseableHttpResponse httpResponse = client.execute(httpget);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				httpData.code = HttpData.OK;
				/**
				 * 判斷回傳資料是否有壓縮
				 */
				Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding");

				if (null != contentEncoding && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					ByteArrayInputStream bais = new ByteArrayInputStream(EntityUtils.toByteArray(httpResponse.getEntity()));
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					GZIPInputStream gzip = new GZIPInputStream(bais, 256);
				    
				    byte[] data = new byte[256];
				    int c;
				    while((c = gzip.read(data)) != -1) {
				    	baos.write(data, 0, c);
				    }
				    
				    gzip.close();
				    bais.close();
					
					if (MyHttpParams.S_C_DATA_TYPE_STRING == params.S2CDataType) {
						httpData.data = new String(baos.toByteArray(), "utf-8");
					} else if(MyHttpParams.S_C_DATA_TYPE_BYTES == params.S2CDataType) {
						httpData.b = baos.toByteArray();
					}
				} else {
					if (MyHttpParams.S_C_DATA_TYPE_STRING == params.S2CDataType) {
						httpData.data = EntityUtils.toString(httpResponse.getEntity());
					}
					else if (MyHttpParams.S_C_DATA_TYPE_BYTES == params.S2CDataType) {
						httpData.b = EntityUtils.toByteArray(httpResponse.getEntity());
					}
				}

				params.callback.callback(httpData);
			} else {
				params.callback.exception(httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().toString());
			}
		} catch (Exception ex) {
			params.callback.exception(-999, ex.getMessage());
		}
	}
}