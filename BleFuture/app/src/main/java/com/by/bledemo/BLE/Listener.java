package com.by.bledemo.BLE;

/**
 * Created by 林北94狂 on 2018/1/10.
 */

public interface Listener {
    void Disconnect();
    void Configured();
    void ConfigError(int status);
    void onError(String err);
}
