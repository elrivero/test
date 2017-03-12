package cl.a2r.micampo.app;

import java.util.List;

import cl.a2r.micampo.R;
import cl.a2r.micampo.app.custom.ConnectThread;
import cl.a2r.micampo.app.custom.ConnectedThread;
import cl.a2r.micampo.app.custom.ShowAlert;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class CalculadoraActivity extends AppCompatActivity implements View.OnClickListener {

	private CoordinatorLayout coordinatorLayout;
	private Toolbar toolbar;
	
	private TextView tvdiio;
	private Button uno, dos, tres, cuatro, cinco, seis, siete, ocho, nueve, cero;
	private FloatingActionButton fabOk;
	private ImageButton fbDelete;
	private boolean diioInverso;
	private String diioActual, diioActualInverso;
	
	public static int diio;
	private String errMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_activity_calculadora);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null){
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);

		Intent intent = getIntent();
		if (intent.getExtras() != null && intent.getStringExtra("title") != null) {
			setTitle(intent.getStringExtra("title").toString());
		}

		cargarInterfaz();
	}

	public void onResume() {
		super.onResume();

		//Handler para Blutooth de baston
		ConnectThread.setHandler(mHandler);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.app_calculadora, menu);
		return true;
	}

	public void refresh(MenuItem item){
		diio = 0;
		diioActual = "";
		diioActualInverso = "";
		diioInverso = false;
		tvdiio.setText("");
	}

	private void cargarInterfaz(){
		tvdiio = (TextView)findViewById(R.id.diio);
		diioInverso = false;

		uno = (Button)findViewById(R.id.uno);
		uno.setOnClickListener(this);
		dos = (Button)findViewById(R.id.dos);
		dos.setOnClickListener(this);
		tres = (Button)findViewById(R.id.tres);
		tres.setOnClickListener(this);
		cuatro = (Button)findViewById(R.id.cuatro);
		cuatro.setOnClickListener(this);
		cinco = (Button)findViewById(R.id.cinco);
		cinco.setOnClickListener(this);
		seis = (Button)findViewById(R.id.seis);
		seis.setOnClickListener(this);
		siete = (Button)findViewById(R.id.siete);
		siete.setOnClickListener(this);
		ocho = (Button)findViewById(R.id.ocho);
		ocho.setOnClickListener(this);
		nueve = (Button)findViewById(R.id.nueve);
		nueve.setOnClickListener(this);
		cero = (Button)findViewById(R.id.cero);
		cero.setOnClickListener(this);

		fbDelete = (ImageButton) findViewById(R.id.fbDelete);

		fabOk = (FloatingActionButton) findViewById(R.id.fabOk);
		fabOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				buttonOk();
			}
		});
	}
	
	public void onClick(View v) {
		int id = v.getId();
		switch (id){
			case R.id.uno:
				tvdiio.setText(tvdiio.getText() + "1");
				break;
			case R.id.dos:
				tvdiio.setText(tvdiio.getText() + "2");
				break;
			case R.id.tres:
				tvdiio.setText(tvdiio.getText() + "3");
				break;
			case R.id.cuatro:
				tvdiio.setText(tvdiio.getText() + "4");
				break;
			case R.id.cinco:
				tvdiio.setText(tvdiio.getText() + "5");
				break;
			case R.id.seis:
				tvdiio.setText(tvdiio.getText() + "6");
				break;
			case R.id.siete:
				tvdiio.setText(tvdiio.getText() + "7");
				break;
			case R.id.ocho:
				tvdiio.setText(tvdiio.getText() + "8");
				break;
			case R.id.nueve:
				tvdiio.setText(tvdiio.getText() + "9");
				break;
			case R.id.cero:
				tvdiio.setText(tvdiio.getText() + "0");
				break;

			case R.id.fabOk:
				buttonOk();
				break;

			case R.id.fbDelete:
				if (tvdiio.getText().length() > 0){
					tvdiio.setText(tvdiio.getText().toString().substring(0, tvdiio.getText().length() - 1));
				}
				break;
		}
	}

	private void buttonOk() {
		if (diioInverso == false && tvdiio.length() >= 1){

			Toast.makeText(CalculadoraActivity.this, R.string.calculadora_diio_reves, Toast.LENGTH_SHORT).show();

			diioInverso = true;
			diioActual = tvdiio.getText().toString();
			tvdiio.setText("");

		} else {

			if (diioInverso && tvdiio.length() >= 1){
				diioActualInverso = tvdiio.getText().toString();
				if (validarDiioInverso()){
					try {
						Bundle conData = new Bundle();
						conData.putString("diio", diioActual);
						Intent intent = new Intent();
						intent.putExtras(conData);
						setResult(RESULT_OK, intent);
						finish();
						return;
					}
//					catch (AppException e) {
//						ShowAlert.showAlert("Error", e.getMessage(), this);
//					}
					catch (Exception e) {
						ShowAlert.showAlert("Error", e.getMessage(), this);
					}

				} else {
					ShowAlert.showAlert("Error", "El DIIO ingresado no coincide con el original. Intente ingresar el diio al reves nuevamente", this);
				}
			} else {
				ShowAlert.showAlert("Error", "DIIO no valido. No escribio ningun DIIO", this);
			}
		}
	}

	private boolean validarDiioInverso(){
		if (diioActual.length() != diioActualInverso.length()){
			return false;
		}
		
		for (int i = 0; i < diioActual.length(); i++){
			if (diioActual.charAt(i) != diioActualInverso.charAt(diioActualInverso.length() - 1 - i)){
				return false;
			}
		}
		return true;
	}

	/**
	 * Handler para manipular la conexion Bluetooth con el Baston
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {

			case ConnectThread.SUCCESS_CONNECT:
				BluetoothSocket mmSocket = (BluetoothSocket) ((List<Object>) msg.obj).get(0);
				BluetoothDevice mmDevice = (BluetoothDevice) ((List<Object>) msg.obj).get(1);
				ConnectedThread connectedThread = new ConnectedThread(mmSocket, mmDevice);
				connectedThread.start();
				break;

			case ConnectedThread.MESSAGE_READ:
				String _eid = (String) msg.obj;
				_eid = _eid.trim();
				long temp = Long.parseLong(_eid);
				_eid = Long.toString(temp);

				Bundle conData = new Bundle();
				conData.putString("eid", _eid);
				Intent intent = new Intent();
				intent.putExtras(conData);
				setResult(RESULT_OK, intent);
				finish();
				break;

			case ConnectedThread.CONNECTION_INTERRUPTED:
				ShowAlert.askReconnect("Error", "Se perdió la conexión con el bastón. ¿Intentar reconectar?", getApplicationContext(), (BluetoothDevice) msg.obj);
				break;

			case ConnectThread.RETRY_CONNECTION:
				ConnectThread connectThread = new ConnectThread((BluetoothDevice) msg.obj, true);
				connectThread.start();
				break;
		}
		}
	};

}
