package com.bluetooth.tiaopi;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bluetooth.tiaopi.adapter.MessageAdapter;
import com.bluetooth.tiaopi.util.ReadThread;

import java.io.IOException;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClientBtActivity extends AppCompatActivity {

    @BindView(R.id.message_RecyclerView)
    RecyclerView messageRecyclerView;
    @BindView(R.id.edit_text)
    EditText editText;
    @BindView(R.id.send_button)
    Button sendButton;

    MessageAdapter messageAdapter;

    ReadThread readThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("客户端");
        setContentView(R.layout.activity_clinet_bt);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        messageAdapter  = new MessageAdapter(this);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageRecyclerView.setAdapter(messageAdapter);

        readThread = new ReadThread(App.getInstance().bluetoothSocket,mHandler);
        readThread.start();

        if (App.getInstance().bluetoothSocket.isConnected()) {
            Message msg = new Message();
            msg.obj = "客户端已经连接上！可以发送信息。";
            msg.what = 1;
            mHandler.sendMessage(msg);
        }

    }

    @OnClick(R.id.send_button)
    public void onClick() {
        String msg = editText.getText().toString();
        if (!TextUtils.isEmpty(msg)) {
            sendMessageHandle(msg);
        }else {
            Toast.makeText(this, "别弄空", Toast.LENGTH_SHORT).show();
        }
    }

    // 发送数据
    private void sendMessageHandle(String msg) {
        if (App.getInstance().bluetoothSocket == null) {
            Toast.makeText(this, "没有连接", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            OutputStream os = App.getInstance().bluetoothSocket.getOutputStream();
            os.write(msg.getBytes());
            messageAdapter.add(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String info = (String) msg.obj;
            if (msg.what == 1) {
                messageAdapter.add(info);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        readThread = null;
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
