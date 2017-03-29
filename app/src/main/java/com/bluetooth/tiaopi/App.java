package com.bluetooth.tiaopi;

import android.app.Application;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

/**
 * Created by TiaoPi on 2017/3/29.
 */

public class App extends Application{

    private static App app;
    BluetoothSocket bluetoothSocket;

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;
    }

    public  void setBluetoothSocke (BluetoothSocket bluetoothSocket){
        this.bluetoothSocket = bluetoothSocket;
    }

    public static App getInstance(){
        return app;
    }

}
