package com.bluetooth.tiaopi;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_ENABLE = 1001;

    @BindView(R.id.client_bt_button)
    Button clientBtButton;
    @BindView(R.id.service_bt_button)
    Button serviceBtButton;
    @BindView(R.id.open_bt_button)
    Button openBtButton;
    @BindView(R.id.check_bt_text_view)
    TextView checkBtTextView;

    BluetoothAdapter btAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("蓝牙DEMO");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBt(btAdapter);

    }

    @OnClick({R.id.open_bt_button,R.id.search_bt_button,
            R.id.client_bt_button, R.id.service_bt_button})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.open_bt_button:
                openBT();
                break;
            case R.id.search_bt_button: //打开搜索配对
                intent = new Intent(MainActivity.this,SearchBtActivity.class);
                startActivity(intent);
                break;
            case R.id.client_bt_button: //打开客户端
                intent = new Intent(MainActivity.this,ClientBtActivity.class);
                startActivity(intent);
                break;
            case R.id.service_bt_button: //打开服务端
                intent = new Intent(MainActivity.this,ServiceBtActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 检查蓝牙是不是可用
     *
     * @param btAdapter
     * @return
     */
    private void checkBt(BluetoothAdapter btAdapter) {
        if (btAdapter == null) {
            checkBtTextView.setText("蓝牙设备不可用");
        } else {
            checkBtTextView.setText("蓝牙设备可用");
        }
    }

    /**
     * 查看是否打开蓝牙 并提示打开
     */
    private void openBT() {
        if (!btAdapter.isEnabled()) {
            Intent openBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(openBT, REQUEST_ENABLE);
        } else {
            toastString("已经打开");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_ENABLE) {
            //是否打开蓝牙
            if (btAdapter.isEnabled()) {
                toastString("已经打开");
            } else {
                toastString("未打开");
            }
        }
    }

    private void toastString(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

}
