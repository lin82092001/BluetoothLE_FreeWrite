package com.serverpit.wolf.BLE;

import java.util.UUID;

/**
 * Created by Wolf on 2016/2/28.
 */
public interface  Listener {
    void Disconnected();
    void Configured();
    void ConfigError(int status);
    void onError(String err);
}
