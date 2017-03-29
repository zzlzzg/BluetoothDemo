package com.bluetooth.tiaopi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bluetooth.tiaopi.adapter.MessageAdapter;
import com.bluetooth.tiaopi.util.ReadThread;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 服务端页面  主要是发送
 */
public class ServiceBtActivity extends AppCompatActivity {

    BluetoothAdapter btAdapter;
    @BindView(R.id.edit_text)
    EditText editText;
    @BindView(R.id.send_button)
    Button sendButton;
    @BindView(R.id.find_button)
    Button findButton;
    @BindView(R.id.start_connet_button)
    Button startConnetButton;
    @BindView(R.id.message_RecyclerView)
    RecyclerView messageRecyclerView;

    BluetoothSocket socket = null;

    BluetoothServerSocket bluetoothServerSocket;

    MessageAdapter messageAdapter;

    ReadThread readThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("服务端");
        setContentView(R.layout.activity_service_bt);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        messageAdapter = new MessageAdapter(this);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageRecyclerView.setAdapter(messageAdapter);
    }

    public void startAccept() {
        BluetoothServerSocket tmp = null;
        try {
//            Method listenMethod = btAdapter.getClass().getMethod("listenUsingRfcommOn",
//                    new Class[]{int.class});
//            tmp = (BluetoothServerSocket) listenMethod.invoke(btAdapter, 1);

            tmp = btAdapter.listenUsingRfcommWithServiceRecord(btAdapter.getName(),
                    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

            thread.start();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bluetoothServerSocket = tmp;
    }

    Thread thread = new Thread() {
        @Override
        public void run() {
            super.run();
            try {
                while (true) {
                    socket = bluetoothServerSocket.accept();
                    if (socket != null) {

                        Log.d("配对", "==============完成");
                        Message message = new Message();
                        message.what = 4000;
                        handler.sendMessage(message);
                        bluetoothServerSocket.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 4000) {
                messageAdapter.add("连接成功");

                readThread = new ReadThread(socket,handler);
                readThread.start();
            }

            String info = (String) msg.obj;
            if (msg.what == 1) {
                messageAdapter.add(info);
            }

        }
    };

    @OnClick({R.id.find_button, R.id.send_button, R.id.start_connet_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.find_button:
                initBT();
                break;
            case R.id.start_connet_button:
                startAccept();
                break;
            case R.id.send_button: //发送消息
                String msg = editText.getText().toString();
                if (!TextUtils.isEmpty(msg)) {
                    sendMessageHandle(msg);
                }else {
                    Toast.makeText(this, "别弄空", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    // 发送数据
    private void sendMessageHandle(String msg) {
        if (socket == null) {
            Toast.makeText(this, "没有连接", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            OutputStream os = socket.getOutputStream();
            os.write(msg.getBytes());
            messageAdapter.add(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void initBT() {
        Intent btIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        //设置蓝牙的可见时间
        btIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
        startActivity(btIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
