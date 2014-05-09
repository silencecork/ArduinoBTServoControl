package com.silencecork.arduino.btservocontrol;

import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.android.utility.bluetooth.BluetoothConnectionHelper;
import com.android.utility.bluetooth.OnBluetoothMessageListener;
import com.silencecork.arduino.btservocontrol.CircularSeekBar.OnCircularSeekBarChangeListener;

public class Control extends Activity {
	
    private static final String APP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	private TextView mDegreeText;
	private BluetoothConnectionHelper mHelper;
	private CircularSeekBar mSeekBar;
	private String mReceiveMessage;
	
	private Handler mHandler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	        int progress = msg.arg1;
	        mHelper.sendMessage(String.valueOf(progress));
	    }
	};
	
    private OnBluetoothMessageListener mListener = new OnBluetoothMessageListener() {
        
        @Override
        public void onMessageReceived(BluetoothDevice device, String message) {
            mReceiveMessage += message;
            if (message.contains("\n")) {
                Log.i("Degree", "degree " + mReceiveMessage);
                mReceiveMessage = "";
            }
        }
        
        @Override
        public void onDisconnect(BluetoothDevice device) {
            if (mSeekBar != null) {
                mSeekBar.setEnabled(false);
                mSeekBar.setProgress(0);
            }
        }
        
        @Override
        public void onConnected(BluetoothDevice device) {
            if (mSeekBar != null) {
                mSeekBar.setEnabled(true);
                mSeekBar.setProgress(0);
            }
        }
    };
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_control);
		
		BluetoothDevice device = getIntent().getParcelableExtra("device");
        mHelper = BluetoothConnectionHelper.createClient(UUID.fromString(APP_UUID), device);
        mHelper.setMessageReceiver(mListener );
        mHelper.connect();
		
		mDegreeText = (TextView) findViewById(R.id.degree);
		
		mSeekBar = (CircularSeekBar) findViewById(R.id.seekBar1);
		mSeekBar.setEnabled(false);
		mSeekBar.setOnSeekBarChangeListener(new OnCircularSeekBarChangeListener() {


			@Override
			public void onProgressChanged(CircularSeekBar circularSeekBar,
					int progress, boolean fromUser) {
				if (fromUser) {
	                if (mDegreeText != null) {
	                    mDegreeText.setText(String.valueOf(progress));
	                }
	               if (!mHandler.hasMessages(0)) {
	                   Message msg = Message.obtain(mHandler, 0, progress, 0);
	                   mHandler.sendMessageDelayed(msg, 100);
	               }
				}
			}

			@Override
			public void onStopTrackingTouch(CircularSeekBar seekBar) {
			    mHelper.sendMessage(String.valueOf(seekBar.getProgress()));
			}

			@Override
			public void onStartTrackingTouch(CircularSeekBar seekBar) {
			}
		});
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHelper.close();
	}

}
