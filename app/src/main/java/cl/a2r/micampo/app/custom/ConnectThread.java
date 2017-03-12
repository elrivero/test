package cl.a2r.micampo.app.custom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class ConnectThread extends Thread {

	public static final int SUCCESS_CONNECT = 0;
	public static final int CONNECTION_FAILED = 3;
	public static final int RETRY_CONNECTION = 4;
    
	private final BluetoothSocket mmSocket;
	private final BluetoothDevice mmDevice;
	
    private UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    
    private static Handler mHandler;
    public static boolean blockRetries = false;
    private boolean imReconnecting = false;
    
    //--------------------------------------------------------------------------
    //----------------------CONEXION BASTONES RFID READER-----------------------
    //--------------------------------------------------------------------------
    
    public static void setHandler(Handler handler){
    	mHandler = handler;
    	ConnectedThread.setHandler(mHandler);
    }
    
    public ConnectThread(BluetoothDevice device, boolean imReconnecting) {
    	this.mmDevice = device;
    	this.imReconnecting = imReconnecting;
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) { }
        mmSocket = tmp;
    }
    
    public void run() {
        // Cancel discovery because it will slow down the connection
    	mBluetoothAdapter.cancelDiscovery();
 
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
                if (imReconnecting == false){
                	mHandler.obtainMessage(CONNECTION_FAILED).sendToTarget();
                }else{
                	if (blockRetries == false){
                		mHandler.obtainMessage(RETRY_CONNECTION, mmDevice).sendToTarget();
                	}
                }
            } catch (IOException closeException) { }
            return;
        }
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(mmSocket);
        list.add(mmDevice);
        mHandler.obtainMessage(SUCCESS_CONNECT, list).sendToTarget();
    }
	
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
    
}
