package com.bluetooth.tiaopi;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bluetooth.tiaopi.adapter.SearchBTAddsAdapter;
import com.bluetooth.tiaopi.model.BTAddsModel;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 搜索设备
 */
public class SearchBtActivity extends AppCompatActivity implements SearchBTAddsAdapter.TextClick {

    private final int PERMISSION_REQUEST_CONSTANT = 1002;

    @BindView(R.id.search_bt_button)
    Button searchBtButton;
    @BindView(R.id.bt_adds_recycler_view)
    RecyclerView btAddsRecyclerView;
    @BindView(R.id.search_bt_progress)
    ProgressBar searchBtProgress;
    @BindView(R.id.cancel_bt_button)
    Button cancelBtButton;
    @BindView(R.id.cancel_connect_bt_button)
    Button cancelConnectBtButton;

    BluetoothAdapter btAdapter;

    SearchBTAddsAdapter btAddsAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("查找设备");
        setContentView(R.layout.activity_search_bt);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        //注册广播  获取蓝牙地址
        initBroadcastReceiver();

        btAddsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        btAddsAdapter = new SearchBTAddsAdapter(this);
        btAddsRecyclerView.setAdapter(btAddsAdapter);
        btAddsAdapter.setTextClick(this);

    }

    @Override
    public void onClickCall(int position) {
        //点击进行配对
        String macAdds = btAddsAdapter.getData().get(position).getMacAdds();

        BluetoothDevice remoteDevice = btAdapter.getRemoteDevice(macAdds);

        Method method;

        try {
//            method = remoteDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
//              = (BluetoothSocket) method.invoke(remoteDevice, 1);

            App.getInstance().bluetoothSocket =
                    remoteDevice.createRfcommSocketToServiceRecord(
                            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));


            Toast.makeText(this, "配对开始,稍后...", Toast.LENGTH_SHORT).show();
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

    }

    Thread thread = new Thread() {
        @Override
        public void run() {
            super.run();
            btAdapter.cancelDiscovery();
            try {
                App.getInstance().bluetoothSocket.connect();
            } catch (IOException e) {
                try {
                    App.getInstance().bluetoothSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    };

    /**
     * 初始化广播   接收搜索的地址
     */
    public void initBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, filter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //设备的名字可能是空的，但地址一定是有的
                BTAddsModel btAddsModel = new BTAddsModel();
                btAddsModel.setName(device.getName());
                btAddsModel.setMacAdds(device.getAddress());
                btAddsAdapter.addBT(btAddsModel);

                Log.d("蓝牙", device.getName() + "--" + device.getAddress());

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //搜索完成
                setTitle("搜索完成");
                searchBtProgress.setVisibility(View.GONE);
                Log.d("蓝牙", "搜索完成");
            }
        }
    };

    @OnClick({R.id.search_bt_button, R.id.cancel_bt_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_bt_button:  //搜索蓝牙
                searchBtProgress.setVisibility(View.VISIBLE);
                btAddsAdapter.clearBT();
                if (Build.VERSION.SDK_INT >= 6.0) {
                    //获取权限
                    ActivityCompat.requestPermissions(this, new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_REQUEST_CONSTANT);
                } else {
                    btAdapter.startDiscovery();
                    setTitle("搜索中...");
                }
                break;
            case R.id.cancel_bt_button: //取消搜索
                btAdapter.cancelDiscovery();
                break;
            case R.id.cancel_connect_bt_button: //取消连接
                try {
                    App.getInstance().bluetoothSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //6.0以后需要权限
        if (requestCode == PERMISSION_REQUEST_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                btAdapter.startDiscovery();
                setTitle("搜索中...");
            } else {
                searchBtProgress.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

}
