package com.eddy.common.network;

public interface IHttpCallback
{
    public void callback(HttpData httpData);
    public void exception(int code, String message);
}
