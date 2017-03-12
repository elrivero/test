package cl.a2r.micampo.postparto;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cl.a2r.common.AppException;
import cl.a2r.micampo.app.CalculadoraActivity;
import cl.a2r.micampo.R;
import cl.a2r.micampo.app.model.MangadaDetalle;
import cl.a2r.micampo.app.service.AppService;
import cl.a2r.micampo.app.DiiosActivity;
import cl.a2r.micampo.app.custom.ConnectThread;
import cl.a2r.micampo.app.custom.ConnectedThread;
import cl.a2r.micampo.app.custom.ShowAlert;
import cl.a2r.micampo.app.model.Mangada;
import cl.a2r.micampo.app.service.MangadaDetalleService;
import cl.a2r.micampo.app.service.MangadaService;
import cl.a2r.micampo.model.revpostparto.Diagnostico;
import cl.a2r.micampo.model.revpostparto.GanRevPostParto;
import cl.a2r.micampo.postparto.service.DiagnosticoService;
import cl.a2r.micampo.postparto.service.MedControlService;
import cl.a2r.micampo.postparto.model.Postparto;
import cl.a2r.micampo.postparto.service.PostpartoService;
import cl.a2r.micampo.wsservice.WSRevPostPartoCliente;
import cl.a2r.sip.model.Ganado;
import cl.a2r.sip.model.MedicamentoControl;
import cl.a2r.sip.wsservice.WSGanadoCliente;

