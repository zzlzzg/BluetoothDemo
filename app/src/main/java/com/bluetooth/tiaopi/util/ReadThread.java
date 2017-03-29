package com.bluetooth.tiaopi.util;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by TiaoPi on 2017/3/29.
 */

public class ReadThread extends Thread {

    private BluetoothSocket socket;
    private Handler handler;

    public ReadThread(BluetoothSocket socket, Handler handler){
        this.socket = socket;
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();
        byte[] buffer = new byte[1024];
        int bytes;
        InputStream is = null;
        try {
            is = socket.getInputStream();
            while (true) {
                if ((bytes = is.read(buffer)) > 0) {
                    byte[] buf_data = new byte[bytes];
                    for (int i = 0; i < bytes; i++) {
                        buf_data[i] = buffer[i];
                    }
                    String s = new String(buf_data);
                    Message msg = new Message();
                    msg.obj = s;
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }
}
