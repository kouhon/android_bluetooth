/**
 * BluetoothManager
 * 
 * created by k.honda
 * copyright (c) 2013 Kouichi Honda All rights reserved.
 */
package com.komegu.bluetooth;

import java.util.ArrayList;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Bluetooth管理クラス
 * @author kouichi
 *
 */
public class BluetoothManager
{
	private final String TAG_LOG = "BluetoothManager";
	private BluetoothAdapter mBluetoothAdapter;
	private ArrayList<BluetoothDevice> mBluetoothDeviceList;
	private BluetoothInterface mBluetoothInterface;
	
	private final BroadcastReceiver mBluetoothDeviceFoundReceiver = new BroadcastReceiver()
	{
		/*
		 * (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		public void onReceive(Context context, Intent intent)
		{
			final String LOG_TAG = "BluetoothDeviceFoundReceiver";
			String action = intent.getAction();
			Log.d(LOG_TAG, "action:"+action);
			if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action) == true)
			{
				procBluetoothDiscoveryStarted();
			}
			if(BluetoothDevice.ACTION_FOUND.equals(action) == true)
			{
				procBluetoothDeviceFound(intent);
			}
			if(BluetoothDevice.ACTION_NAME_CHANGED.equals(action) == true)
			{
				procBluetoothDeviceFound(intent);
			}
			if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action) == true)
			{
				procBluetoothDiscoveryFinished();
			}
		}
	};
	
	private void procBluetoothDiscoveryStarted()
	{
		this.clearBluetoothDeviceList();
		this.mBluetoothDeviceList = new ArrayList<BluetoothDevice>();
	}
	
	private void procBluetoothDeviceFound(Intent intent)
	{
		BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		if(bluetoothDevice.getName() == null)
		{
			return;
		}
		Log.d(TAG_LOG, "device name is " + bluetoothDevice.getName());
		Log.d(TAG_LOG, "device address is " + bluetoothDevice.getAddress());
		if(this.mBluetoothDeviceList == null)
		{
			return;
		}
		this.mBluetoothDeviceList.add(bluetoothDevice);
	}
	
	private void procBluetoothDiscoveryFinished()
	{
		if(this.mBluetoothInterface == null)
		{
			return;
		}
		this.mBluetoothInterface.onBluetoothDeviceDiscoveryFinished();
	}
		
	/**
	 * コンストラクタ
	 */
	public BluetoothManager(Context context, BluetoothInterface bluetoothInterface)
	{
		this.mBluetoothInterface = bluetoothInterface;
		this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		context.registerReceiver(this.mBluetoothDeviceFoundReceiver, filter);
		filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		context.registerReceiver(this.mBluetoothDeviceFoundReceiver, filter);
		filter = new IntentFilter(BluetoothDevice.ACTION_NAME_CHANGED);
		context.registerReceiver(this.mBluetoothDeviceFoundReceiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		context.registerReceiver(this.mBluetoothDeviceFoundReceiver, filter);
	}
	
	/**
	 * 後処理
	 */
	public void terminate(Context context)
	{
		if(this.mBluetoothInterface != null)
		{
			this.mBluetoothInterface = null;
		}
		if(this.mBluetoothAdapter != null)
		{
			this.mBluetoothAdapter = null;
		}
		this.clearBluetoothDeviceList();
		context.unregisterReceiver(this.mBluetoothDeviceFoundReceiver);
	}
	
	private void clearBluetoothDeviceList()
	{
		if(this.mBluetoothDeviceList == null)
		{
			return;
		}
		int count = this.mBluetoothDeviceList.size();
		for(int i = 0; i < count; i++)
		{
			this.mBluetoothDeviceList.set(i, null);
		}
		this.mBluetoothDeviceList.clear();
		this.mBluetoothDeviceList = null;
	}
	
	/**
	 * Bluetoothに端末が対応しているか
	 * @return true:対応　false:未対応
	 */
	public boolean isBlutoothSupported()
	{
		if(this.mBluetoothAdapter == null)
		{
			return false;
		}
		return true;
	}
	
	/**
	 * BluetoothがONになっているか
	 * @return true:ON　false:OFF
	 */
	public boolean isBlutoothEnabled()
	{
		if(this.mBluetoothAdapter == null)
		{
			return false;
		}
		return this.mBluetoothAdapter.isEnabled();
	}
	
	/**
	 * 接続したことのあるデバイス情報を取得する
	 * @return　デバイスコレクション　
	 */
	public Set<BluetoothDevice> getBondedDevices()
	{
		if(this.mBluetoothAdapter == null)
		{
			return null;
		}
		return this.mBluetoothAdapter.getBondedDevices();
	}
	
	/**
	 * Bluetooth端末の検索を開始する
	 * @return
	 */
	public boolean startBluetoothDeviceDiscovery()
	{
		if(this.mBluetoothAdapter == null)
		{
			return false;
		}
		
		return this.mBluetoothAdapter.startDiscovery();
	}
	
	/**
	 * Bluetooth端末の再検索を開始する
	 */
	public boolean reStartBluetoothDeviceDiscovery()
	{
		if(this.mBluetoothAdapter == null)
		{
			return false;
		}
		
		this.cancelBluetoothDeviceDiscovery();
		return this.mBluetoothAdapter.startDiscovery();
	}
	
	/**
	 * Bluetooth端末の検索をキャンセルする
	 */
	public void cancelBluetoothDeviceDiscovery()
	{
		if(this.mBluetoothAdapter == null)
		{
			return;
		}
		
		if(this.mBluetoothAdapter.isDiscovering() == false)
		{
			return;
		}
		this.mBluetoothAdapter.cancelDiscovery();
	}
	
	/**
	 * 自デバイスのBluetooth検出を有効にするダイアログを表示する
	 * @param context
	 * @param discoverableDuration
	 */
	public void showRequestBluetoothDiscoverable(Context context, int discoverableDuration)
	{
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, discoverableDuration);
        context.startActivity(intent);
	}
	
}