public class PostpartoRegistroActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final int REQUEST_CODE_CALCULADORA = 9;

    private Integer actividadid = 0;

    //Objeto visualizado actualmente como candidato
    private Postparto postpartoActual;
    private Integer diio;
    private String eid;

    private LinearLayout llDIIO;
    private TextView tvDiio;

    private LinearLayout llCandidatos;
    private TextView tvCandidatos;
    private LinearLayout llProcesados;
    private TextView tvProcesados;

    private LinearLayout llEstado;
    private TextView tvEstado;

    private LinearLayout llInfo;
    private ImageView ivInfo;
    private TextView tvInfo;

    private ImageView ivSearch;

    private LinearLayout llForm;

    private LinearLayout llDiagnostico;
    private TextView tvDiagnostico;
    private Spinner spinnerDiagnostico;
    private ArrayAdapter<Diagnostico> diagnosticoAdapter;

    private LinearLayout llTratamiento;
    private TextView tvTratamiento;
    private Spinner spinnerMedControl;
    private ArrayAdapter<MedicamentoControl> medControlAdapter;

    private LinearLayout llMangada;
    private TextView tvProcesadosMangadas, tvMangada, tvAnimalesMangada;
    private FloatingActionButton fabSave, fabSaveBusqueda, fabCloseMangada;

    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postparto_activity_registro);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.setResult(RESULT_OK);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        actividadid = AppService.ACTIVIDAD_POSTPARTO_REGISTRO;
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            actividadid = extras.getInt("actividadid");
        }

        llDIIO = (LinearLayout) findViewById(R.id.llDIIO);
        llDIIO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(PostpartoRegistroActivity.this, CalculadoraActivity.class);
            intent.putExtra("title", getResources().getString(R.string.title_activity_postparto_registro));
            startActivityForResult(intent, REQUEST_CODE_CALCULADORA);
            }
        });

        tvDiio = (TextView) findViewById(R.id.tvDiio);

        llCandidatos = (LinearLayout) findViewById(R.id.llCandidatos);
        llCandidatos.setOnClickListener(this);
        tvCandidatos = (TextView) findViewById(R.id.tvCandidatos);

        llProcesados = (LinearLayout) findViewById(R.id.llProcesados);
        llProcesados.setOnClickListener(this);
        tvProcesados = (TextView) findViewById(R.id.tvProcesados);

        llEstado = (LinearLayout) findViewById(R.id.llEstado);
        tvEstado = (TextView) findViewById(R.id.tvEstado);

        llInfo = (LinearLayout) findViewById(R.id.llInfo);
        llInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarAnterior();
            }
        });
        ivInfo = (ImageView) findViewById(R.id.ivInfo);
        tvInfo = (TextView) findViewById(R.id.tvInfo);

        ivSearch = (ImageView) findViewById(R.id.ivSearch);

        llForm = (LinearLayout) findViewById(R.id.llForm);

        llDiagnostico = (LinearLayout) findViewById(R.id.llDiagnostico);
        tvDiagnostico = (TextView) findViewById(R.id.tvDiagnostico);
        spinnerDiagnostico = (Spinner) findViewById(R.id.spinner_diagnostico);
        spinnerDiagnostico.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                spinnerMedControl.setEnabled(false);
                if (postpartoActual.getGanPostparto() != null) {
                    Diagnostico _diag = (Diagnostico) adapterView.getItemAtPosition(position);
                    postpartoActual.getGanPostparto().setIdDiagnostico(_diag.getId());
                    postpartoActual.getGanPostparto().setDiagnostico(_diag.toString());

                    spinnerMedControl.setEnabled(true);
                    if (_diag.getId() == PostpartoService.POSTPARTO_DIAGNOSTICO_SELECCIONE) {
                        //Sin diagnostico, poner Seleccionar Medcontrol
                        spinnerMedControl.setSelection(0); //SELECCIONE
                        spinnerMedControl.setEnabled(false);
                    } else if (_diag.getId() == PostpartoService.POSTPARTO_DIAGNOSTICO_NORMAL) {
                        //Si Diagnostico Normal, poner MedControl 1
                        spinnerMedControl.setSelection(1); //SIN TRATAMIENTO
                        spinnerMedControl.setEnabled(false);
                    }
                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        llTratamiento = (LinearLayout) findViewById(R.id.llTratamiento);
        tvTratamiento = (TextView) findViewById(R.id.tvTratamiento);
        spinnerMedControl = (Spinner) findViewById(R.id.spinner_medcontrol);
        spinnerMedControl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                MedicamentoControl _med = (MedicamentoControl) adapterView.getItemAtPosition(position);
                postpartoActual.getGanPostparto().setIdMedControl(_med.getId());
                postpartoActual.getGanPostparto().setMedicamento(_med.toString());
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        llMangada = (LinearLayout) findViewById(R.id.llMangada);
        tvProcesadosMangadas = (TextView) findViewById(R.id.tvProcesadosMangadas);
        tvMangada = (TextView) findViewById(R.id.tvMangada);
        tvAnimalesMangada = (TextView) findViewById(R.id.tvAnimalesMangada);
        fabCloseMangada = (FloatingActionButton) findViewById(R.id.fabCloseMangada);

        fabSave = (FloatingActionButton) findViewById(R.id.fabSave);
        fabSaveBusqueda = (FloatingActionButton) findViewById(R.id.fabSaveBusqueda);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarDatosAsync();
            }
        });
        fabSaveBusqueda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarDatosAsync();
            }
        });

        fabCloseMangada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarMangada();
            }
        });

        loading = (ProgressBar) findViewById(R.id.loading);

        //Inicializar contexto en Servicios
        AppService.setContext(getApplicationContext());
        MangadaService.setContext(getApplicationContext());
        MangadaDetalleService.setContext(getApplicationContext());
        PostpartoService.setContext(getApplicationContext());
        DiagnosticoService.setContext(getApplicationContext());
        MedControlService.setContext(getApplicationContext());

        //Cargar datos via WebService
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();

        //Handler para Blutooth de baston
        ConnectThread.setHandler(mHandler);
    }

    /**
     * RESOLVIENDO NAVEGACION ATRAS AL DASHBOARD
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Navegacion a listados de DIIOs
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.llCandidatos:
                List<Postparto> listC = PostpartoService.getCandidatos(AppService.getPredioId(), actividadid);
                if (listC != null && listC.size() > 0) {
                    Intent intent = new Intent(getApplicationContext(), DiiosActivity.class);
                    intent.putExtra("title", "Candidatas");
                    intent.putExtra("list", PostpartoService.getDiios(listC, actividadid));
                    startActivityForResult(intent, AppService.REQUEST_CODE_DIIOS);
                }
                break;

            case R.id.llProcesados:
                List<Postparto> listP = PostpartoService.getProcesados(AppService.getPredioId(), actividadid);
                if (listP != null && listP.size() > 0) {
                    Intent intent = new Intent(getApplicationContext(), DiiosActivity.class);
                    intent.putExtra("title", "Procesados");
                    intent.putExtra("list", PostpartoService.getDiios(listP, actividadid));
                    startActivityForResult(intent, AppService.REQUEST_CODE_DIIOS);
                }
                break;
        }
    }

    /**
     * CUANDO REGRESA DE LA CALCULADORA
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CALCULADORA:
            case AppService.REQUEST_CODE_DIIOS:

                if (resultCode == RESULT_OK) {
                    Bundle res = data.getExtras();
                    String _diio = res.getString("diio");
                    String _eid = res.getString("eid");

                    Postparto item = null;
                    diio = 0;
                    eid = "";

                    //Validar DIIO
                    if (_diio != null) {
                        //Procesar DIIO
                        diio = Integer.parseInt(_diio);
                        item = PostpartoService.getCandidatoPorDiio(diio);
                    }
                    else if (_eid != null) {
                        //Procesar EID
                        eid = _eid;
                        item = PostpartoService.getCandidatoPorEid(eid);
                    }
                    else {
                        //Ni DIIO ni EID
                        Toast.makeText(getApplicationContext(), "Ganado no encontrado en la base de datos", Toast.LENGTH_LONG);
                    }

                    //Se encontro el Ganado, mostralo en pantalla
                    if (item != null) {

                        postpartoActual = item;
                        diio = postpartoActual.getDiio();
                        eid = postpartoActual.getEid();

                        //Guardar directo si esta en modo Busqueda
                        if (actividadid == AppService.ACTIVIDAD_POSTPARTO_BUSQUEDA) {

                            if (!MangadaDetalleService.getProcesado(AppService.getPredioId(), AppService.ACTIVIDAD_POSTPARTO_BUSQUEDA, item.getGanPostparto().getIdGanado())) {

                                Mangada mangada = MangadaService.getAbiertaByPredioActividad(AppService.getPredioId(), actividadid);

                                postpartoActual.setSincronizado(1);
                                postpartoActual.setMangada(mangada.getId());
                                postpartoActual.getGanPostparto().setIdFundo(AppService.getPredioId());
                                PostpartoService.update(postpartoActual);

                                MangadaDetalleService.insert(new MangadaDetalle(null, mangada.getId(), postpartoActual.getGanPostparto().getIdGanado()));
                            }
                        }

                        refreshControls();

                    } else {
                        //Buscar Ganado en WebService
                        loadGanadoAsync _load = new loadGanadoAsync();
                        _load.execute();
                    }
                }
                break;
        }
    }

    /**
     * Buscar datos en SQLite
     */
    protected void loadData() {

        List<Diagnostico> listDiagnostico = DiagnosticoService.getAll();
        if (listDiagnostico != null && listDiagnostico.size() > 0) {
            diagnosticoAdapter = new ArrayAdapter<Diagnostico>(PostpartoRegistroActivity.this, android.R.layout.simple_spinner_item, listDiagnostico);
            diagnosticoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDiagnostico.setAdapter(diagnosticoAdapter);
        }

        List<MedicamentoControl> listMedControl = MedControlService.getAll();
        if (listMedControl != null && listMedControl.size() > 0) {
            medControlAdapter = new ArrayAdapter<MedicamentoControl>(PostpartoRegistroActivity.this, android.R.layout.simple_spinner_item, listMedControl);
            medControlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerMedControl.setAdapter(medControlAdapter);
        }

        clearData();
        refreshControls();
    }

    /**
     * Refresca el contenido de la actividadid dependiendo de los atributos del objeto postpartoActual
     */
    private void refreshControls() {

        Mangada mangada = MangadaService.getAbiertaByPredioActividad(AppService.getPredioId(), actividadid);

        if (postpartoActual != null && postpartoActual.getGanPostparto() != null && AppService.getPredio() != null && AppService.getPredioId() > 0) {
            postpartoActual.getGanPostparto().setIdFundo(AppService.getPredioId());
        }

        //DIIO escrito en app_calculadora que no se encntro ningun Ganado
        tvDiio.setText(PostpartoService.getDiio(diio));

        //Encontrados, Procesdos
        tvCandidatos.setText(String.format("%1$d", PostpartoService.countCandidatos(AppService.getPredioId(), actividadid)));
        Integer countProcesados = MangadaDetalleService.countByPredioActividad(AppService.getPredioId(), actividadid);
        tvProcesados.setText(String.format("%1$d", countProcesados));

        //Texto de mensaje
        llEstado.setVisibility(postpartoActual.getMensaje().length() > 0 ? View.VISIBLE : View.GONE);
        tvEstado.setText(postpartoActual.getMensaje());

        //Candidato y no procesado
        if (postpartoActual.getCandidato() == 1 && (actividadid == AppService.ACTIVIDAD_POSTPARTO_BUSQUEDA || !MangadaDetalleService.getProcesado(AppService.getPredioId(), actividadid, postpartoActual.getGanPostparto().getIdGanado()))) {
            //Candidato a procesar
            tvEstado.setTextColor(Color.WHITE);
            tvEstado.setBackgroundColor(
                actividadid == AppService.ACTIVIDAD_POSTPARTO_REGISTRO ?
                getResources().getColor(R.color.accent) :
                    getResources().getColor(R.color.darkorange));
        }
        else if (!postpartoActual.getMensaje().isEmpty()) {
            tvEstado.setTextColor(Color.WHITE);
            tvEstado.setBackgroundColor(Color.RED);
            if (postpartoActual.getMangada() > 0) {
                if (actividadid == AppService.ACTIVIDAD_POSTPARTO_REGISTRO) {
                    tvEstado.setText("No es candidata");
                }
            }
        }

        llInfo.setVisibility(postpartoActual.getDiio() > 0 ? View.VISIBLE : View.GONE);
        if (postpartoActual.getDiio() > 0) {
            ivInfo.setVisibility(View.GONE);
            if (postpartoActual.getGanPostparto().getIdDiagnostico() == PostpartoService.POSTPARTO_DIAGNOSTICO_NORMAL) {
                //De alta
                ivInfo.setVisibility(View.VISIBLE);
                tvInfo.setText(String.format("De alta hace %1$d días", AppService.getDaysAgo(postpartoActual.getGanPostparto().getFechaControl())));
            } else if (postpartoActual.getGanPostparto().getCodRevision() == 1) {
                //Primera revisión
                tvInfo.setText(String.format("Parto %1$s hace %2$d días", postpartoActual.getTipoParto(), AppService.getDaysAgo(postpartoActual.getGanPostparto().getFechaParto())));
            } else if (postpartoActual.getGanPostparto().getCodRevision() > 1 && AppService.getDaysAgo(postpartoActual.getGanPostparto().getFechaControl()) > 0) {
                //Tratamiento
                ivInfo.setVisibility(View.GONE);
                tvInfo.setText(String.format("Tratamiento hace %1$d días", AppService.getDaysAgo(postpartoActual.getGanPostparto().getFechaControl())));
            }
            else if (postpartoActual.getGanPostparto().getCodRevision() > 1 && AppService.getDaysAgo(postpartoActual.getGanPostparto().getFechaControl()) <= 0) {
                //procesado hoy
                //buscar tratamiento anterior
                ivInfo.setVisibility(View.VISIBLE);
                Postparto anterior = PostpartoService.getCandidatoPorDiioPenultimo(postpartoActual.getDiio());
                if (anterior.getGanPostparto() != null && anterior.getGanPostparto().getFechaControl() != null) {
                    tvInfo.setText(String.format("Tratamiento hace %1$d días", AppService.getDaysAgo(anterior.getGanPostparto().getFechaControl())));
                }
            }
        }

        ivSearch.setVisibility(actividadid == AppService.ACTIVIDAD_POSTPARTO_BUSQUEDA ? View.VISIBLE : View.GONE);

        llForm.setVisibility(
            actividadid == AppService.ACTIVIDAD_POSTPARTO_REGISTRO
            && postpartoActual.getDiio() > 0
            && postpartoActual.getGanPostparto() != null
            && postpartoActual.getGanPostparto().getCodRevision() > 0
            ? View.VISIBLE : View.GONE);

        tvDiagnostico.setVisibility(
            postpartoActual.getCandidato() == 0 || MangadaDetalleService.getProcesado(AppService.getPredioId(), actividadid, postpartoActual.getGanPostparto().getIdGanado())
            ? View.VISIBLE : View.GONE);
        spinnerDiagnostico.setSelection(getSpinnerDiagnosticoSelection());
        spinnerDiagnostico.setEnabled(
            postpartoActual.getDiio() > 0
            && postpartoActual.getGanPostparto() != null
            && postpartoActual.getGanPostparto().getIdDiagnostico() == 0
            && postpartoActual.getGanPostparto().getIdMedControl() == 0
        );

        tvTratamiento.setVisibility(
            postpartoActual.getCandidato() == 0 || MangadaDetalleService.getProcesado(AppService.getPredioId(), actividadid, postpartoActual.getGanPostparto().getIdGanado())
            ? View.VISIBLE : View.GONE);
        spinnerMedControl.setSelection(getSpinnerMedControlSelection());
        spinnerMedControl.setEnabled(
            postpartoActual.getDiio() > 0
            && postpartoActual.getGanPostparto() != null
            && postpartoActual.getGanPostparto().getIdDiagnostico() == 0
            && postpartoActual.getGanPostparto().getIdMedControl() == 0
        );

        fabSave.setVisibility(
            actividadid == AppService.ACTIVIDAD_POSTPARTO_REGISTRO
            && postpartoActual.getDiio() > 0
            && postpartoActual.getGanPostparto() != null
            && postpartoActual.getGanPostparto().getIdDiagnostico() == 0
            && postpartoActual.getGanPostparto().getIdMedControl() == 0
            ? View.VISIBLE : View.GONE
        );

        tvProcesadosMangadas.setText(tvProcesados.getText());
        //tvMangada.setText(String.format("%1$d", mangada.getNumero()));
        tvMangada.setText(String.format("%1$d", mangada.getNumero()));
        //Integer countMangada = PostpartoService.countMangadaActual(AppService.getPredioId(), mangada.getNumero());
        Integer countMangada = mangada.getDetalles() != null ? mangada.getDetalles().size() : 0;
        tvAnimalesMangada.setText(String.format("%1$d", countMangada));
        fabCloseMangada.setVisibility(countMangada > 0 ? View.VISIBLE : View.GONE);
    }

    /**
     * Tomar posicion actual del spinner de Diagnostico segun diagnosticoId en postpartoActual
     * @return
     */
    private Integer getSpinnerDiagnosticoSelection() {
        if (postpartoActual.getCandidato() == 0 || MangadaDetalleService.getProcesado(AppService.getPredioId(), actividadid, postpartoActual.getGanPostparto().getIdGanado())) {
            Integer i = 0;
            for (Diagnostico item : DiagnosticoService.getAll()) {
                if (item.getId() == postpartoActual.getGanPostparto().getIdDiagnostico()) {
                    return i;
                }
                i++;
            }
        }

        return 0;
    }

    /**
     * Tomar posicion actual del spinner de Medicamento segun medicamentoId en postpartoActual
     * @return
     */
    private Integer getSpinnerMedControlSelection() {
        if (postpartoActual.getCandidato() == 0 || MangadaDetalleService.getProcesado(AppService.getPredioId(), actividadid, postpartoActual.getGanPostparto().getIdGanado())) {
            Integer i = 0;
            for (MedicamentoControl item : MedControlService.getAll()) {
                if (item.getId() == postpartoActual.getGanPostparto().getIdMedControl()) {
                    return i;
                }
                i++;
            }
        }

        return 0;
    }

    /**
     * Inicializar datos de postpartoActual despues de alguns operaciones
     */
    private void clearData() {
        diio = 0;
        eid = "";

        postpartoActual = new Postparto();
        postpartoActual.setSincronizado(0);
        postpartoActual.setMangada(0);
        postpartoActual.setCandidato(0);

        GanRevPostParto ganPostparto = new GanRevPostParto();
        ganPostparto.setDiio(diio);
        ganPostparto.setEid(eid);
        ganPostparto.setCandidato(false);
        ganPostparto.setCodRevision(0);
        ganPostparto.setIdDiagnostico(-1);
        ganPostparto.setIdMedControl(-1);
        postpartoActual.setGanPostparto(ganPostparto);
    }

    /**
     * Buscar GANADO fuera de la lista de candidatos via WebService
     */
    protected class loadGanadoAsync extends AsyncTask<Void, Void, Void> {
        String title, msg;
        Ganado ganado;

        protected void onPreExecute() {
            title = "";
            msg = "";
        }

        protected Void doInBackground(Void... arg0) {
            try {

                Thread.sleep(100);

                if (!AppService.hayInternet()) {
                    throw new RuntimeException("No hay conexión a Internet");
                }

                //Buscar ganado
                if (diio > 0) {
                    List<Ganado> _list = WSGanadoCliente.traeGanado(diio);
                    if (_list != null && _list.size() > 0) {
                        ganado = _list.get(0);
                        if (ganado.getActiva().toUpperCase() == "N") {
                            ganado = null;
                        }
                    }
                } else if (eid != null && eid.length() > 0) {
                    List<Ganado> _list = WSGanadoCliente.traeGanadoBaston(eid);
                    if (_list != null && _list.size() > 0) {
                        ganado = _list.get(0);
                        if (ganado.getActiva().toUpperCase() == "N") {
                            ganado = null;
                        }
                    }
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

        protected void onPostExecute(Void result) {
            if (title.equals("Error")) {
                ShowAlert.showAlert("Revise", msg, PostpartoRegistroActivity.this);
                return;
            }

            Integer _diio = diio;
            String _eid = eid;
            clearData();

            if (ganado != null) {
                diio = ganado.getDiio();
                eid = ganado.getEid();
                postpartoActual.getGanPostparto().setDiio(ganado.getDiio());
                //TODO El eid anterior queda null porque el metodo del webservice no trae eid lleno
                postpartoActual.getGanPostparto().setEid(ganado.getEid());
                postpartoActual.getGanPostparto().setIdFundo(AppService.getPredioId());

                postpartoActual.getGanPostparto().setMensaje("Ganado sin parto registrado");

                //Si esta en Busqueda hay que guardar este Ganado para que quede procesado en las mangadas
                if (actividadid == AppService.ACTIVIDAD_POSTPARTO_BUSQUEDA) {

                    //Buscar mangada actual
                    Mangada mangada = MangadaService.getAbiertaByPredioActividad(AppService.getPredioId(), actividadid);

                    postpartoActual.setSincronizado(1);
                    postpartoActual.setCandidato(0);
                    postpartoActual.setMangada(mangada.getId());
                    postpartoActual.getGanPostparto().setCandidato(false);
                    postpartoActual.getGanPostparto().setIdTipoParto(0);
                    postpartoActual.getGanPostparto().setFechaControl(new Date());
                    postpartoActual.getGanPostparto().setIdGanado(ganado.getId());
                    postpartoActual.getGanPostparto().setIdUsuario(AppService.getUserId());
                    PostpartoService.updateOrInsert(postpartoActual);

                    //Asociar Ganado a Mangada actual
                    MangadaDetalleService.insert(new MangadaDetalle(null, mangada.getId(), postpartoActual.getGanPostparto().getIdGanado()));
                }
            }
            else {
                diio = _diio;
                eid = _eid;
                postpartoActual.getGanPostparto().setMensaje("Ganado no encontrado \n en base de datos");
            }

            refreshControls();
            //ShowAlert.showAlert("Revise", "DIIO " + diio.toString() + " no encontrado en base de datos.", PostpartoRegistroActivity.this);
        }
    }

    /**
     * Guardar datos en SQLite
     */
    private void guardarDatosAsync() {
        new AsyncTask<Void, Void, Void>() {
            String title, msg;

            protected void onPreExecute() {
                //loading.setVisibility(View.VISIBLE);
                title = "";
                msg = "";
                fabSave.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);

                //Guardar el Postparto actual y verificar si hubo error
                msg = updatePostparto();
                if (msg.length() > 0) {
                    title = "Error";
                }
            }

            protected Void doInBackground(Void... arg0) {
                try {

                    Thread.sleep(100);

                    //Si no hubo error al guardar ultio registro en SQLite
                    if (title.length() == 0) {
                        //buscar todos los pendientes de sincronizacion y mandar a guardar en PostgreSQL
                        List<Postparto> noSincronizados = PostpartoService.getNoSincronizados();
                        if (noSincronizados != null && noSincronizados.size() > 0) {

                            if (!AppService.hayInternet()) {
                                title = "Mensaje";
                                msg = String.format("Registro guardado localmente. Hay %1$d registros locales.", noSincronizados.size());
                                return null;
                            }

                            //Preparar lista de GanRevPostParto a guardar
                            List<GanRevPostParto> list = new ArrayList<GanRevPostParto>();
                            for (Postparto item: noSincronizados) {
                                if (item.getGanPostparto() != null) {
                                    if (item.getGanPostparto().getIdMedControl() == 0) {
                                        item.getGanPostparto().setIdMedControl(null);
                                        item.getGanPostparto().setMedicamento("");
                                    }
                                    list.add(item.getGanPostparto());
                                }
                            }

                            //Verificar si lista llena
                            if (list != null && list.size() > 0) {
                                //Guardar lista en postgresql
                                if (actividadid == AppService.ACTIVIDAD_POSTPARTO_REGISTRO) {
                                    WSRevPostPartoCliente.insertaRevPostParto(list);
                                }

                                //Si guardo bien en PostgreSQL, actualizar atributo Sincronizado
                                for (Postparto item: noSincronizados) {
                                    //Poner como sincronizado
                                    item.setSincronizado(1);
                                    //Actualizar en SQLite
                                    PostpartoService.update(item);
                                }
                            }
                        }
                    }
                }
                catch (AppException e) {

                    title = "Error";
                    msg = e.getMessage();

                }
                catch (InterruptedException e) {

                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(Void result){

                loading.setVisibility(View.INVISIBLE);

                if (title.equals("Error")){
                    //ShowAlert.showAlert(title, msg, getApplicationContext());
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    fabSave.setVisibility(View.VISIBLE);
                    return;
                }

                if (title.equals("Mensaje")){
                    //ShowAlert.showAlert(title, msg, getApplicationContext());
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    clearData();
                    refreshControls();
                    return;
                }

                try {

                    Toast.makeText(getApplicationContext(), String.format("Datos guardados para DIIO %1$s", diio), Toast.LENGTH_LONG).show();

                    //Crear nuevo Postparto con indicacion de nuevo tratamiento
                    if (postpartoActual.getGanPostparto().getIdDiagnostico() != PostpartoService.POSTPARTO_DIAGNOSTICO_NORMAL) {
                        insertPostparto();
                    }

                    //Inicializar datos, limpiar pantalla
                    clearData();
                    refreshControls();
                }
                catch (Exception e){
                    //ShowAlert.showAlert("ERROR", e.getMessage(), getApplicationContext());
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    /**
     * Guarda en SQLite el Postparto en edicion
     * @return mensaje de error si hubo error, "" si guardo OK
     */
    private String updatePostparto() {
        String mensaje = postpartoActual.getMensaje();
        try {

            Diagnostico diagnostico = (Diagnostico) spinnerDiagnostico.getSelectedItem();
            if (diagnostico == null || diagnostico.getId() == -1) {
                return "Debe seleccionar un diagnóstico.";
            }
            postpartoActual.getGanPostparto().setIdDiagnostico(diagnostico.getId());

            MedicamentoControl medControl = (MedicamentoControl) spinnerMedControl.getSelectedItem();
            if (medControl == null || medControl.getId() == -1) {
                return "Debe seleccionar un tratamiento.";
            }
            postpartoActual.getGanPostparto().setIdMedControl(medControl.getId());

            if (postpartoActual.getGanPostparto().getIdDiagnostico().intValue() != PostpartoService.POSTPARTO_DIAGNOSTICO_NORMAL.intValue() && postpartoActual.getGanPostparto().getIdMedControl() == 0) {
                return "Debe seleccionar otro tratamiento.";
            }

            postpartoActual.setSincronizado(0);

            //Actualizar postpartoActual con datos en formulario
            postpartoActual.getGanPostparto().setFechaControl(new Date());
            postpartoActual.getGanPostparto().setIdUsuario(AppService.getUserId());
            postpartoActual.getGanPostparto().setIdFundo(AppService.getPredioId());


            if (postpartoActual.getGanPostparto().getIdMedControl() == 0) {
                postpartoActual.getGanPostparto().setIdMedControl(null);
                postpartoActual.getGanPostparto().setMedicamento("");
            }

            //Tomar mangada abierta, si no hay ninguna abierta
            Mangada mangada = MangadaService.getAbiertaByPredioActividad(AppService.getPredioId(), actividadid);
            postpartoActual.setMangada(mangada.getId());

            //Guardar en SQLite
            PostpartoService.update(postpartoActual);

            //Asociar Ganado a Mangada
            MangadaDetalleService.insert(new MangadaDetalle(null, mangada.getId(), postpartoActual.getGanPostparto().getIdGanado()));

            return "";
        }
        catch (Exception e) {
            //Toast.makeText(getApplicationContext(), "Error al guardar en SQLite.", Toast.LENGTH_LONG);
            return "Error al guardar en SQLite.";
        }
    }
    
    /**
     * Crea nuevo registro Postparto como indicacion del nuevo tratamiento
     * @return mensaje de error si hubo error, "" si guardo OK
     */
    private String insertPostparto() {
        try {

            Date proximaSemana = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(proximaSemana);
            cal.add(Calendar.DATE, 7); // 10 is the days you want to add or subtract

            postpartoActual.getGanPostparto().setCodRevision(postpartoActual.getGanPostparto().getCodRevision() + 1);
            postpartoActual.getGanPostparto().setFechaControl(cal.getTime());
            postpartoActual.getGanPostparto().setIdFundo(AppService.getPredioId());
            postpartoActual.getGanPostparto().setMensaje(postpartoActual.getGanPostparto().getCodRevision() == 2 ? "Revisión procesada" : "Tratamiento procesado");
            postpartoActual.setMangada(0);
            postpartoActual.setId(0);

            //Guardar en SQLite
            PostpartoService.insert(postpartoActual);

            return "";
        }
        catch (Exception e) {

            //Toast.makeText(getApplicationContext(), "Error al guardar en SQLite.", Toast.LENGTH_LONG);
            return "Error al crear nuevo tratamiento en SQLite.";
        }
    }

    /**
     * Muestra un dialogo para cerrar la mangada actual si ciadra
     */
    private void cerrarMangada() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set up the input
        final EditText input = new EditText(this);

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setTitle(R.string.mangada_title);
        builder.setMessage(R.string.mangada_message);
        builder.setPositiveButton(R.string.mangada_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });

        builder.setNegativeButton(R.string.mangada_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            try {
                Integer userMangada = Integer.parseInt(input.getText().toString());
                Mangada mangada = MangadaService.getAbiertaByPredioActividad(AppService.getPredioId(), actividadid);
                int countMangada = mangada.getDetalles() != null && mangada.getDetalles().size() > 0 ? mangada.getDetalles().size() : 0;
                if (userMangada == countMangada) {

                    mangada.setEstadoId(MangadaService.ESTADO_CERRADA);
                    MangadaService.update(mangada);

                    clearData();
                    refreshControls();

                } else {

                    ShowAlert.showAlert("Error", getString(R.string.mangada_error), PostpartoRegistroActivity.this);
                }
            } catch (NumberFormatException e) {

                ShowAlert.showAlert("Error", getString(R.string.mangada_number_validation), PostpartoRegistroActivity.this);

            }
//                catch (AppException e){
//
//                    ShowAlert.showAlert("Error", e.getMessage(), PostpartoRegistroActivity.this);
//                }
            }
        });

        builder.show();
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
                diio = 0;
                eid = "";

                //Validar DIIO
                if (_eid.isEmpty()) {

                    Toast.makeText(getApplicationContext(), "EID " + _eid + " vacio.", Toast.LENGTH_LONG);
                    refreshControls();

                } else {

                    eid = _eid;
                    Postparto item = PostpartoService.getCandidatoPorEid(eid);
                    if (item != null) {
                        postpartoActual = item;
                        diio = postpartoActual.getDiio();
                        eid = postpartoActual.getEid();
                        refreshControls();
                    } else {
                        //Buscar Ganado en WebService
                        loadGanadoAsync _load = new loadGanadoAsync();
                        _load.execute();
                    }
                }

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

    private void mostrarAnterior() {
        Postparto postpartoAnterior = PostpartoService.getCandidatoPorDiioPenultimo(postpartoActual.getDiio());
        if (postpartoAnterior != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Tratamiento anterior");
            builder.setMessage(String.format(
                "Fecha: %1$s\n" +
                "Diagnóstico: %2$s\n" +
                "Tratamiento: %3$s"
                , AppService.dateToString(postpartoAnterior.getGanPostparto().getFechaControl())
                , postpartoAnterior.getGanPostparto().getDiagnostico()
                , postpartoAnterior.getGanPostparto().getMedicamento()
            ));
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //do things
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

}
