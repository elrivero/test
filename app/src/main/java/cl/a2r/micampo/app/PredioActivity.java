package cl.a2r.micampo.app;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cl.a2r.common.AppException;
import cl.a2r.micampo.R;
import cl.a2r.micampo.app.custom.GoogleSignInActivity;
import cl.a2r.micampo.app.service.AppService;
import cl.a2r.micampo.app.service.PredioService;
import cl.a2r.sip.model.Predio;
import cl.a2r.sip.wsservice.WSAutorizacionCliente;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PredioActivity extends GoogleSignInActivity {

    private LinearLayout llForm;
    private Button fabNext;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity_predio);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //Actualizar controles en pantalla

        TextView tvApp = (TextView) findViewById(R.id.tvApp);
        tvApp.setText(getResources().getText(R.string.app_name));
        TextView tvAppVersion = (TextView) findViewById(R.id.tvAppVersion);
        tvAppVersion.setText("Versión " + AppService.getAppVersion());

        TextView tvWellcome = (TextView) findViewById(R.id.tvWellcome);
        tvWellcome.setText(getResources().getString(R.string.app_wellcome));

        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserName.setText(AppService.getUserName());

        //FloatingActionButton fabNext = (FloatingActionButton) findViewById(R.id.fabNext);
        fabNext = (Button) findViewById(R.id.fabNext);
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(PredioActivity.this, DashboardActivity.class);
            startActivity(intent);
            }
        });

        llForm = (LinearLayout) findViewById(R.id.llForm);
        loading = (ProgressBar) findViewById(R.id.loading);

        AppService.setContext(getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();

        //Cargar predios
        loadPredios();
    }

    /**
     * Carga listado de Predios y lo asocia al spinner con adapter
     */
    protected void loadPredios() {

        llForm.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);

        if (AppService.getPredios() != null && AppService.getPredios().size() > 0) {
            //Predios previamente cargados
            setSpinners();
            if (!AppService.hayInternet()) {
                Toast.makeText(getApplicationContext(), "No hay conexión a Internet, se trabajará con los datos guardados en el teléfono.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            //Cargar Predios de AppSQLite
            PredioService.setContext(getApplicationContext());
            List<Predio> _listPredios = PredioService.getAll();
            if (_listPredios != null && _listPredios.size() > 0) {
                AppService.setPredios(_listPredios);
                setSpinners();
                if (!AppService.hayInternet()) {
                    Toast.makeText(getApplicationContext(), "No hay conexión a Internet, se trabajará con los datos guardados en el teléfono.", Toast.LENGTH_LONG).show();
                }
            }
            else {
                //Cargar de WebEervice
                loadPrediosAsync _load = new loadPrediosAsync();
                _load.execute();
            }
        }
    }

    /**
     * Buscar listado de predios via WebService
     */
    protected class loadPrediosAsync extends AsyncTask<Void, Void, Void> {
        String title, msg;
        List<Predio> _listPredios;

        protected void onPreExecute() {
            title = "";
            msg = "";
        }

        protected Void doInBackground(Void... arg0) {
            try {

                Thread.sleep(100);

                if (!AppService.hayInternet()) {
                    throw new RuntimeException("No hay conexión a Internet. No se pueden obtener los datos del servidor.");
                }

                _listPredios = new ArrayList<>();
                //Insertar prompt
                Predio item = new Predio();
                item.setId(-1);
                item.setCodigo("");
                item.setNombre(" Seleccione Predio");
                _listPredios.add(item);

                //Buscar predios
                List<Predio> _list = WSAutorizacionCliente.traePredios();
                if (_list != null && _list.size() > 0) {
                    _listPredios.addAll(_list);
                }

            } catch (AppException e) {

                title = "Error";
                msg = e.getMessage();

            } catch (InterruptedException e) {

                title = "Error";
                msg = e.getMessage();
            }

            return null;
        }

        protected void onPostExecute(Void result){
            if (title.equals("Error")){
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                return;
            }

            if (_listPredios != null && _listPredios.size() > 0) {
                //Actualizar listado de Predios en AppService
                AppService.setPredios(_listPredios);

                //Guardar listado en tabla AppSQLite
                PredioService.insert(_listPredios);

                //Cargar Spinners
                setSpinners();
            }
        }
    }

    /**
     * Asocia spinner de Predios con listado por adapter
     */
    protected void setSpinners() {
        //Spinner Clientes
        List<Predio> _clientes = new ArrayList<>();
        Predio _cliente = new Predio();
        _cliente.setId(1);
        _cliente.setCodigo("1");
        _cliente.setNombre("Chilterra");
        _clientes.add(_cliente);

        //ArrayAdapter<Predio> dataAdapterClientes = new ArrayAdapter<Predio>(PredioActivity.this, R.layout.spinner_item, _clientes);
        //dataAdapterClientes.setDropDownViewResource(R.layout.spinner_dropdown_item);

        ArrayAdapter<Predio> dataAdapterClientes = new ArrayAdapter<Predio>(PredioActivity.this, android.R.layout.simple_spinner_item, _clientes);
        dataAdapterClientes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinnerCliente = (Spinner) findViewById(R.id.spinner_cliente);
        spinnerCliente.setAdapter(dataAdapterClientes);
        spinnerCliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//                Predio _predio = (Predio) adapterView.getItemAtPosition(position);
//                if (_predio.getId() != null) {
//                    AppService.setPredioId(_predio.getId());
//                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        //Spinner Predios
        //ArrayAdapter<Predio> dataAdapterPredios = new ArrayAdapter<Predio>(PredioActivity.this, R.layout.spinner_item, AppService.getPredios());
        //dataAdapterPredios.setDropDownViewResource(R.layout.spinner_dropdown_item);

        ArrayAdapter<Predio> dataAdapterPredios = new ArrayAdapter<Predio>(PredioActivity.this, android.R.layout.simple_spinner_item, AppService.getPredios());
        dataAdapterPredios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinnerPredio = (Spinner) findViewById(R.id.spinner_predio);
        spinnerPredio.setAdapter(dataAdapterPredios);
        spinnerPredio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                fabNext.setEnabled(false);
                Predio _predio = (Predio) adapterView.getItemAtPosition(position);
                if (_predio.getId() != null) {
                    AppService.setContext(getApplicationContext());
                    if (AppService.getId() == 0) {
                        //Si no esta cargada cargar de AppSQLite
                        AppService.load();

                        //Verificar si esta llena en AppSQLite
                        if (AppService.getId() == 0) {
                            //Si vacia en AppSQLite guardar configuracion por defecto
                            AppService.setDefaults();
                            AppService.insert();
                        }
                    }

                    AppService.setPredioId(_predio.getId());
                    AppService.update();
                    fabNext.setEnabled(_predio.getId() > 0 ? true : false);
                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        fabNext.setEnabled(false);
        spinnerPredio.setSelection(0);

        llForm.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
    }

    private Integer getSpinnerPredioSelection() {
        return 0;

//        Integer i = 0;
//        for (Predio item: PredioService.getAll()) {
//            if (item.getId() == AppService.getPredioId()) {
//                return i;
//            }
//            i++;
//        }
//
//        return 0;
    }
}
