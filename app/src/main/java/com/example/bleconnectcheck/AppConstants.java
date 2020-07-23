package com.example.bleconnectcheck;

import java.io.File;

public final class AppConstants {
    public static final String APP_SIGNATURE = "나왔다";
//    public static final File SETTING_JSON = new File( MyApplication.getAppContext().getExternalFilesDir(null), "setting.json");

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
}
