package com.corbynx010.android.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

public class Bluetooth {
    private final String TAG = "TAG";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;

    /** Constructor checks that the device is paired to the arduino
     * Then makes a connection
     * Finally starts the ConnectedThread
     */
    public Bluetooth() {
        Log.d(TAG, "Bluetooth: start");
        if (BTinit()) {
            Log.d(TAG, "Bluetooth: BTinit Success");
            if (BTconnect()) {
                Log.d(TAG, "Bluetooth: BTconnect Success, Connection opened");
            }

        }
    }

    public void run(){
        ConnectedThread mConnectedThread = new ConnectedThread(socket);         //constructs a ConnectedThread
        mConnectedThread.start();                                               //Starts the ConnectedThread run() method
    }

    private boolean BTinit() {
        Log.d(TAG, "BTinit: start");
        final String DEVICE_ADDRESS = "98:D3:81:F9:65:A3";
        boolean found = false;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.d(TAG, "BTinit: btAdapter fail, device doesn't support bt");
        }
        try {
            Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
            if (bondedDevices.isEmpty()) {
                Log.d(TAG, "BTinit: No bonded devices");
            } else {
                for (BluetoothDevice iterator : bondedDevices) {
                    Log.d(TAG, "BTinit: Itterator address: " + iterator.toString() + "Device address: " + DEVICE_ADDRESS);
                    if (iterator.getAddress().equals(DEVICE_ADDRESS)) {
                        device = iterator;
                        found = true;
                        Log.d(TAG, "BTinit: device " + DEVICE_ADDRESS + "bonded");
                        break;
                    }
                }
            }
        }
        catch (NullPointerException e){
            Log.d(TAG, "BTinit: No paired devices, " + e.getMessage());
        }
        return found;
    }

    private boolean BTconnect() {
        Log.d(TAG, "BTconnect: start");
        boolean connected = true;
        BluetoothSocket tmp = null;

        try {
            tmp = device.createInsecureRfcommSocketToServiceRecord(PORT_UUID);
            Log.d(TAG, "BTconnect: rfcommsocket success");
        } catch (IOException e) {
            Log.d(TAG, "BTconnect: rfcommsocket fail");
        }
        socket = tmp;

        try {
            socket.connect();
            Log.d(TAG, "BTconnect: socket connect success");
        } catch (IOException e) {
            Log.d(TAG, "BTconnect: socket connect fail " + e.getMessage());
            try {
                socket.close();
            } catch (IOException e1) {
                Log.d(TAG, "BTconnect: " + e1.getMessage());
            }
            connected = false;
        }
        catch (NullPointerException e){
            Log.d(TAG, "BTconnect: not paired, " + e.getMessage());
        }
        if (connected) {
            try {
                InputStream inputStream = socket.getInputStream();
                Log.d(TAG, "BTconnect: InputStream received");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "BTconnect: InputStream not received " + e.toString());
            }
        }
        return connected;
    }
}
