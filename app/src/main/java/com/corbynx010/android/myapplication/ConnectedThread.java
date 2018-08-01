package com.corbynx010.android.myapplication;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;

public class ConnectedThread extends Thread {
    private final InputStream mmInStream;
    private final String TAG = "TAG";

    public ConnectedThread(BluetoothSocket socket) {
        Log.d(TAG, "ConnectedThread: Starting.");
        InputStream tmpIn = null;
        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mmInStream = tmpIn;
    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // We will need to sit in this method for the whole program unless an error occurs
        while (true) {
            // Read from the InputStream
            try {
                bytes = mmInStream.read(buffer);
                final String incomingMessage = new String(buffer, 0, bytes);
                runOnUiThread(new Runnable() {              //If this doesn't work as an external class the entire Connected thread will have to be put in mainMethod just before the final "}"
                    public void run() {
                        /**call all of the front end methods from here that need to happen when a pedal happens
                         * eg...
                         * if(incomingMessage == "1"){
                         * moveForward();
                         * }
                         */
                    }});
            } catch (IOException e) {
                Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
                break;
            }
        }
    }
}
