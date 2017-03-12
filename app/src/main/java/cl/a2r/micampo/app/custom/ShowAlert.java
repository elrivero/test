package cl.a2r.micampo.app.custom;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;

import cl.a2r.micampo.app.BastonActivity;

@SuppressWarnings("deprecation")
public class ShowAlert {

	public static void showAlert(String title, String msg, Context ctx){
		AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(msg);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

           public void onClick(DialogInterface dialog,int id) {
        	   dialog.dismiss();
           }
           
         });

		alertDialog.show();
	}
	
	public static void askReconnect(String title, String msg, Context ctx, BluetoothDevice device){
		final BluetoothDevice dev = device;
		AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(msg);
        alertDialog.setButton2("Si", new DialogInterface.OnClickListener() {

           public void onClick(DialogInterface dialog,int id) {
	   		   ConnectThread connectThread = new ConnectThread(dev, true);
	   		   connectThread.start();
        	   dialog.dismiss();
           }
           
        });
		
		alertDialog.setButton("No", new DialogInterface.OnClickListener() {

	           public void onClick(DialogInterface dialog,int id) {
	        	   dialog.dismiss();
	           }
	           
	    });

		alertDialog.show();
	}
	
	public static void askYesNo(String title, String msg, Context ctx, OnClickListener listener){
		AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(msg);
	    alertDialog.setButton2("Si", listener);
		alertDialog.setButton("No", listener);
		alertDialog.show();
	}
	
	public static void askBaston(String title, String msg, Context ctx){
		final Context context = ctx;
		AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(msg);
	    alertDialog.setButton2("Si", new DialogInterface.OnClickListener() {
	
	       public void onClick(DialogInterface dialog,int id) {
	    	   Intent i = new Intent(context, BastonActivity.class);
	    	   context.startActivity(i);
	    	   dialog.dismiss();
	       }
	       
	    });
		
		alertDialog.setButton("No", new DialogInterface.OnClickListener() {
	
	           public void onClick(DialogInterface dialog,int id) {
	        	    //Aplicaciones.createSession();
		       		//Intent i = new Intent(context, AppLauncher.getAppClass());
		       		//context.startActivity(i);
		       		//dialog.dismiss();
	           }
	           
	    });
	
		alertDialog.show();
	}
	
	public static void multipleChoice(String title, String msg, String[] items, boolean[] checked, Context ctx, OnMultiChoiceClickListener listener, OnClickListener okListener){
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(title);
		//builder.setMessage(msg);
		builder.setMultiChoiceItems(items, checked, listener);
		final AlertDialog alert = builder.create();
		alert.setButton2("OK", okListener);
		alert.setButton("Cancelar", okListener);
		alert.show();
	}
}
