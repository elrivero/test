package cl.a2r.micampo.app.custom;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class ConnectedThread extends Thread{

	public static final int MESSAGE_READ = 1;
	public static final int CONNECTION_INTERRUPTED = 2;
	
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final InputStream mmInStream;
    
    private boolean manualDisconnect = false;
    private static Handler mHandler;
    
    //--------------------------------------------------------------------------
    //--------------------COMUNUCACION RFID READER - ANDROID--------------------
    //--------------------------------------------------------------------------
    
    public static void setHandler(Handler handler){
    	mHandler = handler;
    }
 
    public ConnectedThread(BluetoothSocket socket, BluetoothDevice device) {
    	mmDevice = device;
        mmSocket = socket;
        InputStream tmpIn = null;
 
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) { }
 
        mmInStream = tmpIn;
       
    }
 
    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                byte[] tempdata = new byte[bytes];
                System.arraycopy(buffer, 0, tempdata, 0, bytes);
                String tempEID = "";
				try {
					tempEID = new String(tempdata, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				//Junta todo lo que envia el baston en 0,2 segundos
                if (parseData){
                	EID = EID + tempEID;
                }else{
                	EID = "";
                	EID = EID + tempEID;
                	resetParseEID();
                }
            } catch (IOException e) {
            	if (manualDisconnect == false){
            		mHandler.obtainMessage(CONNECTION_INTERRUPTED, mmDevice).sendToTarget();
            	}
                break;
            }
        }
    }
 
    /* Call this from the main activity to shutdown the connection */
    public boolean cancel() {
        try {
        	manualDisconnect = true;
            mmSocket.close();
        } catch (IOException e) { }
        return true;
    }
    
    //--------------------------------------------------------------------------
    //--------------CONCATENACION DE LOS DATOS QUE ENVIA EL BASTON--------------
    //----------------------------EN 0,2 SEGUNDOS-------------------------------
    
    private static final int READ_TIME = 200;
    private boolean parseData = false;
    private String EID = "";
    
    private Handler parseHandler = new Handler(){
    };

    private Runnable parseCallback = new Runnable() {
        @Override
        public void run() {
        	//Cuando se terminan los 0,2 segundos, envia al Activity correspondiente el EID.
        	parseData = false;
        	deleteChars();
        	mHandler.obtainMessage(MESSAGE_READ, EID).sendToTarget();
        }
    };
    
    private void resetParseEID(){
    	parseData = true;
    	parseHandler.removeCallbacks(parseCallback);
    	parseHandler.postDelayed(parseCallback, READ_TIME);
    }
    
    private void deleteChars(){
    	int i = 0;
    	while (i < EID.length()){
    		if (Character.isLetter(EID.charAt(i))){
    			EID = EID.substring(i + 1, EID.length() - 1);
    			i--;
    		}
    		i++;
    	}
    }
    
    public String toString(){
    	return mmDevice.getName() + "\n" + mmDevice.getAddress();
    }
    
}
