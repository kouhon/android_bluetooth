/**
 * MainActivity
 * 
 * created by k.honda
 * copyright (c) 2013 Kouichi Honda All rights reserved.
 */
package com.komegu.bluetooth;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements BluetoothInterface
{
	private BluetoothManager mBluetoothManager;
	private TextView mBluetoothSupportedTextView;
	private TextView mBluetoothEnabledTextView;
	private Button mBluetoothDeviceDiscoveryStartBtn;
	private Button mBluetoothDeviceDiscoveryCancelBtn;
	private Button mShowBluetoothDiscoverableDurationBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.mBluetoothSupportedTextView = (TextView)findViewById(R.id.bluetoothSupportedTextView);
		this.mBluetoothEnabledTextView = (TextView)findViewById(R.id.bluetoothEnabledTextView);
		this.mBluetoothDeviceDiscoveryStartBtn = (Button)findViewById(R.id.bluetoothStartDiscoveryBtn);
		this.mBluetoothDeviceDiscoveryStartBtn.setOnClickListener(this.makeBluetoothDeviceDiscoveryStartBtnOnClickListener());
		this.mBluetoothDeviceDiscoveryCancelBtn = (Button)findViewById(R.id.bluetoothCancelDiscoveryBtn);
		this.mBluetoothDeviceDiscoveryCancelBtn.setOnClickListener(this.makeBluetoothDeviceDiscoveryCancelOnClickListener());
		this.mShowBluetoothDiscoverableDurationBtn = (Button)findViewById(R.id.showBluetoothDiscoverableDurationBtn);
		this.mShowBluetoothDiscoverableDurationBtn.setOnClickListener(this.makeShowBluetoothDiscoverableDurationOnClickListener());
		
		this.mBluetoothManager = new BluetoothManager(this.getApplicationContext(), this);
		if(this.mBluetoothManager.isBlutoothSupported())
		{
			this.mBluetoothSupportedTextView.setText("Bluetooth is Supported.");
		}
		else
		{
			this.mBluetoothSupportedTextView.setText("Bluetooth is not Supported.");
		}
		
		if(this.mBluetoothManager.isBlutoothEnabled())
		{
			this.mBluetoothEnabledTextView.setText("Bluetooth is enabled.");
		}
		else
		{
			this.mBluetoothEnabledTextView.setText("Bluetooth is disabled.");
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			this.startActivity(intent);
		}
	}
	
	private OnClickListener makeBluetoothDeviceDiscoveryStartBtnOnClickListener()
	{
		return new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mBluetoothManager.startBluetoothDeviceDiscovery();
			}
		};
	}
	
	private OnClickListener makeBluetoothDeviceDiscoveryCancelOnClickListener()
	{
		return new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mBluetoothManager.cancelBluetoothDeviceDiscovery();
			}
		};
	}
	
	private OnClickListener makeShowBluetoothDiscoverableDurationOnClickListener()
	{
		return new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mBluetoothManager.showRequestBluetoothDiscoverable(v.getContext(), 300);
			}
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	protected void onDestroy()
	{
		super.onDestroy();
		this.mBluetoothManager.terminate(this.getApplicationContext());
		this.mBluetoothManager = null;
		this.mBluetoothDeviceDiscoveryCancelBtn = null;
		this.mBluetoothDeviceDiscoveryStartBtn = null;
		this.mBluetoothEnabledTextView = null;
		this.mBluetoothSupportedTextView = null;
		this.mShowBluetoothDiscoverableDurationBtn = null;
	}

	@Override
	public void onBluetoothDeviceDiscoveryFinished()
	{
		new AlertDialog.Builder(this)
		.setTitle("Bluetooth端末の検索が終了しました")
		.setMessage("Bluetooth端末の検索が終了しました")
		.setCancelable(false)
		.setPositiveButton("OK", null)
		.show();
	}
}
