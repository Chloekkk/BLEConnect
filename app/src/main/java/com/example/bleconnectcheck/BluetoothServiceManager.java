package com.example.bleconnectcheck;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BluetoothServiceManager {
    private static final UUID MY_UUID =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    // insecure 이 뭔지 모르겠음
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    // 따로 부호 지원해주는게 좋을 거 같음
    // errorNum
    private static final int STATUS_NONE = 0;
    private static final int BLUETOOTHDEVICE_NONE = 1;
    private static final int BLUETOOTHDEVICE_OFF = 2;

    private int status = 0;

    private ConnectThread connectThread;
    private AcceptThread acceptThread;

    private BluetoothAdapter bluetoothAdapter;

    public BluetoothServiceManager(Context context) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.status = STATUS_NONE;

        if(bluetoothAdapter==null){
            this.status = BLUETOOTHDEVICE_NONE;
            Log.d("error", "기기가 블루투스를 지원하지 않음");
        }
        if(!bluetoothAdapter.isEnabled()){
            this.status = BLUETOOTHDEVICE_OFF;
            Log.d("error", "기기의 블루투스가 켜져있지 않음");
        }
    }

    public void initialize(){
        Log.d("socket", "initialize");
        if (this.connectThread != null) {
            this.connectThread.cancel();
            this.connectThread = null;
        }

        if (this.acceptThread == null){
            this.acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    public void deviceDiscover(){
        this.bluetoothAdapter.startDiscovery();
    }

    public void getPairedDevices(){
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                // 출력을 해주던가 말던가
            }
        }
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("bluetoothServer", MY_UUID);
            } catch (IOException e) {
                Log.e("socket", "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e("socket", "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    Log.d("socket", "socket connect success!");
                    Log.d("socket", String.valueOf(socket.getConnectionType()));
                    Log.d("socket", String.valueOf(socket.getRemoteDevice().getAddress()));
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e("socket", "Could not close the connect socket", e);
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e("socket", "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e("socket", "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
//            manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("socket", "Could not close the client socket", e);
            }
        }
    }
}
