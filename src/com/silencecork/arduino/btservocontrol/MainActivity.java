
package com.silencecork.arduino.btservocontrol;

import java.util.List;

import com.android.utility.bluetooth.BluetoothListAdapter;
import com.android.utility.bluetooth.LocalBluetoothManager;
import com.android.utility.bluetooth.OnBluetoothDiscoverEventListener;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity implements OnItemClickListener {
    
    private ListView mListView;
    private BluetoothListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mListView = (ListView) findViewById(R.id.device_list);
        mListView.setOnItemClickListener(this);
        
        LocalBluetoothManager.getInstance().startSession(this);
        
        if (!LocalBluetoothManager.getInstance().isBluetoothTurnOn()) {
            LocalBluetoothManager.getInstance().turnOnBluetooth(this);
        } else {
            initList();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!LocalBluetoothManager.getInstance().isBluetoothTurnOn()) {
            finish();
        } else {
            initList();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBluetoothManager.getInstance().endSession();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_discovery) {
            if (mAdapter != null) {
                mAdapter.clearAll();
            }
            LocalBluetoothManager.getInstance().discoverDevice(mDiscoverListener);
            return true;
        }
        return true;
    }
    
    private void initList() {
        List<BluetoothDevice> list = LocalBluetoothManager.getInstance().getPairedDevices();
        mAdapter = new BluetoothListAdapter(this);
        mAdapter.setDeviceList(list);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        BluetoothDevice device = (BluetoothDevice) mAdapter.getItem(position);
        if (!LocalBluetoothManager.getInstance().isPairedDevice(device)) {
            LocalBluetoothManager.getInstance().pairDevice(device);
        } else {
            Intent intent = new Intent(this, Control.class);
            intent.putExtra("device", device);
            startActivity(intent);
        }
    }
    
    private OnBluetoothDiscoverEventListener mDiscoverListener = new OnBluetoothDiscoverEventListener() {
        
        @Override
        public void discoveredDevice(BluetoothDevice device, int rssi) {
            if (mAdapter != null) {
                mAdapter.addItem(device);
            }
        }
        
        @Override
        public void discoverFinish() {
            if (mAdapter != null) {
                List<BluetoothDevice> list = LocalBluetoothManager.getInstance().getPairedDevices();
                for (BluetoothDevice device : list) {
                    mAdapter.addItem(device);
                }
            }
        }
    };
}
